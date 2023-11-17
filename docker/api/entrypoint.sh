#!/bin/bash
echo "Executing entrypoint.sh"

export PATH=$PATH:/usr/local/nodejs/bin
npm install

./wait-for-it.sh -h mysql_db -p 3306 -t 60
node app.js
