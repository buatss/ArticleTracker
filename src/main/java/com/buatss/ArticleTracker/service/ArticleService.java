package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.parser.WpParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository repository;
    @Autowired
    WpParser parser;

    public void scrapWp() {
        parser.findArticles();
        parser.getArticles()
                .stream()
                .filter(article -> repository.findByLink(article.getLink()) == null)
                .forEach(article -> repository.saveAndFlush(article));
    }
}
