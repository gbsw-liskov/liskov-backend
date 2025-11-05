package com.example.liskovbackend.controller.checklist;

import com.example.liskovbackend.controller.ChecklistController;
import com.example.liskovbackend.service.ChecklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChecklistController.class)
public class DeleteChecklistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChecklistService checklistService;

    @Test
    void deleteChecklist_returnsNoContent() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/checklist/{id}", id))
                .andExpect(status().isNoContent()); // 204 검증
    }
}
