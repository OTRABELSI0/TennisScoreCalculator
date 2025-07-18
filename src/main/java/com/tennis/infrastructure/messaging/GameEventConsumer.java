package com.tennis.infrastructure.messaging;

import com.tennis.avro.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class GameEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GameEventConsumer.class);

    @KafkaListener(
            topics = "tennis-game-events",
            groupId = "tennis-game-consumer",
            containerFactory = "avroKafkaListenerContainerFactory"
    )
    public void handleGameEvent(
            @Payload GameEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Received event: type={}, gameId={}, partition={}, offset={}",
                    event.getEventType(), event.getGameId(), partition, offset);

            processEvent(event);

            // Acknowledge the message
            acknowledgment.acknowledge();

        } catch (Exception e) {
            logger.error("Error processing event: {}", event, e);
            // In a real application, send to a dead letter queue
        }
    }

    private void processEvent(GameEvent event) {
        switch (event.getEventType()) {
            case POINT_SCORED -> handlePointScored(event);
            case GAME_FINISHED -> handleGameFinished(event);
            default -> logger.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handlePointScored(GameEvent event) {
        logger.info("Processing point scored: Game={}, Player={}, Score={}",
                event.getGameId(), event.getPlayer(), event.getDisplayScore());

        // - Update real-time dashboards
        // - Send notifications
        // - Update statistics
        // - Store in a database
    }

    private void handleGameFinished(GameEvent event) {
        logger.info("Processing game finished: Game={}, Winner={}, Final Score={}",
                event.getGameId(), event.getWinner(), event.getDisplayScore());

        // - Update leaderboards
        // - Send final notifications
        // - Archive the game
        // - Update player statistics
    }
}
