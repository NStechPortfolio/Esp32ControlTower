#!/bin/bash

set -e

cd ..
./gradlew build -x test
docker compose up -d --build
