package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class BiznesAlertParser extends AbstractArticleFinder {
    public BiznesAlertParser() {
        super(MediaSiteType.BIZNESALERT.getMediaSite());
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
        return element -> element.hasAttr("href") && element.attr("href").contains("biznesalert.pl");
    }

    private Predicate<Element> hasArticle() {
        return Element::hasText;
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
        if (foundLink.startsWith("http://") || foundLink.startsWith("https://") ||
                foundLink.startsWith("www.biznesalert.pl")) {
            return foundLink;
        } else if (foundLink.startsWith("/")) {
            return mediaSiteLink + foundLink.substring(1);
        }
        return mediaSiteLink;
    }
}
