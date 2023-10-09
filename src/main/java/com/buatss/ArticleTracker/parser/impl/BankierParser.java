package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.parser.CookieAcceptor;
import com.buatss.ArticleTracker.util.MediaSiteType;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.buatss.ArticleTracker.util.WebScraperUtils.randomlyScrollPage;

@Component
@Slf4j
public class BankierParser extends AbstractArticleFinder implements CookieAcceptor {
    public BankierParser() {
        super(MediaSiteType.BANKIER.getMediaSite());
    }

    @Override
    public void findArticles() {
        randomlyScrollPage(driver);

        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .stream()
                .filter(hasLink())
                .filter(hasArticle())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasLink() {
        return element -> element.hasAttr("href") && (element.attr("href").contains("bankier.pl/")
                || element.attr("href").startsWith("/"));
    }

    private Predicate<Element> hasArticle() {
        return element -> element.hasText();
    }

    private Function<Element, Article> createArticle() {
        return e -> new Article(
                null,
                e.hasText() ? e.text() : Objects.requireNonNull(e.select("span.m-title-with-label-item__title").first())
                        .text(),
                buildArticleLink(mediaSite.getLink(), e.attr("href")),
                null,
                mediaSite);
    }

    private String buildArticleLink(String mediaSiteLink, String foundLink) {
        if (foundLink.startsWith("http://") || foundLink.startsWith("https://")) {
            return foundLink;
        } else if (foundLink.startsWith("/")) {
            return mediaSiteLink + foundLink.substring(1);
        } else {
            return mediaSiteLink + "www." + foundLink;
        }
    }

    @Override
    public void acceptCookies() {
        WebScraperUtils.acceptCookies("//button[@id='onetrust-accept-btn-handler' and text()='Akceptuję']", driver,
                mediaSite);
    }
}
