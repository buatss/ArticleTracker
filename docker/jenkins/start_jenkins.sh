#!/bin/bash

workdir=$(pwd)

docker build -t jenkins_at .

mkdir jenkins_home

docker run -d -p 8080:8080 -p 50000:50000 --name jenkins_at -v ./jenkins_home:/var/jenkins_home jenkins_at
