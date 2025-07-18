package com.tennis.domain.model;

public record GameState(
        String gameId,
        Score score,
        boolean isFinished,
        Player winner,
        String displayScore
) {

    public static GameState initial(String gameId) {
        var score = new Score(0, 0);
        return new GameState(
                gameId,
                score,
                false,
                null,
                formatDisplayScore(score)
        );
    }

    public GameState addPoint(Player player) {
        var newScore = score.addPoint(player);
        boolean finished = newScore.isGameWon();
        Player gameWinner = finished ? newScore.getWinner() : null;

        return new GameState(
                gameId,
                newScore,
                finished,
                gameWinner,
                formatDisplayScore(newScore)
        );
    }

    private static String formatDisplayScore(Score score) {
        if (score.isDeuce()) {
            return "Player A : Deuce / Player B : Deuce";
        }

        if (score.hasAdvantage(Player.A)) {
            return "Player A : Advantage / Player B : 40";
        }

        if (score.hasAdvantage(Player.B)) {
            return "Player A : 40 / Player B : Advantage";
        }

        return String.format("Player A : %s / Player B : %s",
                score.getDisplayScore(Player.A),
                score.getDisplayScore(Player.B));
    }
}