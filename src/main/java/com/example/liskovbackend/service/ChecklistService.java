package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.checklist.request.ChecklistGenerateRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.*;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final PropertyRepository propertyRepository;
    private final ChecklistItemRepository checklistItemRepository;
    private final GptOssService gptOssService;

    @Transactional
    public ChecklistGenerateResponse generateChecklist(ChecklistGenerateRequest request) {
        var property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        var gptRequest = GptChecklistGenerateRequest.builder()
                .propertyId(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .propertyType(property.getPropertyType())
                .floor(property.getFloor())
                .builtYear(property.getBuiltYear())
                .area(property.getArea())
                .build();

        var result = gptOssService.generateChecklist(gptRequest).block();

        if (result == null) {
            throw new AiNoResponseException("체크리스트가 생성되지 않았습니다.");
        }

        return result;
    }

    @Transactional
    public ChecklistSaveResponse saveChecklist(ChecklistSaveRequest request) {
        log.info("[ChecklistSave] START request={}", request);

        log.info("[ChecklistSave] propertyId={}", request.propertyId());

        var property = propertyRepository.findById(request.propertyId())
            .orElseThrow(() -> {
                log.error("[ChecklistSave] Property NOT FOUND propertyId={}", request.propertyId());
                return new ResourceNotFoundException("매물을 찾을 수 없습니다.");
            });

        log.info("[ChecklistSave] Property FOUND id={}", property.getId());

        checklistRepository.findByPropertyId(property.getId()).ifPresent(it -> {
            log.error("[ChecklistSave] Checklist already exists propertyId={}, checklistId={}",
                property.getId(), it.getId());
            throw new ResourceAlreadyExistsException("매물에 대한 체크리스트가 이미 존재합니다.");
        });

        log.info("[ChecklistSave] No existing checklist for propertyId={}", property.getId());

        var checklist = Checklist.builder()
            .property(property)
            .build();

        log.info("[ChecklistSave] Checklist entity created (not saved)");

        request.items().forEach(itemDto -> {
            log.info("[ChecklistSave] Adding item content={}, severity={}, memo={}",
                itemDto.content(), itemDto.severity(), itemDto.memo());

            checklist.addItem(
                ChecklistItem.builder()
                    .content(itemDto.content())
                    .severity(Severity.valueOf(itemDto.severity()))
                    .memo(itemDto.memo())
                    .build()
            );
        });

        log.info("[ChecklistSave] Total items added={}", checklist.getItems().size());

        var savedChecklist = checklistRepository.save(checklist);

        log.info("[ChecklistSave] Checklist SAVED checklistId={}", savedChecklist.getId());

        log.info("[ChecklistSave] END checklistId={}, itemCount={}",
            savedChecklist.getId(), savedChecklist.getItems().size());

        return ChecklistSaveResponse.builder()
            .checklistId(savedChecklist.getId())
            .propertyId(property.getId())
            .itemCount(savedChecklist.getItems().size())
            .createdAt(savedChecklist.getCreatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public ChecklistGetResponse getChecklistById(Long id) {
        var checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if (checklist.getIsDeleted()) {
            throw new ResourceNotFoundException("삭제된 체크리스트입니다.");
        }

        var checklistItems = checklist.getItems();

        var items = checklistItems.stream()
                .map(item -> ChecklistItemGetResponse.builder()
                        .itemId(item.getId())
                        .content(item.getContent())
                        .severity(item.getSeverity())
                        .memo(item.getMemo()).build()
                ).collect(Collectors.toList());

        return ChecklistGetResponse.builder()
                .checklistId(checklist.getId())
                .propertyId(checklist.getProperty().getId())
                .name(checklist.getProperty().getName())
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AllChecklistGetResponse> getAllChecklist(Long userId) {
        var allChecklists = checklistRepository.findAll();

        return allChecklists.stream()
                .filter(checklist -> !checklist.getIsDeleted())
                .filter(checklist -> checklist.getProperty().getUser().getId().equals(userId))
                .map(checklist -> AllChecklistGetResponse.builder()
                        .checklistId(checklist.getId())
                        .propertyId(checklist.getProperty().getId())
                        .name(checklist.getProperty().getName())
                        .createdAt(checklist.getCreatedAt())
                        .itemCount(checklist.getItems().size())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteChecklist(Long id, Long userId) {
        var checklist = checklistRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if (!checklist.getProperty().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("체크리스트가 존재하지 않습니다.");
        }

        checklist.delete();
    }

    @Transactional
    public ChecklistUpdateResponse updateChecklist(Long checklistId, List<ChecklistUpdateRequest> requests, Long userId) {
        var checklist = checklistRepository.findByIdAndIsDeletedFalse(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다"));

        if (!checklist.getProperty().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("체크리스트가 존재하지 않습니다.");
        }

        var itemIds = requests.stream()
                .map(ChecklistUpdateRequest::getItemId)
                .toList();

        var items = checklistItemRepository.findAllById(itemIds);

        var itemMap = items.stream()
                .filter(item -> item.getChecklist().getId().equals(checklistId))
                .collect(Collectors.toMap(ChecklistItem::getId, Function.identity()));

        int changedCount = 0;

        for (ChecklistUpdateRequest req : requests) {
            var item = itemMap.get(req.getItemId());
            if (item == null) {
                throw new ResourceNotFoundException("해당 체크리스트에 항목이 존재하지 않습니다");
            }

            boolean changed = false;

            if (!Objects.equals(item.getMemo(), req.getMemo())) {
                item.updateMemo(req.getMemo());
                changed = true;
            }
            if (!Objects.equals(item.getSeverity(), req.getSeverity())) {
                item.updateSeverity(req.getSeverity());
                changed = true;
            }

            if (changed) changedCount++;
        }

        return ChecklistUpdateResponse.builder()
                .checklistId(checklist.getId())
                .propertyId(checklist.getProperty().getId())
                .updatedItemCount(changedCount)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

