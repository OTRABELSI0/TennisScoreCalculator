# Tennis Scoring System

A comprehensive tennis scoring system built with Java 21, Spring Boot 3, Apache Kafka with Avro, and hexagonal architecture. This project demonstrates enterprise-level development practices including event-driven architecture, schema evolution, comprehensive testing, and containerization.

## 🎾 Features

- ✅ **Complete Tennis Scoring Logic** - Implements standard tennis rules including deuce scenarios
- ✅ **Hexagonal Architecture** - Clean separation between domain, application, and infrastructure layers
- ✅ **Event-Driven Architecture** - Apache Kafka integration with Avro schema
- ✅ **API Documentation** - OpenAPI/Swagger integration
- ✅ **Comprehensive Testing** - Unit, integration, and performance tests
- ✅ **Monitoring & Metrics** - Custom metrics with Micrometer and Prometheus
- ✅ **Docker Containerization** - Full Docker Compose setup
- ✅ **Schema Registry** - Type-safe messaging with schema evolution
- ✅ **CI/CD Ready** - GitHub Actions workflow included

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │    │     Domain      │    │ Infrastructure  │
│                 │    │                 │    │                 │
│  ┌───────────┐  │    │  ┌───────────┐  │    │  ┌───────────┐  │
│  │    DTOs   │  │    │  │   Models  │  │    │  │Controllers│  │
│  │           │  │    │  │           │  │    │  │           │  │
│  │ Use Cases │  │    │  │ Services  │  │    │  │   Kafka   │  │
│  │           │  │    │  │           │  │    │  │           │  │
│  └───────────┘  │    │  │   Ports   │  │    │  │Repository │  │
│                 │    │  │           │  │    │  │           │  │
└─────────────────┘    │  └───────────┘  │    │  └───────────┘  │
                       │                 │    │                 │
                       └─────────────────┘    └─────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Git**

### 1. Clone the Repository

```bash
git clone git@github.com:OTRABELSI0/TennisScoreCalculator.git
cd tennis-scoring-system
```

### 2. Build the Application

```bash
# Build and run tests
mvn clean test

# Package the application
mvn clean package -DskipTests
```

### 3. Run with Docker (Recommended)

```bash
# Deploy everything (Kafka, Schema Registry, Application)
./scripts/deploy.sh
```

### 4. Run Locally (Development)

```bash
# Run without Kafka (simple mode)
mvn spring-boot:run

# Run with Kafka
mvn spring-boot:run -Dspring.profiles.active=kafka
```

## 🧪 Testing Guide

### Step 1: Unit Tests

Run the core business logic tests:

```bash
# Run all unit tests
mvn test

# Run specific test classes
mvn test -Dtest=ScoreTest
mvn test -Dtest=GameStateTest
mvn test -Dtest=TennisGameServiceTest

# Run tests with coverage
mvn clean test jacoco:report
# View coverage: target/site/jacoco/index.html
```

**Expected Output:**
```
[INFO] Tests run: 38, Failures: 0, Errors: 0, Skipped: 0
```

### Step 2: Integration Tests

Test the REST API endpoints:

```bash
# Run integration tests
mvn test -Dtest="*IntegrationTest"

# Run controller tests specifically
mvn test -Dtest=TennisGameControllerTest
```

### Step 3: API Testing (Manual)

#### Start the Application
```bash
# Option A: Simple mode (no Kafka)
mvn spring-boot:run

# Option B: With Docker
./scripts/deploy.sh
```

#### Test Basic Game API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Play a simple game
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}' | jq '.'
```

**Expected Response:**
```json
{
  "gameId": "uuid-here",
  "scoreProgression": [
    "Player A : 15 / Player B : 0",
    "Player A : 15 / Player B : 15",
    "Player A : 30 / Player B : 15",
    "Player A : 30 / Player B : 30",
    "Player A : 40 / Player B : 30",
    "Player A wins the game"
  ],
  "isFinished": true,
  "winner": "Player A"
}
```

#### Test Different Scenarios
```bash
# Deuce scenario
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAB"}' | jq '.'

# Advantage scenario
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABA"}' | jq '.'

# Long deuce game
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABBAAA"}' | jq '.'

# Get tennis rules
curl http://localhost:8080/api/tennis/rules | jq '.'

# Get statistics
curl http://localhost:8080/api/tennis/stats | jq '.'
```

#### Test Error Handling
```bash
# Invalid input
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "INVALID"}' | jq '.'

# Empty input
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": ""}' | jq '.'
```

### Step 4: Docker Testing

#### Deploy with Docker
```bash
# Deploy full stack
./scripts/deploy.sh

# Check service status
docker-compose ps

# View logs
docker-compose logs tennis-app
docker-compose logs tennis-kafka
```

#### Test Docker Deployment
```bash
# Wait for services to be ready
./scripts/test-api.sh

# Manual testing
curl http://localhost:8090/api/tennis/games \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}' | jq '.'
```

### Step 5: Kafka & Avro Testing

#### Prerequisites
```bash
# Ensure Docker services are running
docker-compose up -d
```

#### Test Kafka Integration
```bash
# Test with Kafka profile
curl -X POST http://localhost:8090/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}' | jq '.'

# Check Kafka UI
open http://localhost:8080

# Check Schema Registry
curl http://localhost:8081/subjects | jq '.'

