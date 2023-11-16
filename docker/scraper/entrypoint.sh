#!/bin/bash
echo "Executing entrypoint.sh"

mv geckodriver /usr/local/bin/
chmod +rwx /usr/local/bin/*
export geckodriver=/usr/local/bin/geckodriver

java -jar -Dspring.profiles.active=prod /at.jar
