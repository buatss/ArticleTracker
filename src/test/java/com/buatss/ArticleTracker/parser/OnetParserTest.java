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

import static com.buatss.ArticleTracker.util.MediaSiteType.ONET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class OnetParserTest {

    @InjectMocks
    private OnetParser onetParser;

    @Mock
    private WebDriver mockDriver;

    @Mock
    private Document mockDocument;

    @Mock
    private WebElement mockButton;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(onetParser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() {
        String htmlString = "<html><body>" +
                "<a href=\"/article1\">Title 1</a>" +
                "<a href=\"/article2\">Title 2</a>" +
                "<a href=\"/article3\">Title 3</a>" +
                "<a href=\"https://onet.pl/article4\">Title 4</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(ONET.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(@class, 'cmp-intro_acceptAll')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            onetParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis, times(2));
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title 1", "https://www.onet.pl/article1", null, ONET.getMediaSite()),
                new Article(null, "Title 2", "https://www.onet.pl/article2", null, ONET.getMediaSite()),
                new Article(null, "Title 3", "https://www.onet.pl/article3", null, ONET.getMediaSite()),
                new Article(null, "Title 4", "https://www.onet.pl/article4", null, ONET.getMediaSite())
        );

        List<Article> actual = onetParser.getArticles();

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
            doNothing().when(mockDriver).get(ONET.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(@class, 'cmp-intro_acceptAll')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            onetParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis, times(2));
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = onetParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
