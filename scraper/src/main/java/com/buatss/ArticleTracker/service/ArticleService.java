package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.*;
import com.buatss.ArticleTracker.model.*;
import com.buatss.ArticleTracker.parser.*;
import com.buatss.ArticleTracker.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {
    @Autowired
    List<AbstractArticleFinder> parsers;
    @Autowired
    WebDriver driver;
    @Autowired
    private ArticleRepository repository;
    private final CyclicBarrier barrier = new CyclicBarrier(6);
    private final Boolean runStandalone = Boolean.valueOf(System.getenv("standalone"));

    public void scrapAllSequential() {
        parsers
                .stream()
                .map(AbstractArticleFinder::getArticles)
                .flatMap(waitAndStreamArticles())
                .filter(isNotDuplicated())
                .filter(isLinkNotTooLong())
                .forEach(saveArticle());
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
                scrollEachPageAndWait(1000L);
            }
            findArticlesInEachPage(buffer);
            saveFoundArticles(buffer);
            closeAllTabs();
        }
        driver.quit();
    }

    private void findArticlesInEachPage(List<AbstractArticleFinder> buffer) {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            Optional<AbstractArticleFinder> finder = buffer
                    .parallelStream()
                    .filter(p -> driver.getCurrentUrl().contains(p.getMediaSite().getLink()))
                    .findFirst();
            if (finder.isPresent()) {
                log.trace("Finding articles for " + finder.get().getMediaSite().toString());
                finder.get().findArticles();
            } else {
                log.error("Couldn't match parser to URL:" + driver.getCurrentUrl());
            }
        }
    }

    private void scrollEachPageAndWait(Long time) {
        Set<String> handles = driver.getWindowHandles();

        for (String handle : handles) {
            driver.switchTo().window(handle);
            WebScraperUtils.randomlyScrollDown(driver);
            WebScraperUtils.waitMs(time);
        }
    }

    private static Function<List<Article>, Stream<? extends Article>> waitAndStreamArticles() {
        return a ->
        {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage());
            }
            return a.stream();
        };
    }

    private void saveFoundArticles(List<AbstractArticleFinder> buffer) {
        buffer
                .parallelStream()
                .map(AbstractArticleFinder::getArticles)
                .flatMap(Collection::parallelStream)
                .filter(isNotDuplicated())
                .filter(isLinkNotTooLong())
                .forEach(saveArticle());
    }

    private Consumer<Article> saveArticle() {
        if (runStandalone) {
            return article -> {
                try {
                    log.trace("Article = " + article);
                    ArticleSaver.saveArticleToFile(article);
                } catch (Exception ignored) {
                }
            };
        } else {
            return article -> {
                log.trace("Article = " + article);
                repository.saveAndFlush(article);
            };
        }
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
                } catch (Exception e) {
                    log.trace(e.getLocalizedMessage());
                }
            }
        }
        driver.switchTo().window(mainHandle);
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
