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

import static com.buatss.ArticleTracker.util.WebScraperUtils.randomlyScrollPage;

@Component
public class SEParser extends AbstractArticleFinder implements CookieAcceptor {
    public SEParser() {
        super(MediaSiteType.SE.getMediaSite());
    }

    @Override
    public void findArticles() {
        randomlyScrollPage(driver);

        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .stream()
                .filter(hasArticle())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasArticle() {
        return element -> element.hasAttr("href") && element.hasText() && (element.attr("href").startsWith("/")
                || element.attr("href").contains("se.pl"));
    }

    private Function<Element, Article> createArticle() {
        return e -> {
            String title = e.text();
            String link = buildArticleLink(mediaSite.getLink(), e.attr("href"));
            return new Article(null, title, link, null, mediaSite);
        };
    }

    private String buildArticleLink(String mediaSiteLink, String foundLink) {
        if (foundLink.startsWith("http://www.") || foundLink.startsWith("https://www.")) {
            return foundLink;
        } else if (foundLink.startsWith("/")) {
            return mediaSiteLink + foundLink.substring(1);
        } else {
            return mediaSiteLink + "www." + foundLink;
        }
    }

    @Override
    public void acceptCookies() {
        WebScraperUtils.acceptCookies("//button[contains(text(),'Akceptuję')]", driver, mediaSite);
    }
}
