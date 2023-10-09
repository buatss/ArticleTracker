package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.parser.CookieAcceptor;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.remote.http.ConnectionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;


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
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        log.error(e.getLocalizedMessage());
                    }
                    return finder.getArticles().stream();
                })
                .filter(isNotDuplicated())
                .filter(isLinkNotTooLong())
                .forEach(saveArticleInDb());

        driver.quit();
    }

    public void scrapAllParallel(int maxActiveTabs) {
        List<AbstractArticleFinder> bufferedParsers = new ArrayList<>(List.copyOf(parsers));

        while (!bufferedParsers.isEmpty()) {
            List<AbstractArticleFinder> buffer = new ArrayList<>();

            for (int i = 0; i < maxActiveTabs && !bufferedParsers.isEmpty(); i++) {
                AbstractArticleFinder parser = bufferedParsers.remove(0);
                buffer.add(parser);
            }

            openSites(buffer);
            returnToFirstTab();
            acceptCookiesAllTabs();
            WebScraperUtils.waitRandomMilis();

            for (int i = 0; i < 4; i++) {
                scrollEachPageAndWait(buffer, 1000L);
            }
            ExecutorService executor = Executors.newFixedThreadPool(6);
            for (AbstractArticleFinder finder : buffer) {
                executor.submit(findAndSaveArticles(finder));
            }
            executor.shutdown();
            closeAllTabs();
        }
        driver.quit();
    }

    private Runnable findAndSaveArticles(AbstractArticleFinder finder) {
        return () -> {
            finder.findArticles();
            finder.getArticles()
                    .parallelStream()
                    .filter(isNotDuplicated())
                    .filter(isLinkNotTooLong())
                    .forEach(saveArticleInDb());
        };
    }

    private Consumer<Article> saveArticleInDb() {
        return article -> {
            try {
                log.trace("Article = " + article);
                repository.saveAndFlush(article);
            } catch (Exception ignored) {
            }
        };
    }

    private static Predicate<Article> isLinkNotTooLong() {
        return article -> article.getLink().length() < 255;
    }

    private Predicate<Article> isNotDuplicated() {
        return article -> repository.findByLink(article.getLink()) == null;
    }

    private void closeAllTabs() {
        Set<String> windowHandles = driver.getWindowHandles();
        String mainHandle = driver.getWindowHandle();

        for (String handle : windowHandles) {
            if (!handle.equals(mainHandle)) {
                driver.switchTo().window(handle);
                try {
                    driver.close();
                } catch (ConnectionFailedException e) {
                    log.warn(e.getLocalizedMessage());
                }
            }
        }
        driver.switchTo().window(mainHandle);
    }

    private void scrollEachPageAndWait(List<AbstractArticleFinder> buffer, Long time) {
        Set<String> handles = driver.getWindowHandles();

        for (String handle : handles) {
            driver.switchTo().window(handle);
            WebScraperUtils.randomlyScrollDown(driver);
            WebScraperUtils.waitMs(time);
        }
    }

    private void openSites(List<AbstractArticleFinder> buffer) {
        int bufferSize = buffer.size();
        for (int i = 0; i < bufferSize; i++) {
            AbstractArticleFinder parser = buffer.get(i);
            parser.openSite();

            if (i < bufferSize - 1) {
                driver.switchTo().newWindow(WindowType.TAB);
            }
        }
    }

    private void acceptCookiesAllTabs() {
        Set<String> handles = driver.getWindowHandles();

        for (String handle : handles) {
            driver.switchTo().window(handle);

            if (driver instanceof CookieAcceptor) {
                ((CookieAcceptor) driver).acceptCookies();
                WebScraperUtils.waitMs(100L);
            }
        }
    }

    private void returnToFirstTab() {
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray()[0].toString());
    }
}
