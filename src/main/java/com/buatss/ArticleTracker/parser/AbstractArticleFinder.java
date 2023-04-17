package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.model.MediaSite;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractArticleFinder {
    @Autowired
    protected WebDriver driver;
    protected final MediaSite mediaSite;

    protected final List<Article> articles = new ArrayList<>();

    protected AbstractArticleFinder(MediaSite mediaSite) {
        this.mediaSite = mediaSite;
    }

    public abstract void findArticles();
}
