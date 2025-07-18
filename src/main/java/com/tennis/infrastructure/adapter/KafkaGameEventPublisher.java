package com.tennis.infrastructure.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.domain.model.GameState;
import com.tennis.domain.model.Player;
import com.tennis.domain.port.GameEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("kafka")
@ConditionalOnProperty(name = "tennis.kafka.enabled", havingValue = "true")
public class KafkaGameEventPublisher implements GameEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaGameEventPublisher.class);
    private static final String TOPIC = "tennis-game-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaGameEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publishPointScored(GameState gameState, Player player) {
        Map<String, Object> event = createBaseEvent(gameState);
        event.put("eventType", "POINT_SCORED");
        event.put("player", player.name());

        publishEvent(gameState.gameId(), event);
    }

    @Override
    public void publishGameFinished(GameState gameState) {
        Map<String, Object> event = createBaseEvent(gameState);
        event.put("eventType", "GAME_FINISHED");

        publishEvent(gameState.gameId(), event);
    }

    private Map<String, Object> createBaseEvent(GameState gameState) {
        Map<String, Object> event = new HashMap<>();
        event.put("gameId", gameState.gameId());
        event.put("playerAScore", gameState.score().playerAPoints());
        event.put("playerBScore", gameState.score().playerBPoints());
        event.put("displayScore", gameState.displayScore());
        event.put("isFinished", gameState.isFinished());
        event.put("winner", gameState.winner() != null ? gameState.winner().name() : null);
        event.put("timestamp", Instant.now().toEpochMilli());
        return event;
    }

    private void publishEvent(String gameId, Map<String, Object> event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, gameId, jsonEvent);
            logger.info("Published event to Kafka: {}", jsonEvent);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event", e);
        }
    }
}
