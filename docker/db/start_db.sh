#!/bin/bash

# command "./start_db.sh -v" starts container with named volume, otherwise it will be anonymous and deleted with container

if [ "$(docker info >/dev/null 2>&1 && echo "Docker is running")" = "Docker is running" ]; then
  echo "Docker engine is running, continuing execution..."
else
  echo "Docker is not running, please start docker engine."
  exit 1
fi

mkdir -p -v ./mysql

while getopts ":v" opt; do
  case $opt in
    v)
      create_volume="true"
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done

volume_option=""
if [ "$create_volume" = "true" ]; then
  volume_option="-v mysql-volume:/var/lib/mysql"
fi

docker run -d \
  --name mysql_db \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  -e MYSQL_DATABASE=my_database \
  $volume_option \
  -p 3306:3306 \
  mysql:8.0.33


container_id=$(docker ps -q -f name=mysql_db)
echo "MySQL container started with ID: $container_id"
