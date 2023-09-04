package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.model.MediaSite;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public abstract class AbstractArticleFinder {
    protected final MediaSite mediaSite;
    protected final List<Article> articles = new ArrayList<>();
    @Autowired
    protected WebDriver driver;

    protected AbstractArticleFinder(MediaSite mediaSite) {
        this.mediaSite = mediaSite;
    }

    public abstract void findArticles();
}
