#!/bin/bash

set -e

echo "🎾 Building Tennis Scoring System..."

# Clean and test
echo "Running tests..."
mvn clean test

# Package application
echo "Packaging application..."
mvn package -DskipTests

# Check if JAR file exists
if [ ! -f "target/tennis-scoring-system-1.0.0.jar" ]; then
    echo "❌ JAR file not found! Build may have failed."
    exit 1
fi

echo "✅ JAR file created: $(ls -lh target/tennis-scoring-system-1.0.0.jar)"

# Build Docker image
echo "Building Docker image..."
docker build -t tennis-scoring-system:latest .

echo "✅ Build completed successfully!"
echo ""
echo "🚀 To run the application:"
echo "   ./scripts/deploy.sh"