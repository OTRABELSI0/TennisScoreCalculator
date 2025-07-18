package com.tennis.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing the game results")
public record GameResponse(
        @Schema(description = "Unique identifier for the game", example = "123e4567-e89b-12d3-a456-426614174000")
        String gameId,

        @Schema(description = "List of score progressions throughout the game",
                example = "[\"Player A : 15 / Player B : 0\", \"Player A : 15 / Player B : 15\", \"Player A wins the game\"]")
        List<String> scoreProgression,

        @Schema(description = "Whether the game has finished", example = "true")
        boolean isFinished,

        @Schema(description = "Winner of the game (null if not finished)", example = "Player A")
        String winner
) {}