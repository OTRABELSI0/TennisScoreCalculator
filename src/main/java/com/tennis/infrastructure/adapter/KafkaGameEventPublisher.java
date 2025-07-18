package com.tennis.infrastructure.adapter;

import com.tennis.avro.EventMetadata;
import com.tennis.avro.EventType;
import com.tennis.avro.GameEvent;
import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;
import com.tennis.domain.port.GameEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Profile("kafka")
public class KafkaGameEventPublisher implements GameEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaGameEventPublisher.class);
    private static final String TOPIC = "tennis-game-events";

    private final KafkaTemplate<String, GameEvent> kafkaTemplate;

    public KafkaGameEventPublisher(KafkaTemplate<String, GameEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPointScored(GameState gameState, Player player) {
        GameEvent event = createGameEvent(gameState, EventType.POINT_SCORED, player.name());
        publishEvent(gameState.gameId(), event);
        logger.info("Published POINT_SCORED event for game: {}, player: {}",
                gameState.gameId(), player);
    }

    @Override
    public void publishGameFinished(GameState gameState) {
        GameEvent event = createGameEvent(gameState, EventType.GAME_FINISHED, null);
        publishEvent(gameState.gameId(), event);
        logger.info("Published GAME_FINISHED event for game: {}, winner: {}",
                gameState.gameId(), gameState.winner());
    }

    private GameEvent createGameEvent(GameState gameState, EventType eventType, String player) {
        EventMetadata metadata = EventMetadata.newBuilder()
                .setVersion("1.0.0")
                .setSource("tennis-scoring-system")
                .build();

        return GameEvent.newBuilder()
                .setGameId(gameState.gameId())
                .setEventType(eventType)
                .setPlayer(player)
                .setPlayerAScore(gameState.score().playerAPoints())
                .setPlayerBScore(gameState.score().playerBPoints())
                .setDisplayScore(gameState.displayScore())
                .setIsFinished(gameState.isFinished())
                .setWinner(gameState.winner() != null ? gameState.winner().name() : null)
                .setTimestamp(Instant.now().toEpochMilli())
                .setMetadata(metadata)
                .build();
    }

    private void publishEvent(String gameId, GameEvent event) {
        try {
            kafkaTemplate.send(TOPIC, gameId, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.error("Failed to publish event for game: {}", gameId, ex);
                        } else {
                            logger.debug("Successfully published event for game: {} to partition: {}",
                                    gameId, result.getRecordMetadata().partition());
                        }
                    });
        } catch (Exception e) {
            logger.error("Error publishing event for game: {}", gameId, e);
        }
    }
}

