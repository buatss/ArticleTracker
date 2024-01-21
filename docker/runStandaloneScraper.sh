#!/bin/bash
cd scraper
./prepare_image.sh
cd ..
docker run --rm -d --name at \
    -p 8080:8080 \
    -v ./scraper:/usr/src/app/articles.json \
    -e geckodriver=/usr/local/bin/geckodriver \
    -e spring.datasource.url=jdbc:mysql://mysql_db:3306/at_database \
    -e standalone=true \
    at \
    tail -f /dev/null