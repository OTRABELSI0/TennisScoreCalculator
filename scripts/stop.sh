#!/bin/bash

set -e

echo "🛑 Stopping Tennis Scoring System..."

docker-compose down

echo "✅ All services stopped!"
