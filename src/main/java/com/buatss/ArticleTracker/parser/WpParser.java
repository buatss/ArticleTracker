package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class WpParser extends AbstractArticleFinder {
    private final URL url;

    public WpParser() throws IOException {
        this.url = new URL("http://www.wp.pl");
    }

    @Override
    public void findArticles() {
        Document doc = null;
        try {
            doc = Jsoup.connect(String.valueOf(this.url)).get();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to " + this.url);
        }

        findArticlesInHyperlink(doc);
        findArticlesNestedInHyperlink(doc);
    }

    private void findArticlesInHyperlink(Document doc) {
        Elements elements = doc.select("a");
        elements.stream()
                .filter(element -> element.hasAttr("href"))
                .filter(element -> element.hasAttr("title"))
                .map(element -> new Article(null, element.attr("title"), element.attr("href"), null))
                .forEach(x -> {
                    this.getArticles().add(x);
                });
    }

    private void findArticlesNestedInHyperlink(Document doc) {
        Elements elements = doc.select("a");
        elements.stream()
                .filter(element -> element.hasAttr("href") && element.attr("href").contains("wp.pl"))
                .filter(element -> element.select("div").stream().anyMatch(Element::hasText))
                .map(element -> new Article(null,
                        element.select("div").stream().filter(Element::hasText).map(Element::text).findFirst().get(),
                        element.attr("href"),
                        null))
                .forEach(x -> {
                    this.getArticles().add(x);
                });
    }
}

