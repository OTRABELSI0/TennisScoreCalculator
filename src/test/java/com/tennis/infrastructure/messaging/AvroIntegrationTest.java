package com.tennis.infrastructure.messaging;

import com.tennis.avro.EventType;
import com.tennis.avro.GameEvent;
import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AvroIntegrationTest {

    @Test
    void shouldCreateAndSerializeGameEvent() {
        // Given
        GameState gameState = GameState.initial("test-game-id");
        gameState = gameState.addPoint(Player.A);

        // When - Create Avro event
        GameEvent event = GameEvent.newBuilder()
                .setGameId(gameState.gameId())
                .setEventType(EventType.POINT_SCORED)
                .setPlayer("A")
                .setPlayerAScore(gameState.score().playerAPoints())
                .setPlayerBScore(gameState.score().playerBPoints())
                .setDisplayScore(gameState.displayScore())
                .setIsFinished(gameState.isFinished())
                .setWinner(gameState.winner() != null ? gameState.winner().name() : null)
                .setTimestamp(System.currentTimeMillis())
                .setMetadata(com.tennis.avro.EventMetadata.newBuilder()
                        .setVersion("1.0.0")
                        .setSource("tennis-scoring-system")
                        .build())
                .build();

        // Then - Verify event structure
        assertNotNull(event);
        assertEquals("test-game-id", event.getGameId());
        assertEquals(EventType.POINT_SCORED, event.getEventType());
        assertEquals("A", event.getPlayer());
        assertEquals(1, event.getPlayerAScore());
        assertEquals(0, event.getPlayerBScore());
        assertFalse(event.getIsFinished());
        assertNull(event.getWinner());
        assertNotNull(event.getTimestamp());
        assertEquals("1.0.0", event.getMetadata().getVersion());
        assertEquals("tennis-scoring-system", event.getMetadata().getSource());
    }

    @Test
    void shouldCreateGameFinishedEvent() {
        // Given
        GameState gameState = GameState.initial("test-game-id");
        // Simulate a finished game
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.A); // 4-0 (game finished)

        // When
        GameEvent event = GameEvent.newBuilder()
                .setGameId(gameState.gameId())
                .setEventType(EventType.GAME_FINISHED)
                .setPlayer(null)
                .setPlayerAScore(gameState.score().playerAPoints())
                .setPlayerBScore(gameState.score().playerBPoints())
                .setDisplayScore(gameState.displayScore())
                .setIsFinished(gameState.isFinished())
                .setWinner(gameState.winner().name())
                .setTimestamp(System.currentTimeMillis())
                .setMetadata(com.tennis.avro.EventMetadata.newBuilder()
                        .setVersion("1.0.0")
                        .setSource("tennis-scoring-system")
                        .build())
                .build();

        // Then
        assertEquals(EventType.GAME_FINISHED, event.getEventType());
        assertTrue(event.getIsFinished());
        assertEquals("A", event.getWinner());
        assertNull(event.getPlayer());
    }
}