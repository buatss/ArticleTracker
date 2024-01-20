FROM node:20-alpine

WORKDIR /app

COPY . .
RUN rm prepare_image.sh Dockerfile README.md

RUN apk update && apk upgrade && \
    apk add --no-cache wget && \
    apk --no-cache add bash

RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh
RUN npm i
RUN npm run build

EXPOSE 5173
ENTRYPOINT ["/app/entrypoint.sh"]
