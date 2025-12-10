package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.checklist.request.ChecklistGenerateRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.*;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
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
                .buildYear(property.getBuildYear())
                .area(property.getArea())
                .availableDate(property.getAvailableDate())
                .build();

        var response = gptOssService.generateChecklist(gptRequest);

        if (response == null || response.block() == null) {
            throw new AiNoResponseException("체크리스트가 생성되지 않았습니다.");
        }

        return response.block();
    }

    @Transactional
    public ChecklistSaveResponse saveChecklist(ChecklistSaveRequest request) {
        var property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        if (property.getChecklists() != null) {
            throw new ResourceAlreadyExistsException("매물에 대한 체크리스트가 이미 존재합니다.");
        }

        var checklist = Checklist.builder()
                .property(property)
                .items(null)
                .build();

        var itemsDto = request.items();

        var savedChecklistItems = itemsDto.stream()
                .map(itemDto -> ChecklistItem.builder()
                        .checklist(checklist)
                        .content(itemDto.content())
                        .severity(itemDto.severity())
                        .build())
                .collect(Collectors.toList());

        checklistItemRepository.saveAll(savedChecklistItems);

        checklist.updateItems(savedChecklistItems);
        Checklist savedChecklist = checklistRepository.save(checklist);

        return ChecklistSaveResponse.builder()
                .checklistId(savedChecklist.getId())
                .propertyId(property.getId())
                .itemCount(savedChecklistItems.size())
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
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AllChecklistGetResponse> getAllChecklist() {
        var allChecklists = checklistRepository.findAll();

        return allChecklists.stream()
                .filter(checklist -> !checklist.getIsDeleted())
                .map(checklist -> AllChecklistGetResponse.builder()
                        .checklistId(checklist.getId())
                        .propertyId(checklist.getProperty().getId())
                        .createdAt(checklist.getCreatedAt())
                        .itemCount(checklist.getItems().size())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteChecklist(Long id) {
        var checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if (checklist.getIsDeleted()) {
            throw new ResourceNotFoundException("이미 삭제된 체크리스트입니다.");
        }

        checklist.delete();
    }

    @Transactional
    public ChecklistUpdateResponse updateChecklist(Long checklistId, List<ChecklistUpdateRequest> requests) {
        var checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다"));

        if (checklist.getIsDeleted()) {
            throw new ResourceNotFoundException("삭제된 체크리스트입니다.");
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

