package com.buatss.ArticleTracker.init;

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

    @Autowired
    MediaSiteRepository mediaSiteRepository;
    @Autowired
    ArticleService service;
    @Value("${scrap.on.startup}")
    private Boolean scrapOnStartup;
    @Value("${scrap.parallel.on.startup}")
    private Boolean scrapParallelOnStartup;
    @Value("${exit.after.scrap.on.startup}")
    private Boolean exitAfterScrapOnStartup;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        InetAddress localhost = InetAddress.getLocalHost();
        log.info("Current IP address: " + localhost.getHostAddress());

        log.info("Loading all media types.");
        loadMediaSites();

        if (scrapOnStartup) {
            log.info("Scrapping on startup.");
            if (scrapParallelOnStartup) {
                service.scrapAllParallel(6);
            } else {
                service.scrapAllSequential();
            }
        }

        if (exitAfterScrapOnStartup) {
            System.exit(0);
        }

        if (exitAfterScrapOnStartup) {
            System.exit(0);
        }
    }

    private void loadMediaSites() {
        MediaSiteType.getAllMedias()
                .stream()
                .peek(mediaSite -> log.info("Loading media site=" + mediaSite.getLink()))
                .filter(mediaSite -> !mediaSiteRepository.existsById(mediaSite.getId()))
                .forEach(mediaSite -> mediaSiteRepository.saveAndFlush(mediaSite));
    }
}


