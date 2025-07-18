package com.tennis.infrastructure.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class GameMetrics {

    private final Counter gamesPlayedCounter;
    private final Counter pointsScoredCounter;
    private final Counter playerAWinsCounter;
    private final Counter playerBWinsCounter;
    private final Timer gameProcessingTimer;

    public GameMetrics(MeterRegistry meterRegistry) {
        this.gamesPlayedCounter = Counter.builder("tennis.games.played")
                .description("Total number of tennis games played")
                .register(meterRegistry);

        this.pointsScoredCounter = Counter.builder("tennis.points.scored")
                .description("Total number of points scored")
                .register(meterRegistry);

        this.playerAWinsCounter = Counter.builder("tennis.games.won")
                .tag("player", "A")
                .description("Games won by Player A")
                .register(meterRegistry);

        this.playerBWinsCounter = Counter.builder("tennis.games.won")
                .tag("player", "B")
                .description("Games won by Player B")
                .register(meterRegistry);

        this.gameProcessingTimer = Timer.builder("tennis.game.processing.time")
                .description("Time taken to process a game")
                .register(meterRegistry);
    }

    public void incrementGamesPlayed() {
        gamesPlayedCounter.increment();
    }

    public void incrementPointsScored() {
        pointsScoredCounter.increment();
    }

    public void incrementPlayerWins(String player) {
        if ("A".equals(player)) {
            playerAWinsCounter.increment();
        } else if ("B".equals(player)) {
            playerBWinsCounter.increment();
        }
    }

    public Timer.Sample startGameProcessingTimer() {
        return Timer.start();
    }

    public void recordGameProcessingTime(Timer.Sample sample) {
        sample.stop(gameProcessingTimer);
    }
}