package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository repository;
    @Autowired
    List<AbstractArticleFinder> parsers;

    public void scrapAll() {
        parsers
                .stream()
                .flatMap(finder -> {
                    finder.findArticles();
                    return finder.getArticles().stream();
                })
                .filter(article -> repository.findByLink(article.getLink()) == null)
                .filter(article -> article.getLink().length() < 255)
                .forEach(article -> {
                    System.out.println(article);
                    try {
                        repository.saveAndFlush(article);
                    } catch (Exception ignored) {
                    }
                });

        parsers.get(0).getDriver().quit();
    }
}
