package com.tennis.infrastructure.messaging;

import com.tennis.avro.EventType;
import com.tennis.avro.GameEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableKafkaStreams
@Profile("kafka")
public class GameEventStreamsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GameEventStreamsProcessor.class);

    @Value("${spring.kafka.schema-registry-url}")
    private String schemaRegistryUrl;

    @Bean
    public KStream<String, GameEvent> processGameEvents(StreamsBuilder streamsBuilder) {

        // Configure Avro Serde
        final SpecificAvroSerde<GameEvent> gameEventSerde = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap(
                "schema.registry.url", schemaRegistryUrl);
        gameEventSerde.configure(serdeConfig, false);

        // Create the main stream
        KStream<String, GameEvent> gameEvents = streamsBuilder.stream(
                "tennis-game-events",
                Consumed.with(Serdes.String(), gameEventSerde)
        );

        // Log all events
        gameEvents.foreach((key, value) ->
                logger.info("Processing event: gameId={}, type={}, player={}",
                        value.getGameId(), value.getEventType(), value.getPlayer())
        );

        // Filter and route finished games
        gameEvents
                .filter((key, value) -> value.getIsFinished())
                .to("tennis-finished-games", Produced.with(Serdes.String(), gameEventSerde));

        // Filter and route live scoring events
        gameEvents
                .filter((key, value) -> !value.getIsFinished())
                .to("tennis-live-scores", Produced.with(Serdes.String(), gameEventSerde));

        // Create aggregated statistics
        gameEvents
                .filter((key, value) -> value.getEventType() == EventType.POINT_SCORED)
                .groupByKey()
                .count()
                .toStream()
                .foreach((gameId, pointCount) ->
                        logger.info("Game {} has {} points scored", gameId, pointCount)
                );

        return gameEvents;
    }
}