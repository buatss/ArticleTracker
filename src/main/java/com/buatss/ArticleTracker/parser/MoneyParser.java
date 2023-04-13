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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.buatss.ArticleTracker.util.WebScraperUtils.randomlyScrollPage;

@Component
public class MoneyParser extends AbstractArticleFinder {
    private final MediaSite mediaSite = MediaSiteType.MONEY.getMediaSite();

    @Override
    public void findArticles() {
        System.setProperty("webdriver.gecko.driver", System.getenv("geckodriver"));

        WebDriver driver = new FirefoxDriver();

        driver.get(this.mediaSite.getLink());

        acceptCookies(driver);
        randomlyScrollPage(driver);

        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("a")
                .stream()
                .filter(hasArticle())
                .map(createArticle())
                .forEach(this.getArticles()::add);

        driver.quit();
    }

    private void acceptCookies(WebDriver driver) {
        WebScraperUtils.waitRandomMilis();
        WebElement button =
                driver.findElement(By.xpath("//button[contains(text(),'AKCEPTUJĘ I PRZECHODZĘ DO SERWISU')]"));
        button.click();
    }

    private Predicate<Element> hasArticle() {
        return element -> element.hasAttr("href") && element.hasText() && (element.attr("href").startsWith("/")
                || element.attr("href").contains("money.pl/"));
    }

    private Function<Element, Pair<Elements, String>> elementsWithLink() {
        return a -> Pair.of(a.select("h3"), a.attr("href"));
    }

    private Function<Pair<Elements, String>, Pair<String, String>> findTitle() {
        return p -> Pair.of(p.getFirst().text(), p.getSecond());
    }

    private Function<Element, Article> createArticle() {
        return e -> new Article(
                null,
                e.text(),
                mediaSite.getLink().concat(e.attr("href")),
                null,
                mediaSite);
    }
}

