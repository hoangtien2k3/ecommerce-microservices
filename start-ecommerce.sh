#!/bin/bash
set -e

# Setup .env
if [ ! -f .env ]; then
  cp .env.example .env
  echo ".env created from .env.example"
  exit 1
fi

docker compose up -d

echo "🌐http://localhost:3000 -> Frontend"
echo "🔌http://localhost:8888 -> API Gateway"
echo "🔐http://localhost:8080 -> Keycloak"
echo "📡http://localhost:8761 -> Eureka Dashboard"
echo "🗄️http://localhost:9001 -> MinIO Console"
echo "🔍http://localhost:9411 -> Zipkin"
