#!/bin/bash

if [ "$1" = "-v" ]; then
  docker volume rm mysql-volume
fi

mkdir -p logs

if [ "$(docker ps -q -f name=mysql_db)" ]; then
  docker stop mysql_db
  docker rm mysql_db
fi

docker volume prune --force

echo "MySQL container stopped and removed."