# Get GameEvent schema
curl http://localhost:8081/subjects/tennis-game-events-value/versions/latest | jq '.schema' | jq -r '.' | jq '.'
```

#### Test Avro Schema
```bash
# Run Avro-specific tests
mvn test -Dtest=SchemaEvolutionTest

# Test schema generation
./scripts/test-avro.sh
```

### Step 6: Monitoring & Metrics Testing

#### Check Application Metrics
```bash
# Health endpoint
curl http://localhost:8080/actuator/health | jq '.'

# All metrics
curl http://localhost:8080/actuator/metrics | jq '.'

# Custom tennis metrics
curl http://localhost:8080/actuator/metrics/tennis.games.played | jq '.'
curl http://localhost:8080/actuator/metrics/tennis.points.scored | jq '.'
curl http://localhost:8080/actuator/metrics/tennis.games.won | jq '.'

# Prometheus format
curl http://localhost:8080/actuator/prometheus
```

#### With Docker (Full Monitoring Stack)
```bash
# Access monitoring tools
open http://localhost:8090  # Application
open http://localhost:8080  # Kafka UI
open http://localhost:9090  # Prometheus
open http://localhost:3000  # Grafana (admin/admin)
```

### Step 7: API Documentation Testing

#### Swagger UI
```bash
# Open API documentation
open http://localhost:8080/swagger-ui.html

# Get OpenAPI spec
curl http://localhost:8080/api-docs | jq '.'
```

## 📊 Service Endpoints

| Service | URL | Description |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | Main application (local) |
| **Application (Docker)** | http://localhost:8090 | Main application (Docker) |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | API Documentation |
| **Health Check** | http://localhost:8080/actuator/health | Application health |
| **Metrics** | http://localhost:8080/actuator/metrics | Application metrics |
| **Kafka UI** | http://localhost:8080 | Kafka management UI |
| **Schema Registry** | http://localhost:8081 | Avro schema registry |
| **Prometheus** | http://localhost:9090 | Metrics collection |
| **Grafana** | http://localhost:3000 | Metrics visualization |

## 🎮 Tennis Rules Implementation

### Basic Scoring
- **0 points**: "0"
- **1 point**: "15"
- **2 points**: "30"
- **3 points**: "40"
- **4+ points**: Game won (if leading by 2+)

### Special Scenarios
- **Deuce**: Both players at 40 points
- **Advantage**: One player ahead by 1 point after deuce
- **Game Won**: Player with advantage wins next point, or player reaches 4+ points with 2+ point lead

### Input Format
- **'A'**: Player A wins the ball
- **'B'**: Player B wins the ball
- **Example**: `"ABABAA"` means A scores, B scores, A scores, B scores, A scores, A scores (A wins 4-2)

## 🛠️ Development Commands

### Build Commands
```bash
# Clean build
mvn clean compile

# Generate Avro classes
mvn avro:schema

# Run tests
mvn test

# Package
mvn package -DskipTests

# Build Docker image
docker build -t tennis-scoring-system:latest .
```

### Development Server
```bash
# Run in development mode
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=kafka

# Run with debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Docker Commands
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f tennis-app

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose down && docker-compose up -d --build
```

## 🧪 Test Scenarios

### Scenario 1: Simple Game
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "AAAA"}'
```
**Expected**: Player A wins 4-0

### Scenario 2: Close Game
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAA"}'
```
**Expected**: Player A wins 4-2 with score progression

### Scenario 3: Deuce
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABAB"}'
```
**Expected**: Game ends in deuce (3-3)

### Scenario 4: Advantage
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABA"}'
```
**Expected**: Player A has advantage

### Scenario 5: Back to Deuce
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABAB"}'
```
**Expected**: Advantage A, then back to deuce

### Scenario 6: Win from Advantage
```bash
curl -X POST http://localhost:8080/api/tennis/games \
  -H "Content-Type: application/json" \
  -d '{"ballSequence": "ABABABAA"}'
```
**Expected**: Player A wins after having advantage

## 🔍 Troubleshooting

### Common Issues

#### 1. Tests Failing
```bash
# Check Java version
java -version  # Should be 21+

# Clean and rebuild
mvn clean compile

# Run specific failing test
mvn test -Dtest=FailingTestClass -X
```

#### 2. Docker Issues
```bash
# Check Docker is running
docker --version
docker-compose --version

# Clean Docker state
docker-compose down
docker system prune -f
docker-compose up -d
```

#### 3. Port Conflicts
```bash
# Check what's using ports
lsof -i :8080
lsof -i :9092

# Kill processes if needed
kill -9 <PID>
```

#### 4. Kafka Connection Issues
```bash
# Check Kafka is running
docker-compose logs kafka

# Test Kafka connectivity
docker exec tennis-kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### 5. Schema Registry Issues
```bash
# Check Schema Registry
curl http://localhost:8081/subjects

# Check Schema Registry logs
docker-compose logs schema-registry
```

### Debug Mode

#### Enable Debug Logging
```yaml
# Add to application.yml
logging:
  level:
    com.tennis: DEBUG
    org.apache.kafka: DEBUG
```

#### Remote Debugging
```bash
# Start with debug port
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Connect IDE to localhost:5005
```

## 📈 Monitoring

### Application Metrics
- `tennis.games.played` - Total games played
- `tennis.points.scored` - Total points scored
- `tennis.games.won` - Games won by player (tagged)
- `tennis.game.