package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.AllChecklistGetResponse;
import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.entity.ChecklistItem;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.enums.Severity;
import com.example.liskovbackend.repository.ChecklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class GetAllChecklistServiceTest {
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
                .createdAt(LocalDateTime.now())
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

}
