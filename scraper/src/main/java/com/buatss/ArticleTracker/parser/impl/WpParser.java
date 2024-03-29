package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class WpParser extends AbstractArticleFinder {
    protected WpParser() {
        super(MediaSiteType.WP.getMediaSite());
    }

    @Override
    public void findArticles() {
        Document doc = Jsoup.parse(driver.getPageSource());

        findArticlesInHyperlink(doc);
        findArticlesNestedInHyperlink(doc);
    }

    private void findArticlesInHyperlink(Document doc) {
        Elements elements = doc.select("a");
        elements.stream()
                .filter(hasLinkToArticleWithTitle())
                .map(this::createArticleFromHyperlink)
                .forEach(x -> this.getArticles().add(x));
    }

    private void findArticlesNestedInHyperlink(Document doc) {
        Elements elements = doc.select("a");
        elements.stream()
                .filter(hasLinkToArticle())
                .filter(hasTextInDiv())
                .map(this::createArticleFromElementNested)
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasLinkToArticleWithTitle() {
        return element -> element.hasAttr("href") && element.hasAttr("title") && !element.attr("title").isEmpty() &&
                element.attr("href").contains("wp.pl");
    }

    private Predicate<Element> hasLinkToArticle() {
        return element -> element.hasAttr("href") && element.attr("href").contains("wp.pl");
    }

    private Predicate<Element> hasTextInDiv() {
        return element -> element.select("div").stream().anyMatch(Element::hasText);
    }

    private Article createArticleFromHyperlink(Element element) {
        return new Article(null, element.attr("title"), element.attr("href"), null, this.mediaSite);
    }

    private Article createArticleFromElementNested(Element element) {
        String title = element.select("div").stream().filter(Element::hasText).map(Element::text).findFirst().get();
        String url = element.attr("href");
        return new Article(null, title, url, null, this.mediaSite);
    }
}

