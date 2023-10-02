FROM docker.io/jenkins/jenkins:lts-jdk17

COPY plugins.yml /usr/share/jenkins/ref/plugins.yml
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.yml

USER root
RUN apt-get update && apt-get install -y maven

RUN apt-get update -y
RUN apt-get upgrade -y

RUN apt-get install -y wget
RUN apt-get install -y bzip2
RUN apt-get install -y libgtk-3-0
RUN apt-get install -y libdbus-glib-1-2
RUN apt-get install -y libx11-xcb1

RUN wget https://ftp.mozilla.org/pub/firefox/releases/115.0.2/linux-x86_64/en-US/firefox-115.0.2.tar.bz2
RUN tar -xvjf firefox-115.0.2.tar.bz2 -C /opt
RUN ln -s /opt/firefox/firefox /usr/local/bin/firefox

RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.33.0/geckodriver-v0.33.0-linux64.tar.gz
RUN tar -xvzf geckodriver-v0.33.0-linux64.tar.gz
RUN cp geckodriver /usr/local/bin/
RUN chmod +x /usr/local/bin/geckodriver

USER jenkins
