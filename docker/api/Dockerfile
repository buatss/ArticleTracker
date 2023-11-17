FROM ubuntu:22.04

COPY . .

RUN apt update -y && apt upgrade -y
RUN apt-get install wget -y
RUN apt-get install xz-utils -y
RUN wget https://nodejs.org/dist/v20.9.0/node-v20.9.0-linux-x64.tar.xz

RUN tar -xJf node-v20.9.0-linux-x64.tar.xz
RUN rm node-v20.9.0-linux-x64.tar.xz
RUN mv node-v20.9.0-linux-x64 /usr/local/nodejs

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
RUN chmod +x wait-for-it.sh

EXPOSE 3000
ENTRYPOINT ["/entrypoint.sh"]
