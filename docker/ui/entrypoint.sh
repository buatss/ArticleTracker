#!/bin/bash
echo "Executing entrypoint.sh"

./wait-for-it.sh -h mysql_db -p 3306 -t 60
npm run dev
