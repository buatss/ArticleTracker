package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.parser.CookieAcceptor;
import com.buatss.ArticleTracker.util.MediaSiteType;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class WyborczaParser extends AbstractArticleFinder implements CookieAcceptor {
    protected WyborczaParser() {
        super(MediaSiteType.WYBORCZA.getMediaSite());
    }

    @Override
    public void findArticles() {
        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .parallelStream()
                .filter(hasArticleLinkWithText())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasArticleLinkWithText() {
        return e -> e.hasAttr("href") && e.attr("href").contains("wyborcza.pl") && e.hasText();
    }

    private Function<Element, Pair<Elements, String>> elementsWithLink() {
        return a -> Pair.of(a.select("h3"), a.attr("href"));
    }

    private Function<Pair<Elements, String>, Pair<String, String>> findTitle() {
        return p -> Pair.of(p.getFirst().text(), p.getSecond());
    }

    private Function<Element, Article> createArticle() {
        return e ->
                new Article(
                        null,
                        e.text(),
                        e.attr("href"),
                        null,
                        this.mediaSite
                );
    }

    @Override
    public void acceptCookies() {
        WebScraperUtils.acceptCookies("//button[contains(text(),'AKCEPTUJĘ')]", driver, mediaSite);
    }
}

