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
public class OnetParser extends AbstractArticleFinder implements CookieAcceptor {
    protected OnetParser() {
        super(MediaSiteType.ONET.getMediaSite());
    }

    @Override
    public void findArticles() {
        Document doc = Jsoup.parse(driver.getPageSource());
        doc.select("a")
                .stream()
                .filter(hasArticleLink())
                .map(elementsWithLink())
                .filter(p -> p.getFirst().hasText())
                .map(findTitle())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasArticleLink() {
        return element -> element.hasAttr("href") && element.attr("href").contains("onet.pl");
    }

    private Function<Element, Pair<Elements, String>> elementsWithLink() {
        return a -> Pair.of(a.select("h3"), a.attr("href"));
    }

    private Function<Pair<Elements, String>, Pair<String, String>> findTitle() {
        return p -> Pair.of(p.getFirst().text(), p.getSecond());
    }

    private Function<Pair<String, String>, Article> createArticle() {
        return p -> new Article(null, p.getFirst(), p.getSecond(), null, this.mediaSite);
    }

    @Override
    public void acceptCookies() {
        WebScraperUtils.acceptCookies("//button[contains(@class, 'cmp-button_button cmp-intro_acceptAll')]", driver,
                mediaSite);
    }
}

