package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractArticleFinder {
    protected final List<Article> articles = new ArrayList<>();

    public abstract void findArticles();
}
