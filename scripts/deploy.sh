#!/bin/bash

set -e

echo "🎾 Deploying Tennis Scoring System..."

# Check if JAR file exists, if not build it
if [ ! -f "target/tennis-scoring-system-1.0.0.jar" ]; then
    echo "JAR file not found. Building application..."
    ./scripts/build.sh
else
    echo "JAR file found. Building Docker image..."
    docker build -t tennis-scoring-system:latest .
fi

# Start all services
echo "Starting services..."
docker-compose up -d

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 30

# Check health
echo "Checking service health..."
docker-compose ps

echo ""
echo "✅ Deployment completed successfully!"
echo ""
echo "🎾 Tennis Scoring System is now running!"
echo ""
echo "📊 Application: http://localhost:8090"
echo "🔍 Health Check: http://localhost:8090/actuator/health"
echo "📈 Metrics: http://localhost:8090/actuator/metrics"
echo "🎛️  Kafka UI: http://localhost:8080"
echo ""
echo "🧪 Test the API:"
echo "curl -X POST http://localhost:8090/api/tennis/games \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"ballSequence\": \"ABABAA\"}'"