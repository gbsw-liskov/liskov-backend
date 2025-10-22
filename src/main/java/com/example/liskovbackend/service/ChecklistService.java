package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.checklist.request.ChecklistItemsSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.*;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final PropertyRepository propertyRepository;
    private final ChecklistItemRepository checklistItemRepository;

    //체크리스트 저장
    @Transactional
    public ChecklistSaveResponse saveChecklist(ChecklistSaveRequest request){

        //매물 찾기 (request.getPropertyId())
        Optional<Property> optionalProperty = propertyRepository.findById(request.getPropertyId());

        if(optionalProperty.isEmpty()){
            throw new ResourceNotFoundException("매물을 찾을 수 없습니다. ");
        }

        Property property = optionalProperty.get();

        if(property.getChecklists() != null){
            throw new ResourceAlreadyExistsException("매물에 대한 체크리스트가 이미 존재합니다.");
        }

        List<ChecklistItemsSaveRequest> itemsDto = request.getItems();

        //매물에 대한 체크리스트 생성
        Checklist checklist = Checklist.builder()
                .property(property)
                .items(null)
                .build();


        //체크리스트 아이템 생성, 저장
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


        ChecklistGetResponse response = ChecklistGetResponse.builder()
                .checklistId(checklist.getId())
                .propertyId(checklist.getProperty().getId())
                .items(items)
                .build();

        return response;
    }

    @Transactional(readOnly = true)
    public List<AllChecklistGetResponse> getAllChecklist() {
        List<Checklist> allChecklists = checklistRepository.findAll();
        List<AllChecklistGetResponse> responses = allChecklists.stream()
                .filter(checklist -> !checklist.getIsDeleted())
                .map(checklist -> AllChecklistGetResponse.builder()
                        .checklistId(checklist.getId())
                        .propertyId(checklist.getProperty().getId())
                        .createdAt(checklist.getCreatedAt())
                        .itemCount(checklist.getItems().size())
                        .build()
                ).collect(Collectors.toList());

        return responses;
    }

    @Transactional
    public void deleteChecklist(Long id) {
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if(checklist.getIsDeleted()){
            throw new ResourceNotFoundException("이미 삭제된 체크리스트입니다.");
        }

        checklist.delete();
    }

    @Transactional
    public ChecklistUpdateResponse updateChecklist(Long id, List<ChecklistUpdateRequest> request) {
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

        if(checklist.getIsDeleted()){
            throw new ResourceNotFoundException("삭제된 체크리스트입니다.");
        }

        //수정된 items의 개수를 반환하기 위해 사용
        long changedCount = 0;

        long updatedCount = request.stream()
                .map(req -> {
                    //itemId로 ChecklistItem을 find한 후 item의 checklistId와 위에서 find한 Checklist의 id가 일치하는지 확인
                    ChecklistItem item = checklistItemRepository.findById(req.getItemId())
                            .filter(i -> i.getChecklist().getId().equals(id))
                            .orElseThrow(() -> new ResourceNotFoundException("해당 체크리스트에 항목이 존재하지 않습니다."));

                    //수정 되었는지를 확인하기 위함
                    boolean changed = false;

                    //memo 수정
                    if (!Objects.equals(item.getMemo(), req.getMemo())) {
                        item.setMemo(req.getMemo());
                        changed = true;
                    }

                    //severity 수정
                    if (!Objects.equals(item.getSeverity(), req.getSeverity())) {
                        item.setSeverity(req.getSeverity());
                        changed = true;
                    }

                    return changed;
                })
                .filter(Boolean::booleanValue)
                .count();

        return ChecklistUpdateResponse.builder()
                .checklistId(checklist.getId())
                .propertyId(checklist.getProperty().getId())
                .updatedItemCount(request.size())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
