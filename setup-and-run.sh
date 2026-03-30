#!/bin/bash
# Load .env and start Spring Boot
set -a && source .env && set +a
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m"
