package com.buatss.ArticleTracker.init;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.db.MediaSiteRepository;
import com.buatss.ArticleTracker.service.ArticleService;
import com.buatss.ArticleTracker.util.MediaSiteType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class ApplicationStartupRunner implements ApplicationRunner {

    @Value("${scrap.on.startup}")
    private Boolean scrapOnStartup;

    @Autowired
    ArticleRepository repositarticleRepository;
    @Autowired
    MediaSiteRepository mediaSiteRepository;
    @Autowired
    ArticleService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InetAddress localhost = InetAddress.getLocalHost();
        log.info("Current IP address: " + localhost.getHostAddress());

        log.info("Loading all media types.");
        loadMediaSites();

        if (scrapOnStartup) {
            log.info("Scrapping on startup.");
            service.scrapAll();
        }
    }

    private void loadMediaSites() {
        MediaSiteType.getAllMedias()
                .stream()
                .filter(mediaSite -> !mediaSiteRepository.existsById(mediaSite.getId()))
                .forEach(mediaSite -> mediaSiteRepository.saveAndFlush(mediaSite));
    }
}


