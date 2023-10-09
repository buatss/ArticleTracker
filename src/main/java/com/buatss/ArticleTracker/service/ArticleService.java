package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    @Autowired
    List<AbstractArticleFinder> parsers;
    @Autowired
    WebDriver driver;
    @Autowired
    private ArticleRepository repository;

    public void scrapAllSequential() {
        parsers
                .stream()
                .flatMap(finder -> {
                    finder.findArticles();
                    return finder.getArticles().stream();
                })
                .filter(article -> repository.findByLink(article.getLink()) == null)
                .filter(article -> article.getLink().length() < 255)
                .forEach(article -> {
                    try {
                        log.trace("Article = " + article);
                        repository.saveAndFlush(article);
                    } catch (Exception ignored) {
                    }
                });

        parsers.get(0).getDriver().quit();
    }

    public void scrapAllParallel(int maxActiveTabs) {
        parsers = parsers.subList(0, 3);
        List<String> activeTabs = new ArrayList<>();

        //open tabs
        parsers.forEach(p -> {
            p.openSite();
            driver.switchTo().newWindow(WindowType.TAB);
        });
        //accept cookies everywhere possible
        //loop 3 times: scroll each tab, wait after all then return to tab 1
        //parse all data and save to db in parallel

        //open next tabs and repeat

//        parsers
//                .stream()
//                .flatMap(finder -> {
//                    finder.findArticles();
//                    return finder.getArticles().stream();
//                })
//                .filter(article -> repository.findByLink(article.getLink()) == null)
//                .filter(article -> article.getLink().length() < 255)
//                .forEach(article -> {
//                    try {
//                        log.trace("Article = " + article);
//                        repository.saveAndFlush(article);
//                    } catch (Exception ignored) {
//                    }
//                });

//        parsers.get(0).getDriver().quit();
    }
}
