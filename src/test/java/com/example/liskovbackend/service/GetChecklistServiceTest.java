package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.ChecklistGetResponse;
import com.example.liskovbackend.dto.ChecklistItemGetResponse;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.enums.Severity;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetChecklistServiceTest {

    @Mock
    private ChecklistRepository checklistRepository;

    @InjectMocks
    private ChecklistService checklistService;

    private Checklist checklist;
    private Property property;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        property = Property.builder()
                .id(1L)
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

        checklist = Checklist.builder()
                .id(1L)
                .property(property)
                .items(Arrays.asList(item1, item2))
                .isDeleted(false)
                .build();
    }

    @Test
    void getChecklistById_success() {
        // given
        Long checklistId = 1L;
        when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(checklist));

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
