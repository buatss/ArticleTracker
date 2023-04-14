package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractArticleFinder {
    @Autowired
    protected WebDriver driver;
    protected final List<Article> articles = new ArrayList<>();
    public abstract void findArticles();
}
