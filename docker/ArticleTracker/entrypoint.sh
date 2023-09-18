#!/bin/sh
echo "Executing entrypoint.sh"

cp geckodriver /usr/local/bin/
chmod +rwx /usr/local/bin/*
export geckodriver=/usr/local/bin/geckodriver

java -jar at.jar -Dspring.profiles.active=prod
