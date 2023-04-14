package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.model.MediaSite;
import com.buatss.ArticleTracker.util.MediaSiteType;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.buatss.ArticleTracker.util.WebScraperUtils.waitRandomMilis;

@Component
public class OnetParser extends AbstractArticleFinder {
    private final MediaSite mediaSite = MediaSiteType.ONET.getMediaSite();
    private final WebDriver driver;

    public OnetParser(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void findArticles() {
        driver.get(this.mediaSite.getLink());

        acceptCookies(driver);
        WebScraperUtils.randomlyScrollPage(driver);

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

    private void acceptCookies(WebDriver driver) {
        waitRandomMilis();
        WebElement button = driver.findElement(By.xpath("//button[contains(@class, 'cmp-intro_acceptAll')]"));
        button.click();
        waitRandomMilis();
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
}

