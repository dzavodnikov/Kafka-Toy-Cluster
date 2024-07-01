#!/usr/bin/env bash

echo "Start Kafka cluster and build the project..."

cd .. && mvn clean install
docker compose up -d --wait
