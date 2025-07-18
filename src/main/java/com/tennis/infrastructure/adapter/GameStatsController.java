package com.tennis.infrastructure.adapter;

import com.tennis.domain.port.GameRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tennis/stats")
@Tag(name = "Statistics", description = "Game statistics and metrics")
public class GameStatsController {

    private final GameRepository gameRepository;
    private final MeterRegistry meterRegistry;

    public GameStatsController(GameRepository gameRepository, MeterRegistry meterRegistry) {
        this.gameRepository = gameRepository;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @Operation(
            summary = "Get game statistics",
            description = "Returns comprehensive game statistics including games played, points scored, and win rates"
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get metrics from Micrometer
        double gamesPlayed = getCounterValue("tennis.games.played");
        double pointsScored = getCounterValue("tennis.points.scored");
        double playerAWins = getCounterValue("tennis.games.won", "player", "A");
        double playerBWins = getCounterValue("tennis.games.won", "player", "B");

        stats.put("gamesPlayed", (int) gamesPlayed);
        stats.put("pointsScored", (int) pointsScored);
        stats.put("playerAWins", (int) playerAWins);
        stats.put("playerBWins", (int) playerBWins);

        if (gamesPlayed > 0) {
            stats.put("playerAWinRate", String.format("%.2f%%", (playerAWins / gamesPlayed) * 100));
            stats.put("playerBWinRate", String.format("%.2f%%", (playerBWins / gamesPlayed) * 100));
            stats.put("averagePointsPerGame", String.format("%.2f", pointsScored / gamesPlayed));
        }

        stats.put("metricsEndpoint", "/actuator/metrics");
        stats.put("prometheusEndpoint", "/actuator/prometheus");

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get statistics summary",
            description = "Returns a brief summary of key statistics"
    )
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        double gamesPlayed = getCounterValue("tennis.games.played");
        double pointsScored = getCounterValue("tennis.points.scored");

        summary.put("totalGames", (int) gamesPlayed);
        summary.put("totalPoints", (int) pointsScored);
        summary.put("status", gamesPlayed > 0 ? "Active" : "No games played yet");

        return ResponseEntity.ok(summary);
    }

    private double getCounterValue(String counterName) {
        Counter counter = meterRegistry.find(counterName).counter();
        return counter != null ? counter.count() : 0.0;
    }

    private double getCounterValue(String counterName, String tagKey, String tagValue) {
        Counter counter = meterRegistry.find(counterName)
                .tag(tagKey, tagValue)
                .counter();
        return counter != null ? counter.count() : 0.0;
    }
}