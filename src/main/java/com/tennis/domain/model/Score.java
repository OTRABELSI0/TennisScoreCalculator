package com.tennis.domain.model;

public record Score(int playerAPoints, int playerBPoints) {

    public String getDisplayScore(Player player) {
        int points = player == Player.A ? playerAPoints : playerBPoints;

        // Handle deuce/advantage scenarios
        if (playerAPoints >= 3 && playerBPoints >= 3) {
            if (isDeuce()) {
                return "Deuce";
            } else if (hasAdvantage(player)) {
                return "Advantage";
            } else {
                return "40";
            }
        }

        // Normal scoring
        return switch (points) {
            case 0 -> "0";
            case 1 -> "15";
            case 2 -> "30";
            case 3 -> "40";
            default -> "40"; // Fallback for scores > 3 when not in deuce scenario
        };
    }

    public Score addPoint(Player player) {
        return player == Player.A
                ? new Score(playerAPoints + 1, playerBPoints)
                : new Score(playerAPoints, playerBPoints + 1);
    }

    public boolean isDeuce() {
        return playerAPoints >= 3 && playerBPoints >= 3 && playerAPoints == playerBPoints;
    }

    public boolean hasAdvantage(Player player) {
        if (playerAPoints < 3 || playerBPoints < 3) return false;
        return player == Player.A
                ? playerAPoints == playerBPoints + 1
                : playerBPoints == playerAPoints + 1;
    }

    public boolean isGameWon() {
        // Must have at least 4 points and lead by 2
        if (Math.max(playerAPoints, playerBPoints) >= 4) {
            return Math.abs(playerAPoints - playerBPoints) >= 2;
        }
        return false;
    }

    public Player getWinner() {
        if (!isGameWon()) {
            throw new IllegalStateException("Game is not won yet");
        }
        return playerAPoints > playerBPoints ? Player.A : Player.B;
    }
}