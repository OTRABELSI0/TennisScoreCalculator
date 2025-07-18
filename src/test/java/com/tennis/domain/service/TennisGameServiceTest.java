package com.tennis.domain.service;

import com.tennis.domain.port.GameEventPublisher;
import com.tennis.domain.port.GameRepository;
import com.tennis.infrastructure.monitoring.GameMetrics;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TennisGameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameEventPublisher eventPublisher;

    @Mock
    private GameMetrics gameMetrics;

    @Mock
    private Timer.Sample timerSample;

    private TennisGameService tennisGameService;

    @BeforeEach
    void setUp() {
        when(gameMetrics.startGameProcessingTimer()).thenReturn(timerSample);
        tennisGameService = new TennisGameService(gameRepository, eventPublisher, gameMetrics);
    }

    @Test
    void shouldPlaySimpleGame() {
        // Given
        String ballSequence = "AAAA"; // Player A wins 4-0

        // When
        List<String> results = tennisGameService.playGame(ballSequence);

        // Then
        assertEquals(4, results.size());
        assertEquals("Player A : 15 / Player B : 0", results.get(0));
        assertEquals("Player A : 30 / Player B : 0", results.get(1));
        assertEquals("Player A : 40 / Player B : 0", results.get(2));
        assertEquals("Player A wins the game", results.get(3));

        // Verify interactions
        verify(gameRepository, times(4)).save(any());
        verify(eventPublisher, times(4)).publishPointScored(any(), any());
        verify(eventPublisher, times(1)).publishGameFinished(any());
        verify(gameMetrics, times(1)).incrementGamesPlayed();
        verify(gameMetrics, times(1)).incrementPlayerWins("A");
        verify(gameMetrics, times(4)).incrementPointsScored();
    }

    @Test
    void shouldPlayGameWithSpecificSequence() {
        // Given
        String ballSequence = "ABABAA"; // The example from the kata

        // When
        List<String> results = tennisGameService.playGame(ballSequence);

        // Then
        assertEquals(6, results.size());
        assertEquals("Player A : 15 / Player B : 0", results.get(0));
        assertEquals("Player A : 15 / Player B : 15", results.get(1));
        assertEquals("Player A : 30 / Player B : 15", results.get(2));
        assertEquals("Player A : 30 / Player B : 30", results.get(3));
        assertEquals("Player A : 40 / Player B : 30", results.get(4));
        assertEquals("Player A wins the game", results.get(5));
    }

    @Test
    void shouldHandleDeuceScenario() {
        // Given
        String ballSequence = "ABABAB"; // 3-3, should end in deuce

        // When
        List<String> results = tennisGameService.playGame(ballSequence);

        // Then
        String lastResult = results.getLast();
        assertTrue(lastResult.contains("Deuce"));

        // Game should not be finished
        verify(eventPublisher, never()).publishGameFinished(any());
        verify(gameMetrics, never()).incrementGamesPlayed();
    }

    @Test
    void shouldHandleAdvantageScenario() {
        // Given
        String ballSequence = "ABABABA"; // 4-3, Player A has advantage

        // When
        List<String> results = tennisGameService.playGame(ballSequence);

        // Then
        String lastResult = results.getLast();
        assertTrue(lastResult.contains("Advantage"));
    }

    @Test
    void shouldStopProcessingWhenGameIsFinished() {
        // Given
        String ballSequence = "AAAAAA"; // More balls than needed

        // When
        List<String> results = tennisGameService.playGame(ballSequence);

        // Then
        assertEquals(4, results.size()); // Should stop after 4 points
        assertEquals("Player A wins the game", results.get(3));
    }
}