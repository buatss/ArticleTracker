FROM docker.io/jenkins/jenkins:latest

COPY plugins.yml /usr/share/jenkins/ref/plugins.yml
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.yml

# Instalacja Mavena
USER root
RUN apt-get update && apt-get install -y maven
USER jenkins
