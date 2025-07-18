package com.tennis.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    void shouldDisplayCorrectScoreForPlayerA() {
        // Given
        var score = new Score(1, 2);

        // When & Then
        assertEquals("15", score.getDisplayScore(Player.A));
    }

    @Test
    void shouldDisplayCorrectScoreForPlayerB() {
        // Given
        var score = new Score(1, 2);

        // When & Then
        assertEquals("30", score.getDisplayScore(Player.B));
    }

    @Test
    void shouldDisplayAllNormalScoreValues() {
        assertEquals("0", new Score(0, 0).getDisplayScore(Player.A));
        assertEquals("15", new Score(1, 0).getDisplayScore(Player.A));
        assertEquals("30", new Score(2, 0).getDisplayScore(Player.A));
        assertEquals("40", new Score(3, 0).getDisplayScore(Player.A));
    }

    @Test
    void shouldDisplayDeuceForBothPlayers() {
        var score = new Score(3, 3);
        assertEquals("Deuce", score.getDisplayScore(Player.A));
        assertEquals("Deuce", score.getDisplayScore(Player.B));
    }

    @Test
    void shouldDisplayAdvantageCorrectly() {
        var score = new Score(4, 3);
        assertEquals("Advantage", score.getDisplayScore(Player.A));
        assertEquals("40", score.getDisplayScore(Player.B));
    }

    @Test
    void shouldDisplayAdvantageForPlayerB() {
        var score = new Score(3, 4);
        assertEquals("40", score.getDisplayScore(Player.A));
        assertEquals("Advantage", score.getDisplayScore(Player.B));
    }

    @Test
    void shouldAddPointToPlayerA() {
        // Given
        var score = new Score(1, 1);

        // When
        var newScore = score.addPoint(Player.A);

        // Then
        assertEquals(2, newScore.playerAPoints());
        assertEquals(1, newScore.playerBPoints());
    }

    @Test
    void shouldAddPointToPlayerB() {
        // Given
        var score = new Score(1, 1);

        // When
        var newScore = score.addPoint(Player.B);

        // Then
        assertEquals(1, newScore.playerAPoints());
        assertEquals(2, newScore.playerBPoints());
    }

    @Test
    void shouldDetectDeuce() {
        assertTrue(new Score(3, 3).isDeuce());
        assertTrue(new Score(4, 4).isDeuce());
        assertTrue(new Score(5, 5).isDeuce());
    }

    @Test
    void shouldNotDetectDeuceWhenScoresAreDifferent() {
        assertFalse(new Score(3, 2).isDeuce());
        assertFalse(new Score(2, 3).isDeuce());
        assertFalse(new Score(4, 3).isDeuce());
    }

    @Test
    void shouldDetectAdvantageForPlayerA() {
        assertTrue(new Score(4, 3).hasAdvantage(Player.A));
        assertTrue(new Score(5, 4).hasAdvantage(Player.A));
        assertFalse(new Score(4, 3).hasAdvantage(Player.B));
    }

    @Test
    void shouldDetectAdvantageForPlayerB() {
        assertTrue(new Score(3, 4).hasAdvantage(Player.B));
        assertTrue(new Score(4, 5).hasAdvantage(Player.B));
        assertFalse(new Score(3, 4).hasAdvantage(Player.A));
    }

    @Test
    void shouldDetectGameWonByPlayerA() {
        assertTrue(new Score(4, 2).isGameWon());
        assertTrue(new Score(4, 1).isGameWon());
        assertTrue(new Score(4, 0).isGameWon());
        assertTrue(new Score(5, 3).isGameWon()); // After deuce

        assertEquals(Player.A, new Score(4, 2).getWinner());
    }

    @Test
    void shouldDetectGameWonByPlayerB() {
        assertTrue(new Score(2, 4).isGameWon());
        assertTrue(new Score(1, 4).isGameWon());
        assertTrue(new Score(0, 4).isGameWon());
        assertTrue(new Score(3, 5).isGameWon()); // After deuce

        assertEquals(Player.B, new Score(2, 4).getWinner());
    }

    @Test
    void shouldNotDetectGameWonWhenScoreIsClose() {
        assertFalse(new Score(3, 3).isGameWon()); // Deuce
        assertFalse(new Score(4, 3).isGameWon()); // Advantage, not won
        assertFalse(new Score(3, 4).isGameWon()); // Advantage, not won
        assertFalse(new Score(4, 4).isGameWon()); // Deuce again
    }

    @Test
    void shouldThrowExceptionWhenGettingWinnerOfUnfinishedGame() {
        Score score = new Score(3, 3);
        assertThrows(IllegalStateException.class, score::getWinner);
    }
}