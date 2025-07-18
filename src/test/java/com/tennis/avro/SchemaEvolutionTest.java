package com.tennis.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SchemaEvolutionTest {

    @Test
    void shouldSerializeAndDeserializeGameEvent() throws IOException {
        // Given
        EventMetadata metadata = EventMetadata.newBuilder()
                .setVersion("1.0.0")
                .setSource("tennis-scoring-system")
                .build();

        GameEvent originalEvent = GameEvent.newBuilder()
                .setGameId("test-game")
                .setEventType(EventType.POINT_SCORED)
                .setPlayer("A")
                .setPlayerAScore(1)
                .setPlayerBScore(0)
                .setDisplayScore("Player A : 15 / Player B : 0")
                .setIsFinished(false)
                .setWinner(null)
                .setTimestamp(System.currentTimeMillis())
                .setMetadata(metadata)
                .build();

        // When - Serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GameEvent> writer = new SpecificDatumWriter<>(GameEvent.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(originalEvent, encoder);
        encoder.flush();
        out.close();

        // Then - Deserialize
        DatumReader<GameEvent> reader = new SpecificDatumReader<>(GameEvent.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GameEvent deserializedEvent = reader.read(null, decoder);

        // Verify
        assertEquals(originalEvent.getGameId(), deserializedEvent.getGameId());
        assertEquals(originalEvent.getEventType(), deserializedEvent.getEventType());
        assertEquals(originalEvent.getPlayer(), deserializedEvent.getPlayer());
        assertEquals(originalEvent.getPlayerAScore(), deserializedEvent.getPlayerAScore());
        assertEquals(originalEvent.getPlayerBScore(), deserializedEvent.getPlayerBScore());
        assertEquals(originalEvent.getMetadata().getVersion(), deserializedEvent.getMetadata().getVersion());
    }

    @Test
    void shouldHaveCorrectSchemaStructure() {
        Schema schema = GameEvent.getClassSchema();

        // Verify required fields exist
        assertNotNull(schema.getField("gameId"));
        assertNotNull(schema.getField("eventType"));
        assertNotNull(schema.getField("playerAScore"));
        assertNotNull(schema.getField("playerBScore"));
        assertNotNull(schema.getField("timestamp"));
        assertNotNull(schema.getField("metadata"));

        // Verify optional fields
        assertTrue(schema.getField("player").hasDefaultValue());
        assertTrue(schema.getField("winner").hasDefaultValue());

        // Verify enum
        Schema eventTypeSchema = schema.getField("eventType").schema();
        assertEquals(Schema.Type.ENUM, eventTypeSchema.getType());
        assertTrue(eventTypeSchema.getEnumSymbols().contains("POINT_SCORED"));
        assertTrue(eventTypeSchema.getEnumSymbols().contains("GAME_FINISHED"));
    }
}