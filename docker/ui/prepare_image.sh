#!/bin/bash

if [ "$(docker info >/dev/null 2>&1 && echo "Docker is running")" = "Docker is running" ]; then
  echo "Docker engine is running, continuing execution..."
else
  echo "Docker is not running, please start docker engine."
  exit 1
fi

SOURCE_DIR="../../ui"
DEST_DIR="./"

cp -r "$SOURCE_DIR/." "$DEST_DIR"
rm -rf node_modules
rm -rf dist

docker build . -t at_ui
