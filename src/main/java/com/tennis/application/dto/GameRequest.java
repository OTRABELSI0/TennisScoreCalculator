package com.tennis.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to play a tennis game")
public record GameRequest(
        @Schema(
                description = "Sequence of ball wins. 'A' means Player A wins the ball, 'B' means Player B wins the ball",
                example = "ABABAA",
                pattern = "^[AB]+$"
        )
        @NotBlank(message = "Ball sequence cannot be empty")
        @Pattern(regexp = "^[AB]+$", message = "Ball sequence must contain only 'A' and 'B' characters")
        String ballSequence
) {}