package com.tennis.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tennis scoring rules")
public record TennisRulesResponse(
        @Schema(description = "Basic scoring progression", example = "0, 15, 30, 40, Game")
        String scoring,

        @Schema(description = "Deuce rule", example = "When both players reach 40, it's deuce")
        String deuce,

        @Schema(description = "Advantage rule", example = "From deuce, next point gives advantage")
        String advantage,

        @Schema(description = "Winning rule", example = "Player with advantage wins on next point, or back to deuce")
        String winning,

        @Schema(description = "Input format", example = "Send 'A' for Player A point, 'B' for Player B point")
        String input,

        @Schema(description = "Example sequence", example = "ABABAA means: A scores, B scores, A scores, B scores, A scores, A scores (A wins)")
        String example
) {}