package com.tennis.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.application.dto.GameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TennisGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPlayGameSuccessfully() throws Exception {
        // Given
        GameRequest request = new GameRequest("ABABAA");

        // When & Then
        mockMvc.perform(post("/api/tennis/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreProgression").isArray())
                .andExpect(jsonPath("$.scoreProgression[0]").value("Player A : 15 / Player B : 0"))
                .andExpect(jsonPath("$.scoreProgression[5]").value("Player A wins the game"))
                .andExpect(jsonPath("$.isFinished").value(true))
                .andExpect(jsonPath("$.winner").value("Player A"));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        // Given
        GameRequest request = new GameRequest("INVALID");

        // When & Then
        mockMvc.perform(post("/api/tennis/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void shouldReturnBadRequestForEmptyInput() throws Exception {
        // Given
        GameRequest request = new GameRequest("");

        // When & Then
        mockMvc.perform(post("/api/tennis/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/tennis/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tennis Game Service is running"));
    }

    @Test
    void shouldReturnRules() throws Exception {
        mockMvc.perform(get("/api/tennis/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoring").exists())
                .andExpect(jsonPath("$.deuce").exists())
                .andExpect(jsonPath("$.advantage").exists());
    }
}