package com.tennis.application.usecase;

import com.tennis.application.dto.GameRequest;
import com.tennis.application.dto.GameResponse;
import com.tennis.domain.service.TennisGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayTennisGameUseCaseTest {

    @Mock
    private TennisGameService tennisGameService;

    private PlayTennisGameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new PlayTennisGameUseCase(tennisGameService);
    }

    @Test
    void shouldExecuteGameAndReturnResponse() {
        // Given
        GameRequest request = new GameRequest("ABABAA");
        List<String> mockResults = List.of(
                "Player A : 15 / Player B : 0",
                "Player A : 15 / Player B : 15",
                "Player A : 30 / Player B : 15",
                "Player A : 30 / Player B : 30",
                "Player A wins the game"
        );

        when(tennisGameService.playGame(anyString())).thenReturn(mockResults);

        // When
        GameResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(5, response.scoreProgression().size());
        assertTrue(response.isFinished());
        assertEquals("Player A", response.winner());
        assertEquals("Player A : 15 / Player B : 0", response.scoreProgression().get(0));
        assertEquals("Player A wins the game", response.scoreProgression().get(4));
    }

    @Test
    void shouldHandleUnfinishedGame() {
        // Given
        GameRequest request = new GameRequest("AB");
        List<String> mockResults = List.of(
                "Player A : 15 / Player B : 0",
                "Player A : 15 / Player B : 15"
        );

        when(tennisGameService.playGame(anyString())).thenReturn(mockResults);

        // When
        GameResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.scoreProgression().size());
        assertFalse(response.isFinished());
        assertNull(response.winner());
    }

    @Test
    void shouldIdentifyPlayerBAsWinner() {
        // Given
        GameRequest request = new GameRequest("BBBB");
        List<String> mockResults = List.of(
                "Player A : 0 / Player B : 15",
                "Player A : 0 / Player B : 30",
                "Player A : 0 / Player B : 40",
                "Player B wins the game"
        );

        when(tennisGameService.playGame(anyString())).thenReturn(mockResults);

        // When
        GameResponse response = useCase.execute(request);

        // Then
        assertTrue(response.isFinished());
        assertEquals("Player B", response.winner());
    }
}

