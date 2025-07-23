package org.example.springbootrecap.controller;

import org.example.springbootrecap.repository.ToDoRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ToDoRepo repo;

    @Test
    void undoRedoTest() throws Exception {
        String json = """
            {
                "description": "Test-To-Do",
                "status": "DONE"
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().json(json))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());

        assertFalse(repo.findAll().isEmpty(), "Repo should not be empty before undo-redo");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/todo/undo"))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        } catch (Exception e) {
            fail("First undo failed");
        }

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/todo/undo"))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        } catch (Exception e) {
            fail("Second undo succeeded");
        }

        assertTrue(repo.findAll().isEmpty(), "Repo should be empty");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/todo/redo"))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        } catch (Exception e) {
            fail("First redo failed");
        }

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/todo/redo"))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        } catch (Exception e) {
            fail("Second redo succeeded");
        }

        assertFalse(repo.findAll().isEmpty(), "Repo should not be empty empty after undo-redo");
    }
}