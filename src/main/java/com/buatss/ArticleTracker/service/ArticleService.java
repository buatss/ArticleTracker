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
        parsers.forEach(parser -> {
            parser.findArticles();
            parser.getArticles()
                    .stream()
                    .filter(article -> repository.findByLink(article.getLink()) == null)
                    .filter(article -> article.getLink().length() < 255)
                    .forEach(article -> repository.saveAndFlush(article));
        });
    }
}
