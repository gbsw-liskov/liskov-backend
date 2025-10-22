package com.example.liskovbackend.service.checklist;

import com.example.liskovbackend.entity.Checklist;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.ChecklistRepository;
import com.example.liskovbackend.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteChecklistServiceTest {
    @Mock
    private ChecklistRepository checklistRepository;

    @InjectMocks
    private ChecklistService checklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteChecklist_success() {
        // given
        Long id = 1L;
        Checklist checklist = Checklist.builder()
                .id(id)
                .isDeleted(false)
                .build();

        when(checklistRepository.findById(id)).thenReturn(Optional.of(checklist));
        when(checklistRepository.save(any(Checklist.class))).thenReturn(checklist);

        // when
        checklistService.deleteChecklist(id);

        // then
        assertThat(checklist.getIsDeleted()).isTrue();
        verify(checklistRepository, times(1)).save(checklist);
    }

    @Test
    void deleteChecklist_notFound() {
        // given
        Long id = 999L;
        when(checklistRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> checklistService.deleteChecklist(id));
        verify(checklistRepository, never()).save(any(Checklist.class));
    }

    @Test
    void deleteChecklist_responseStatus_noContent() {
        // given
        Long id = 1L;
        Checklist checklist = Checklist.builder().id(id).isDeleted(false).build();
        when(checklistRepository.findById(id)).thenReturn(Optional.of(checklist));

        // when
        checklistService.deleteChecklist(id);

    }
}
