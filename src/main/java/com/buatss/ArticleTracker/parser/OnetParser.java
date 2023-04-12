package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.model.MediaSite;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class OnetParser extends AbstractArticleFinder {
    private final MediaSite mediaSite = MediaSiteType.ONET.getMediaSite();
    Random random = new Random();
    private final Long MIN_WAIT_TIME = 1000L;
    private final Long MAX_WAIT_TIME = 3000L;

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
                .filter(hasArticleLink())
                .map(elementsWithLink())
                .filter(p -> p.getFirst().hasText())
                .map(findTitle())
                .map(createArticle())
                .forEach(this.getArticles()::add);

        driver.quit();
    }

    private void acceptCookies(WebDriver driver) {
        waitRandomMilis();
        WebElement button = driver.findElement(By.xpath("//button[contains(@class, 'cmp-intro_acceptAll')]"));
        button.click();
        waitRandomMilis();
    }

    private void randomlyScrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < random.nextInt(5, 15); i++) {
            js.executeScript("window.scrollBy(0, 1000)");
            waitRandomMilis();
        }
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

    private void waitRandomMilis() {
        try {
            Thread.sleep(random.nextLong(MIN_WAIT_TIME, MAX_WAIT_TIME));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

