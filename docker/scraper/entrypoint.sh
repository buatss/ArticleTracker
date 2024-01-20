#!/bin/bash
echo "Executing entrypoint.sh"

mv geckodriver /usr/local/bin/
chmod +rwx /usr/local/bin/*
export geckodriver=/usr/local/bin/geckodriver

./wait-for-it.sh -h mysql_db -p 3306 -t 60
java -jar -Dspring.profiles.active=prod /at.jar
