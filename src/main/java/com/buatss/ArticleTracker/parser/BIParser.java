package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.buatss.ArticleTracker.util.WebScraperUtils.randomlyScrollPage;

@Component
public class BIParser extends AbstractArticleFinder {
    public BIParser() {
        super(MediaSiteType.BI.getMediaSite());
    }

    @Override
    public void findArticles() {
        driver.get(this.mediaSite.getLink());

        acceptCookies("//button[@aria-label='accept and close']");
        randomlyScrollPage(driver);

        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .stream()
                .filter(hasArticle())
                .filter(hasTitle())
                .map(createArticle())
                .forEach(this.getArticles()::add);
    }

    private Predicate<Element> hasArticle() {
        return element -> element.hasAttr("href") && element.attr("href").contains("businessinsider.com.pl");
    }

    private Predicate<Element> hasTitle() {
        return element -> element.select("h3").hasText();
    }

    private Function<Element, Article> createArticle() {
        return e -> {
            String title = e.select("h3").text();
            String link = buildArticleLink(mediaSite.getLink(), e.attr("href"));
            return new Article(null, title, link, null, mediaSite);
        };
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
}
