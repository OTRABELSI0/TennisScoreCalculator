package com.tennis.infrastructure.adapter;

import com.tennis.application.dto.GameRequest;
import com.tennis.application.dto.GameResponse;
import com.tennis.application.dto.TennisRulesResponse;
import com.tennis.application.usecase.PlayTennisGameUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tennis")
@CrossOrigin(origins = "*")
@Tag(name = "Tennis Game", description = "Tennis scoring system API")
public class TennisGameController {

    private static final Logger logger = LoggerFactory.getLogger(TennisGameController.class);

    private final PlayTennisGameUseCase playTennisGameUseCase;

    public TennisGameController(PlayTennisGameUseCase playTennisGameUseCase) {
        this.playTennisGameUseCase = playTennisGameUseCase;
    }

    @PostMapping("/games")
    @Operation(
            summary = "Play a tennis game",
            description = "Simulates a tennis game based on a sequence of ball wins. Returns the score progression and final result."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Game played successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful game",
                                    value = """
                    {
                      "gameId": "123e4567-e89b-12d3-a456-426614174000",
                      "scoreProgression": [
                        "Player A : 15 / Player B : 0",
                        "Player A : 15 / Player B : 15",
                        "Player A : 30 / Player B : 15",
                        "Player A : 30 / Player B : 30",
                        "Player A : 40 / Player B : 30",
                        "Player A wins the game"
                      ],
                      "isFinished": true,
                      "winner": "Player A"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = """
                    {
                      "timestamp": "2023-12-07T10:15:30",
                      "status": 400,
                      "error": "Validation Failed",
                      "errors": {
                        "ballSequence": "Ball sequence must contain only 'A' and 'B' characters"
                      }
                    }
                    """
                            )
                    )
            )
    })
    public ResponseEntity<GameResponse> playGame(
            @Parameter(
                    description = "Game request containing ball sequence",
                    required = true,
                    example = "ABABAA"
            )
            @Valid @RequestBody GameRequest request) {

        logger.info("Playing game with sequence: {}", request.ballSequence());

        GameResponse response = playTennisGameUseCase.execute(request);

        logger.info("Game completed. Winner: {}, Finished: {}",
                response.winner(), response.isFinished());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Simple health check endpoint"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(value = "Tennis Game Service is running")
            )
    )
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tennis Game Service is running");
    }

    @GetMapping("/rules")
    @Operation(
            summary = "Get tennis rules",
            description = "Returns the tennis scoring rules implemented by this system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tennis rules",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TennisRulesResponse.class)
            )
    )
    public ResponseEntity<TennisRulesResponse> getRules() {
        TennisRulesResponse rules = new TennisRulesResponse(
                "0, 15, 30, 40, Game",
                "When both players reach 40, it's deuce",
                "From deuce, next point gives advantage",
                "Player with advantage wins on next point, or back to deuce",
                "Send 'A' for Player A point, 'B' for Player B point",
                "ABABAA means: A scores, B scores, A scores, B scores, A scores, A scores (A wins)"
        );

        return ResponseEntity.ok(rules);
    }

}
