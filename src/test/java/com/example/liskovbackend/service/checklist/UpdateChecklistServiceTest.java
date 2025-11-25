package com.example.liskovbackend.service.checklist;

import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.ChecklistUpdateResponse;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistItemRepository;
import com.example.liskovbackend.repository.ChecklistRepository;

import com.example.liskovbackend.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateChecklistServiceTest {

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ChecklistItemRepository checklistItemRepository;

    @InjectMocks
    private ChecklistService checklistService;

    private Checklist checklist;
    private Property property;
    private ChecklistItem item1;
    private ChecklistItem item2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        property = Property.builder().id(100L).build();

        checklist = Checklist.builder()
                .id(1L)
                .property(property)
                .build();

        item1 = ChecklistItem.builder()
                .id(10L)
                .memo("기존 메모")
                .severity(Severity.NORMAL)
                .checklist(checklist)
                .build();

        item2 = ChecklistItem.builder()
                .id(20L)
                .memo("기존 메모2")
                .severity(Severity.DANGER)
                .checklist(checklist)
                .build();
    }

    @Test
    //ChecklistService.updateChecklist 정상 작동 여부, 데이터 수정되는지 검증
    void updateChecklist_success() {
        // given
        ChecklistUpdateRequest req1 = new ChecklistUpdateRequest(10L, "수정된 메모", Severity.WARNING);
        ChecklistUpdateRequest req2 = new ChecklistUpdateRequest(20L, "수정된 메모2", Severity.WARNING);

        List<ChecklistUpdateRequest> requests = List.of(req1, req2);

        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));
        when(checklistItemRepository.findById(10L)).thenReturn(Optional.of(item1));
        when(checklistItemRepository.findById(20L)).thenReturn(Optional.of(item2));

        // when
        ChecklistUpdateResponse response = checklistService.updateChecklist(1L, requests);

        // then
        // 반환값 검증
        assertNotNull(response);
        assertEquals(1L, response.getChecklistId());
        assertEquals(100L, response.getPropertyId());
        assertEquals(2, response.getUpdatedItemCount());
        assertTrue(response.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(2)));

        // 실제 데이터 변경 검증
        assertEquals("수정된 메모", item1.getMemo());
        assertEquals(Severity.WARNING, item1.getSeverity());

        assertEquals("수정된 메모2", item2.getMemo());
        assertEquals(Severity.WARNING, item2.getSeverity());

        // repository 호출 검증
        verify(checklistRepository, times(1)).findById(1L);
        verify(checklistItemRepository, times(1)).findById(10L);
        verify(checklistItemRepository, times(1)).findById(20L);
    }

    @Test
    // 존재하지 않는 id일 경우 (ResourceNotFoundException 검증)
    void updateChecklist_checklistNotFound() {
        // given
        when(checklistRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> checklistService.updateChecklist(1L, List.of()));
    }

    @Test
    // ChecklistItem이 Checklist에 속하지 않을 경우
    void updateChecklist_itemNotInChecklist() {
        // given
        ChecklistUpdateRequest req = new ChecklistUpdateRequest(10L, "변경", Severity.DANGER);
        Checklist otherChecklist = Checklist.builder().id(999L).property(property).build();

        item1.setChecklist(otherChecklist); // 다른 체크리스트에 속하게 만듦

        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));
        when(checklistItemRepository.findById(10L)).thenReturn(Optional.of(item1));

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> checklistService.updateChecklist(1L, List.of(req)));
    }
}

