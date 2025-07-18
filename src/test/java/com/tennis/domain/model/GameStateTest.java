package com.tennis.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void shouldCreateInitialGameState() {
        // When
        var gameState = GameState.initial("game-123");

        // Then
        assertEquals("game-123", gameState.gameId());
        assertEquals(0, gameState.score().playerAPoints());
        assertEquals(0, gameState.score().playerBPoints());
        assertFalse(gameState.isFinished());
        assertNull(gameState.winner());
        assertEquals("Player A : 0 / Player B : 0", gameState.displayScore());
    }

    @Test
    void shouldAddPointAndUpdateDisplayScore() {
        // Given
        var gameState = GameState.initial("game-123");

        // When
        var newGameState = gameState.addPoint(Player.A);

        // Then
        assertEquals(1, newGameState.score().playerAPoints());
        assertEquals(0, newGameState.score().playerBPoints());
        assertEquals("Player A : 15 / Player B : 0", newGameState.displayScore());
        assertFalse(newGameState.isFinished());
    }

    @Test
    void shouldFinishGameWhenPlayerWins() {
        // Given - Create a game state where Player A has 3 points and Player B has 2
        var gameState = GameState.initial("game-123");
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.B); // 3-1
        gameState = gameState.addPoint(Player.B); // 3-2

        // When - Player A scores the winning point
        var finalGameState = gameState.addPoint(Player.A); // 4-2

        // Then
        assertTrue(finalGameState.isFinished());
        assertEquals(Player.A, finalGameState.winner());
    }

    @Test
    void shouldDisplayDeuceCorrectly() {
        // Given - Create a deuce situation (3-3)
        var gameState = GameState.initial("game-123");
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.B); // 3-1
        gameState = gameState.addPoint(Player.B); // 3-2
        gameState = gameState.addPoint(Player.B); // 3-3 (deuce)

        // When
        var displayScore = gameState.displayScore();

        // Then
        assertEquals("Player A : Deuce / Player B : Deuce", displayScore);
    }

    @Test
    void shouldDisplayAdvantageCorrectly() {
        // Given - Create an advantage situation (4-3)
        var gameState = GameState.initial("game-123");
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.B); // 3-1
        gameState = gameState.addPoint(Player.B); // 3-2
        gameState = gameState.addPoint(Player.B); // 3-3 (deuce)
        gameState = gameState.addPoint(Player.A); // 4-3 (advantage A)

        // When
        var displayScore = gameState.displayScore();

        // Then
        assertEquals("Player A : Advantage / Player B : 40", displayScore);
    }

    @Test
    void shouldDisplayAdvantageForPlayerB() {
        // Given - Create an advantage situation for Player B (3-4)
        var gameState = GameState.initial("game-123");
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.B); // 3-1
        gameState = gameState.addPoint(Player.B); // 3-2
        gameState = gameState.addPoint(Player.B); // 3-3 (deuce)
        gameState = gameState.addPoint(Player.B); // 3-4 (advantage B)

        // When
        var displayScore = gameState.displayScore();

        // Then
        assertEquals("Player A : 40 / Player B : Advantage", displayScore);
    }

    @Test
    void shouldReturnToDeuceFromAdvantage() {
        // Given - Create advantage for A, then B scores
        var gameState = GameState.initial("game-123");
        gameState = gameState.addPoint(Player.A); // 1-0
        gameState = gameState.addPoint(Player.A); // 2-0
        gameState = gameState.addPoint(Player.A); // 3-0
        gameState = gameState.addPoint(Player.B); // 3-1
        gameState = gameState.addPoint(Player.B); // 3-2
        gameState = gameState.addPoint(Player.B); // 3-3 (deuce)
        gameState = gameState.addPoint(Player.A); // 4-3 (advantage A)

        // When - Player B scores to return to deuce
        gameState = gameState.addPoint(Player.B); // 4-4 (deuce again)

        // Then
        assertEquals("Player A : Deuce / Player B : Deuce", gameState.displayScore());
        assertFalse(gameState.isFinished());
    }
}