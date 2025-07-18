# Tennis Scoring System

A modern tennis scoring system built with Java 21, Spring Boot 3, Kafka, and hexagonal architecture.

## Features

- ✅ Tennis game scoring logic with proper deuce handling
- ✅ Hexagonal architecture for clean separation of concerns
- ✅ Event-driven architecture with Kafka
- ✅ Avro schema for type-safe messaging
- ✅ Comprehensive testing (unit, integration, performance)
- ✅ Monitoring and metrics with Micrometer
- ✅ Docker containerization

## Quick Start

### Prerequisites

- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Running the Application

1. Clone the repository
```bash
git clone <your-repo-url>
cd tennis-scoring-system

## Docker Deployment

### Quick Start with Docker

1. **Build and deploy everything:**
```bash
./scripts/deploy.sh

### Test the Application
# Run all tests
mvn test

### Monitoring
Services
Tennis App: http://localhost:8090
Kafka UI: http://localhost:8080
Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin/admin)