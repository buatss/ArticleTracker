package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.parser.CookieAcceptor;
import com.buatss.ArticleTracker.util.MediaSiteType;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class WNPParser extends AbstractArticleFinder implements CookieAcceptor {
    public WNPParser() {
        super(MediaSiteType.WNP.getMediaSite());
    }

    @Override
    public void findArticles() {
        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .stream()
                .filter(hasLink())
                .filter(hasArticle())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasLink() {
        return element -> element.hasAttr("href") && element.attr("href").contains("wnp.pl/");
    }

    private Predicate<Element> hasArticle() {
        return element -> element.select("h1").hasText() || element.select("h2").hasText() ||
                element.select("h3").hasText();
    }

    private Function<Element, Article> createArticle() {
        return e -> new Article(
                null,
                e.text(),
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
        WebScraperUtils.acceptCookies("//a[contains(@role, 'button')]//span[text()='I agree and go to the site']",
                driver, mediaSite);
    }
}
