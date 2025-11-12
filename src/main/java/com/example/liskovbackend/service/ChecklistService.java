package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.checklist.request.ChecklistGenerateRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistItemsSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.*;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
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

    //체크리스트 생성
    @Transactional
    public ChecklistGenerateResponse generateChecklist(ChecklistGenerateRequest request) {
        //매물 조회
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        //AI 요청 dto 생성
        GptChecklistGenerateRequest gptRequest = GptChecklistGenerateRequest.builder()
                .propertyId(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .propertyType(property.getPropertyType())
                .floor(property.getFloor())
                .buildYear(property.getBuildYear())
                .area(property.getArea())
                .availableDate(property.getAvailableDate())
                .build();

        //AI 체크리스트 생성 로직 호출
        Mono<ChecklistGenerateResponse> response = gptOssService.generateChecklist(gptRequest);

        if(response == null || response.block() == null){
            throw new AiNoResponseException("체크리스트가 생성되지 않았습니다.");
        }

        //응답 반환
        return response.block();
    }

    //체크리스트 저장
    @Transactional
    public ChecklistSaveResponse saveChecklist(ChecklistSaveRequest request){

        //매물 찾기 (request.getPropertyId())
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        if(property.getChecklists() != null){
            throw new ResourceAlreadyExistsException("매물에 대한 체크리스트가 이미 존재합니다.");
        }

        //매물에 대한 체크리스트 생성
        Checklist checklist = Checklist.builder()
                .property(property)
                .items(null)
                .build();

        //체크리스트 아이템 생성, 저장
        List<ChecklistItemsSaveRequest> itemsDto = request.getItems();

        List<ChecklistItem> savedChecklistItems = itemsDto.stream()
                .map(itemDto -> ChecklistItem.builder()
                        .checklist(checklist)
                        .content(itemDto.getContent())
                        .severity(itemDto.getSeverity())
                        .build())
                .collect(Collectors.toList());

        checklistItemRepository.saveAll(savedChecklistItems);

        //체크리스트 아이템을 체크리스트에 추가하고 저장
        checklist.setItems(savedChecklistItems);
        Checklist savedChecklist = checklistRepository.save(checklist);

        return ChecklistSaveResponse.builder()
                .checklistId(savedChecklist.getId())
                .propertyId(property.getId())
                .itemCount(savedChecklistItems.size())
                .createdAt(savedChecklist.getCreatedAt())
                .build();
    }

    //체크리스트 단건 조회
    @Transactional(readOnly = true)
    public ChecklistGetResponse getChecklistById(Long id) {
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if(checklist.getIsDeleted()){
            throw new ResourceNotFoundException("삭제된 체크리스트입니다.");
        }

        List<ChecklistItem> checklistItems = checklist.getItems();

        List<ChecklistItemGetResponse> items = checklistItems.stream()
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

    //체크리스트 전체 조회
    @Transactional(readOnly = true)
    public List<AllChecklistGetResponse> getAllChecklist() {
        List<Checklist> allChecklists = checklistRepository.findAll();

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

    //체크리스트 삭제
    @Transactional
    public void deleteChecklist(Long id) {
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if(checklist.getIsDeleted()){
            throw new ResourceNotFoundException("이미 삭제된 체크리스트입니다.");
        }

        checklist.delete();
    }

    //체크리스트 수정
    @Transactional
    public ChecklistUpdateResponse updateChecklist(Long checklistId, List<ChecklistUpdateRequest> requests) {
        Checklist checklist = checklistRepository.findById(checklistId)
            .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다. id=" + checklistId));

        if (checklist.getIsDeleted()) {
            throw new ResourceNotFoundException("삭제된 체크리스트입니다.");
        }

        List<Long> itemIds = requests.stream()
            .map(ChecklistUpdateRequest::getItemId)
            .toList();

        List<ChecklistItem> items = checklistItemRepository.findAllById(itemIds);

        Map<Long, ChecklistItem> itemMap = items.stream()
            .filter(item -> item.getChecklist().getId().equals(checklistId))
            .collect(Collectors.toMap(ChecklistItem::getId, Function.identity()));

        int changedCount = 0;

        for (ChecklistUpdateRequest req : requests) {
            ChecklistItem item = itemMap.get(req.getItemId());
            if (item == null) {
                throw new ResourceNotFoundException("해당 체크리스트에 항목이 존재하지 않습니다");
            }

            boolean changed = false;

            if (!Objects.equals(item.getMemo(), req.getMemo())) {
                item.setMemo(req.getMemo());
                changed = true;
            }
            if (!Objects.equals(item.getSeverity(), req.getSeverity())) {
                item.setSeverity(req.getSeverity());
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


