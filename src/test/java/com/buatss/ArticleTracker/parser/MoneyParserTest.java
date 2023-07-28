package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.buatss.ArticleTracker.util.MediaSiteType.MONEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class MoneyParserTest {

    @InjectMocks
    private MoneyParser moneyParser;

    @Mock
    private WebDriver mockDriver;

    @Mock
    private Document mockDocument;

    @Mock
    private WebElement mockButton;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(moneyParser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() {
        String htmlString = "<html><body>" +
                "<a href=\"/article1\">Title 1</a>" +
                "<a href=\"/article2\">Title 2</a>" +
                "<a href=\"/article3\">Title 3</a>" +
                "<a href=\"https://money.pl/article4\">Title 4</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(MONEY.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(text(),'AKCEPTUJĘ I PRZECHODZĘ DO SERWISU')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            moneyParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title 1", "https://www.money.pl/article1", null, MONEY.getMediaSite()),
                new Article(null, "Title 2", "https://www.money.pl/article2", null, MONEY.getMediaSite()),
                new Article(null, "Title 3", "https://www.money.pl/article3", null, MONEY.getMediaSite()),
                new Article(null, "Title 4", "https://www.money.pl/article4", null, MONEY.getMediaSite())
        );

        List<Article> actual = moneyParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findArticles_notFound() {
        String htmlString = "<html><body>" +
                "<a href=\"https://randompage.pl/article4\">random title</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(MONEY.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(text(),'AKCEPTUJĘ I PRZECHODZĘ DO SERWISU')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            moneyParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = moneyParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
