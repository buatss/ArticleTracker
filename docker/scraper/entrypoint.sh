#!/bin/sh
echo "Executing entrypoint.sh"

mv geckodriver /usr/local/bin/
chmod +rwx /usr/local/bin/*
export geckodriver=/usr/local/bin/geckodriver

if [ "$standalone" = "false" ]; then
    ./wait-for-it.sh -h mysql_db -p 3306 -t 60
else
    echo "Running in standalone mode."
fi

java -jar -Dspring.profiles.active=standalone /at.jar
