#!/bin/bash
set -e

docker compose up -d

echo ""
echo "Docker Deploy complete"
echo "http://localhost:3000  -> Frontend"
echo "http://localhost:8888  -> API Gateway"
echo "http://localhost:8080  -> Keycloak"
echo "http://localhost:8761  -> Eureka Dashboard"
echo "http://localhost:9001  -> MinIO Console"
echo "http://localhost:9411  -> Zipkin"
