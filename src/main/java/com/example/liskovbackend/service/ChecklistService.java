package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.*;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final PropertyRepository propertyRepository;
    private final ChecklistItemRepository checklistItemRepository;

    public ChecklistSaveResponse saveChecklist(ChecklistSaveRequest request){

        //매물 찾기 (request.getPropertyId())
        Optional<Property> optionalProperty = propertyRepository.findById(request.getPropertyId());

        if(optionalProperty.isEmpty()){
            throw new ResourceNotFoundException("매물을 찾을 수 없습니다. " + request.getPropertyId());
        }

        Property property = optionalProperty.get();

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

    public ChecklistGetResponse getChecklistById(Long id) {
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("체크리스트가 존재하지 않습니다."));

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
}
