#!/bin/sh
echo "Executing entrypoint.sh"

cp geckodriver /usr/local/bin/
chmod +rwx /usr/local/bin/*

java -jar at.jar -Dspring.profiles.active=prod
