FROM ubuntu:22.04 AS builder

# Compatibility https://firefox-source-docs.mozilla.org/testing/geckodriver/Support.html

RUN apt-get update -y && \
    apt-get install -y wget openjdk-17-jdk bzip2 libgtk-3-0 libdbus-glib-1-2 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

FROM builder AS app

WORKDIR /usr/src/app

# donwload & install firefox
RUN wget https://ftp.mozilla.org/pub/firefox/releases/115.0.2/linux-x86_64/en-US/firefox-115.0.2.tar.bz2 \
    && tar -xvjf firefox-115.0.2.tar.bz2 -C /opt \
    && ln -s /opt/firefox/firefox /usr/local/bin/firefox

# download & install geckodriver
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.33.0/geckodriver-v0.33.0-linux64.tar.gz \
    && tar -xvzf geckodriver-v0.33.0-linux64.tar.gz \
    && cp geckodriver /usr/local/bin/ \
    && chmod +x /usr/local/bin/geckodriver

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
RUN chmod +x wait-for-it.sh

COPY entrypoint.sh /entrypoint.sh
COPY at.jar /at.jar
COPY cronScript.sh /cronScript.sh

FROM app AS final
RUN chmod +x /entrypoint.sh /at.jar /cronScript.sh

ENTRYPOINT ["/entrypoint.sh"]
