#!/bin/bash

set -e

BASE_URL="http://localhost:8090"

echo "🎾 Testing Tennis Scoring System API"
echo "=================================="

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

# Test 1: Simple game
echo ""
echo "Test 1: Simple game (ABABAA)"
curl -X POST $BASE_URL/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}' \
  | jq '.'

# Test 2: Deuce scenario
echo ""
echo "Test 2: Deuce scenario (ABABAB)"
curl -X POST $BASE_URL/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAB"}' \
  | jq '.'

# Test 3: Advantage scenario
echo ""
echo "Test 3: Advantage scenario (ABABABA)"
curl -X POST $BASE_URL/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABA"}' \
  | jq '.'

# Test 4: Health check
echo ""
echo "Test 4: Health check"
curl -X GET $BASE_URL/actuator/health | jq '.'

# Test 5: Metrics
echo ""
echo "Test 5: Custom metrics"
curl -X GET $BASE_URL/actuator/metrics/tennis.games.played | jq '.'

echo ""
echo "✅ All tests completed!"