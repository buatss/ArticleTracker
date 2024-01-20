#!/bin/bash

WORKDIR="$PWD"
LOG_FILE="$WORKDIR/logs.txt"
AT_JAR_PATH="$WORKDIR/at.jar"

if [ ! -e "$LOG_FILE" ]; then
  touch "$LOG_FILE"
  echo "Created empty log file: $LOG_FILE"
fi

FIRST_DATE=$(grep -oE '[0-9]{4}-[0-9]{2}-[0-9]{2}' "$LOG_FILE" | head -n 1)
CURRENT_DATE=$(date "+%Y-%m-%d")

if [ "$FIRST_DATE" == "$CURRENT_DATE" ]; then
  echo "The date in the logs is the same as the current date. No actions are performed." | tee >> "$LOG_FILE" /dev/tty
else
  echo "The date in the logs is different from the current date. Running the java -jar at.jar script."
  sleep 60
  java -jar -Dspring.profiles.active=prod "$AT_JAR_PATH" > "$LOG_FILE" 2>&1
  echo "Execution script done."
fi
