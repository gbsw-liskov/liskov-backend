package com.example.liskovbackend.service.checklist;

import com.example.liskovbackend.dto.checklist.response.AllChecklistGetResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistGetResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistItemGetResponse;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetChecklistServiceTest {

    @Mock
    private ChecklistRepository checklistRepository;

    @InjectMocks
    private ChecklistService checklistService;

    private Checklist checklist1;
    private Checklist checklist2;
    private Property property1;
    private Property property2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        property1 = Property.builder()
                .id(1L)
                .build();

        property2 = Property.builder()
                .id(2L)
                .build();

        ChecklistItem item1 = ChecklistItem.builder()
                .id(1L)
                .content("점검 항목 1")
                .severity(Severity.DANGER)
                .memo("메모1")
                .build();

        ChecklistItem item2 = ChecklistItem.builder()
                .id(2L)
                .content("점검 항목 2")
                .severity(Severity.NORMAL)
                .memo("메모2")
                .build();

        checklist1 = Checklist.builder()
                .id(1L)
                .property(property1)
                .items(Arrays.asList(item1, item2))
                .isDeleted(false)
                .build();

        checklist2 = Checklist.builder()
                .id(2L)
                .property(property2)
                .items(List.of(item1))
                .createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    void getAllChecklist_success() {
        // given
        when(checklistRepository.findAll()).thenReturn(Arrays.asList(checklist1, checklist2));

        // when
        List<AllChecklistGetResponse> responses = checklistService.getAllChecklist();

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size()); // 두 개의 체크리스트 반환 확인

        AllChecklistGetResponse firstResponse = responses.get(0);
        assertEquals(1L, firstResponse.getChecklistId());
        assertEquals(1L, firstResponse.getPropertyId());
        assertEquals(2, firstResponse.getItemCount());

        AllChecklistGetResponse secondResponse = responses.get(1);
        assertEquals(2L, secondResponse.getChecklistId());
        assertEquals(2L, secondResponse.getPropertyId());
        assertEquals(1, secondResponse.getItemCount());

        verify(checklistRepository, times(1)).findAll();
    }

    @Test
    void getChecklistById_success() {
        // given
        Long checklistId = 1L;
        when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(checklist1));

        // when - call checklistService
        ChecklistGetResponse response = checklistService.getChecklistById(checklistId);

        // then
        // case response not null
        assertNotNull(response);

        // compare (checklistId, propertyId, items-Size
        assertEquals(1L, response.getChecklistId());
        assertEquals(1L, response.getPropertyId());
        assertEquals(2, response.getItems().size());

        // checklistItem
        ChecklistItemGetResponse firstItem = response.getItems().get(0);

        // compare (content, severity, memo)
        assertEquals("점검 항목 1", firstItem.getContent());
        assertEquals(Severity.DANGER, firstItem.getSeverity());
        assertEquals("메모1", firstItem.getMemo());

        verify(checklistRepository, times(1)).findById(checklistId);
    }

    @Test
    void getAllChecklist_emptyList_returnsEmptyResponse() {
        // given
        when(checklistRepository.findAll()).thenReturn(List.of());

        // when
        List<AllChecklistGetResponse> responses = checklistService.getAllChecklist();

        // then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(checklistRepository, times(1)).findAll();
    }

    @Test
    void getAllChecklist_isDeletedFiltered_success() {
        // given
        Checklist deletedChecklist = Checklist.builder()
                .id(1L)
                .isDeleted(true)
                .build();

        when(checklistRepository.findAll())
                .thenReturn(List.of(deletedChecklist));

        // when
        List<AllChecklistGetResponse> responses = checklistService.getAllChecklist();

        // then
        assertTrue(responses.isEmpty());
        verify(checklistRepository, times(1)).findAll();
    }


    @Test
    void getChecklistById_notFound_throwsException() {
        // given
        Long invalidId = 999L;
        when(checklistRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when & then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                checklistService.getChecklistById(invalidId)
        );

        assertEquals("체크리스트가 존재하지 않습니다.", exception.getMessage());
        verify(checklistRepository, times(1)).findById(invalidId);
    }

    @Test
    void getChecklistById_isDeleted_throwsException() {
        // given
        Long checklistId = 1L;
        Checklist deletedChecklist = Checklist.builder()
                .id(checklistId)
                .isDeleted(true)
                .build();

        when(checklistRepository.findById(checklistId))
                .thenReturn(Optional.of(deletedChecklist));

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> checklistService.getChecklistById(checklistId));

        verify(checklistRepository, times(1)).findById(checklistId);
    }

}
