#!/bin/bash

set -e

BASE_URL="http://localhost:8090"

echo "🎾 Testing Tennis Scoring System with Avro"
echo "=========================================="

# Wait for application to be ready
echo "Waiting for application to be ready..."
for i in {1..30}; do
    if curl -s $BASE_URL/actuator/health > /dev/null; then
        echo "✅ Application is ready!"
        break
    fi
    echo "⏳ Waiting... ($i/30)"
    sleep 2
done

# Test game with Kafka events
echo ""
echo "🎮 Playing a game (this will generate Avro events)..."
curl -X POST $BASE_URL/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}' \
  | jq '.'

echo ""
echo "📊 Check Kafka UI at http://localhost:8080 to see Avro messages"
echo "🔍 Check Schema Registry at http://localhost:8081/subjects"

# Check if schema is registered
echo ""
echo "📋 Checking registered schemas..."
curl -s http://localhost:8081/subjects | jq '.'

echo ""
echo "🎾 Checking GameEvent schema..."
curl -s http://localhost:8081/subjects/tennis-game-events-value/versions/latest | jq '.schema' | jq -r '.' | jq '.'

echo ""
echo "✅ Avro integration test completed!"