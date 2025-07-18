package com.tennis.application.usecase;

import com.tennis.application.dto.GameRequest;
import com.tennis.application.dto.GameResponse;
import com.tennis.domain.service.TennisGameService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlayTennisGameUseCase {

    private final TennisGameService tennisGameService;

    public PlayTennisGameUseCase(TennisGameService tennisGameService) {
        this.tennisGameService = tennisGameService;
    }

    public GameResponse execute(GameRequest request) {
        List<String> scoreProgression = tennisGameService.playGame(request.ballSequence());

        String lastScore = scoreProgression.getLast();
        boolean isFinished = lastScore.contains("wins the game");
        String winner = null;

        if (isFinished) {
            winner = lastScore.contains("Player A") ? "Player A" : "Player B";
        }

        return new GameResponse(
                UUID.randomUUID().toString(),
                scoreProgression,
                isFinished,
                winner
        );
    }
}