package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.util.MediaSiteType;
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

import static com.buatss.ArticleTracker.util.WebScraperUtils.*;

@Component
public class WyborczaParser extends AbstractArticleFinder {
    protected WyborczaParser() {
        super(MediaSiteType.WYBORCZA.getMediaSite());
    }

    @Override
    public void findArticles() {
        driver.get(this.mediaSite.getLink());

        acceptCookies(driver);
        delayedRefresh(driver);
        randomlyScrollPage(driver);

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

    private void acceptCookies(WebDriver driver) {
        waitRandomMilis();
        WebElement button =
                driver.findElement(By.xpath("//button[contains(text(),'AKCEPTUJĘ')]"));
        button.click();
    }

    private void delayedRefresh(WebDriver driver) {
        waitMs(5000L);
        driver.navigate().refresh();
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
}

