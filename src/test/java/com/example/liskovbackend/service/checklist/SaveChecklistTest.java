package com.example.liskovbackend.service.checklist;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.example.liskovbackend.dto.checklist.request.ChecklistItemsSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.response.ChecklistSaveResponse;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import com.example.liskovbackend.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SaveChecklistTest {

    // Mock Repository 사용
    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ChecklistItemRepository checklistItemRepository;

    // Mock Repository들을 주입받는 테스트 대상 서비스
    @InjectMocks
    private ChecklistService checklistService;

    // 테스트 실행 전 Mock 초기화
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 시나리오 테스트 - 체크리스트 저장
    @Test
    void saveChecklist_success() {
        // given
        Long propertyId = 1L;
        // 매물 설정
        Property property = Property.builder().id(propertyId).build();

        // propertyRepository.findById()가 호출될 때 Optional.of(property) 반환하도록 설정
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // checklistRepository.save() 호출 시 전달된 객체 그대로 반환 (DB에 실제로 저장하지 않음)
        when(checklistRepository.save(any(Checklist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // checklistItemRepository.saveAll() 호출 시 전달된 리스트 그대로 반환
        when(checklistItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // 실제로 서비스에 전달할 요청 DTO 생성
        ChecklistSaveRequest request = ChecklistSaveRequest.builder()
                .propertyId(propertyId)
                .items(List.of(
                        ChecklistItemsSaveRequest.builder()
                                .content("Check plumbing")
                                .severity(Severity.DANGER)
                                .build(),
                        ChecklistItemsSaveRequest.builder()
                                .content("Check walls")
                                .severity(Severity.NONE)
                                .build()
                ))
                .build();

        // when
        // 테스트 대상 서비스 호출
        ChecklistSaveResponse response = checklistService.saveChecklist(request);

        // then
        // 반환값 검증
        assertNotNull(response);
        assertEquals(propertyId, response.getPropertyId());
        assertEquals(2, response.getItemCount());

        // mock들이 올바르게 호출되었는지 검증
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(checklistItemRepository, times(1)).saveAll(anyList());
        verify(checklistRepository, times(1)).save(any(Checklist.class));
    }

    // 예외 테스트 - ResourceNotFoundException
    @Test
    void saveChecklist_propertyNotFound() {
        // given
        Long propertyId = 1L;

        // DB에 매물이 없다고 가정 (Optional.empty 반환)
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // 빈 아이템 리스트를 가진 요청 DTO 생성
        ChecklistSaveRequest request = ChecklistSaveRequest.builder()
                .propertyId(propertyId)
                .items(List.of())
                .build();

        // then
        // PropertyNotFoundException이 발생해야 정상
        assertThrows(ResourceNotFoundException.class, () -> checklistService.saveChecklist(request));

        // repository 호출 검증
        verify(propertyRepository, times(1)).findById(propertyId);
        verifyNoInteractions(checklistRepository);
        verifyNoInteractions(checklistItemRepository);
    }

    @Test
    void saveChecklist_alreadyExists(){
        // given
        Long propertyId = 1L;
        // 이미 체크리스트가 있는 매물 설정
        Property property = Property.builder()
                .id(propertyId)
                .checklists(List.of(Checklist.builder().id(1L).build())) // 체크리스트 존재
                .build();

        // propertyRepository.findById()가 호출될 때 Optional.of(property) 반환
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // 요청 DTO 생성
        ChecklistSaveRequest request = ChecklistSaveRequest.builder()
                .propertyId(propertyId)
                .items(List.of(
                        ChecklistItemsSaveRequest.builder()
                                .content("Check plumbing")
                                .severity(Severity.DANGER)
                                .build()
                ))
                .build();

        // then
        // 이미 체크리스트가 있으므로 ResourceAlreadyExistsException 발생 확인
        assertThrows(ResourceAlreadyExistsException.class, () -> checklistService.saveChecklist(request));

        // repository 호출 검증
        verify(propertyRepository, times(1)).findById(propertyId);
        verifyNoInteractions(checklistRepository);
        verifyNoInteractions(checklistItemRepository);
    }
}
