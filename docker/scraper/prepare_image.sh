#!/bin/bash

workdir=$(pwd)

if [ "$(docker info >/dev/null 2>&1 && echo "Docker is running")" = "Docker is running" ]; then
  echo "Docker engine is running, continuing execution..."
else
  echo "Docker is not running, please start docker engine."
  exit 1
fi

cd ../../scraper/

./mvnw clean package

jar_file=$(find target -name "ArticleTracker*.jar" -print -quit)

if [ -n "$jar_file" ]; then
  cp "$jar_file" ../docker/scraper/at.jar
  echo "Copied $jar_file to docker/scraper as at.jar"
else
  echo "Error: ArticleTracker.jar not found in target directory!"
  exit 1
fi

cp entrypoint.sh ../docker/scraper/entrypoint.sh

cd "$workdir"
echo "Clearing previous at image"
docker image rm at 2>/dev/null || true

echo "Creating new at image"
docker build -t at .
