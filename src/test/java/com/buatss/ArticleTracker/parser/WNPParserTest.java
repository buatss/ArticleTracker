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

import static com.buatss.ArticleTracker.util.MediaSiteType.WNP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class WNPParserTest {

    @InjectMocks
    private WNPParser parser;

    @Mock
    private WebDriver mockDriver;

    @Mock
    private Document mockDocument;

    @Mock
    private WebElement mockButton;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(parser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() {
        String htmlString = "<html><body>" +
                "<a href=\"https://www.wnp.pl/article1\">" +
                "<h1>Title1<h3> 1" +
                "</a>" +
                "<a href=\"https://www.wnp.pl/article2\">" +
                "<h2>Title2<h3> 1" +
                "</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(WNP.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//a[contains(@role, 'button')]//span[text()='I agree and go to the site']"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title1", "https://www.wnp.pl/article1", null, WNP.getMediaSite()),
                new Article(null, "Title2", "https://www.wnp.pl/article2", null, WNP.getMediaSite())
        );

        List<Article> actual = parser.getArticles();

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
            doNothing().when(mockDriver).get(WNP.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//a[contains(@role, 'button')]//span[text()='I agree and go to the site']"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = parser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
