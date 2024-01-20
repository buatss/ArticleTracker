FROM node:21-alpine3.18

COPY . .
RUN rm prepare_image.sh Dockerfile readme.md

RUN apk update && apk upgrade && \
    apk add --no-cache wget xz && \
    apk --no-cache add bash

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh

RUN chmod +x /wait-for-it.sh
RUN npm install

EXPOSE 3000
ENTRYPOINT ["/entrypoint.sh"]
