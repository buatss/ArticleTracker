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
public class MoneyParser extends AbstractArticleFinder {
    public MoneyParser() {
        super(MediaSiteType.MONEY.getMediaSite());
    }

    @Override
    public void findArticles() {
        driver.get(this.mediaSite.getLink());

        acceptCookies("//button[contains(text(),'AKCEPTUJĘ I PRZECHODZĘ DO SERWISU')]");
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
                || element.attr("href").contains("money.pl/"));
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
        if (foundLink.startsWith(mediaSiteLink)) {
            return foundLink;
        } else if (foundLink.startsWith("https://money")) {
            return foundLink.replaceFirst("https://", "https://www.");
        } else if (foundLink.startsWith("/")) {
            return mediaSiteLink + foundLink.substring(1);
        } else {
            return mediaSiteLink + foundLink;
        }
    }
}
