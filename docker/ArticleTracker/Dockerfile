FROM ubuntu:22.04

# Compatibility https://firefox-source-docs.mozilla.org/testing/geckodriver/Support.html

# update and install dependencies
RUN apt-get update -y
RUN apt-get upgrade -y

RUN apt-get install -y wget
RUN apt-get install -y openjdk-17-jdk

# for decompressing downloads
RUN apt-get install -y bzip2

# gui tools
RUN apt-get install -y libgtk-3-0

# allows processes to communicate
RUN apt-get install -y libdbus-glib-1-2

# donwload & install firefox
RUN wget https://ftp.mozilla.org/pub/firefox/releases/115.0.2/linux-x86_64/en-US/firefox-115.0.2.tar.bz2 \
    && tar -xvjf firefox-115.0.2.tar.bz2 -C /opt \
    && ln -s /opt/firefox/firefox /usr/local/bin/firefox

# download & install geckodriver
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.33.0/geckodriver-v0.33.0-linux64.tar.gz \
    && tar -xvzf geckodriver-v0.33.0-linux64.tar.gz \
    && cp geckodriver /usr/local/bin/ \
    && chmod +x /usr/local/bin/geckodriver

COPY entrypoint.sh /entrypoint.sh
COPY at.jar /at.jar
RUN chmod +x /entrypoint.sh
RUN chmod +x /at.jar

ENTRYPOINT ["/entrypoint.sh"]
