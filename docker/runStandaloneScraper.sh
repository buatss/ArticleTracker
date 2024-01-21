#!/bin/bash
cd scraper
./prepare_image.sh
cd ..
docker run -d --name at \
    -p 8080:8080 \
    -v $(pwd)/ArticleTracker/at:/data \
    -e geckodriver=/usr/local/bin/geckodriver \
    -e spring.datasource.url=jdbc:mysql://mysql_db:3306/at_database \
    -e standalone=true \
    at \
    tail -f /dev/null
