package com.tennis.domain.service;

import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;
import com.tennis.domain.port.GameEventPublisher;
import com.tennis.domain.port.GameRepository;
import com.tennis.infrastructure.monitoring.GameMetrics;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TennisGameService {

    private final GameRepository gameRepository;
    private final GameEventPublisher eventPublisher;
    private final GameMetrics gameMetrics;

    public TennisGameService(GameRepository gameRepository,
                             GameEventPublisher eventPublisher,
                             GameMetrics gameMetrics) {
        this.gameRepository = gameRepository;
        this.eventPublisher = eventPublisher;
        this.gameMetrics = gameMetrics;
    }

    public List<String> playGame(String ballSequence) {
        Timer.Sample sample = gameMetrics.startGameProcessingTimer();

        try {
            String gameId = UUID.randomUUID().toString();
            GameState gameState = GameState.initial(gameId);
            List<String> results = new ArrayList<>();

            for (char ball : ballSequence.toCharArray()) {
                if (gameState.isFinished()) {
                    break;
                }

                Player player = ball == 'A' ? Player.A : Player.B;
                gameState = gameState.addPoint(player);

                gameRepository.save(gameState);
                eventPublisher.publishPointScored(gameState, player);
                gameMetrics.incrementPointsScored();

                if (gameState.isFinished()) {
                    results.add(String.format("Player %s wins the game", gameState.winner()));
                    eventPublisher.publishGameFinished(gameState);
                    gameMetrics.incrementGamesPlayed();
                    gameMetrics.incrementPlayerWins(gameState.winner().name());
                } else {
                    results.add(gameState.displayScore());
                }
            }

            return results;
        } finally {
            gameMetrics.recordGameProcessingTime(sample);
        }
    }
}