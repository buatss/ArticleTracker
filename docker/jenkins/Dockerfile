FROM docker.io/jenkins/jenkins:lts-jdk17

COPY plugins.yml /usr/share/jenkins/ref/plugins.yml
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.yml

USER root
RUN apt-get update && apt-get install -y maven
USER jenkins
