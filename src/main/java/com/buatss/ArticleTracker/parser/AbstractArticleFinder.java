package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.model.MediaSite;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@Slf4j
public abstract class AbstractArticleFinder {
    protected final MediaSite mediaSite;
    protected final List<Article> articles = new ArrayList<>();
    @Autowired
    protected WebDriver driver;

    protected AbstractArticleFinder(MediaSite mediaSite) {
        this.mediaSite = mediaSite;
    }

    public abstract void findArticles();

    protected void acceptCookies(String xpath) {
        WebScraperUtils.waitRandomMilis();
        try {
            WebElement button = driver.findElement(By.xpath(xpath));
            button.click();
        } catch (NoSuchElementException e) {
            log.warn("[" + this.getMediaSite().getName() + "] Couldn't find button to accept cookies.");
            log.warn("[" + this.getMediaSite().getName() + "] " + e.getLocalizedMessage());
        }
    }
}
