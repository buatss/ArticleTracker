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

import static com.buatss.ArticleTracker.util.MediaSiteType.WYBORCZA;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.WebDriver.Navigation;

public class WyborczaParserTest {

    @Mock
    Navigation mockNavigation;
    @InjectMocks
    private WyborczaParser wyborczaParser;
    @Mock
    private WebDriver mockDriver;
    @Mock
    private Document mockDocument;
    @Mock
    private WebElement mockButton;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(wyborczaParser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() {
        String htmlString = "<html><body>" +
                "<div class=\"article\">\n" +
                "    <a href=\"https://www.wyborcza.pl/article1\">Title 1</a>\n" +
                "</div>\n" +
                "<div class=\"article\">\n" +
                "    <a href=\"https://www.wyborcza.pl/article2\">Title 2</a>\n" +
                "</div>\n" +
                "<div class=\"article\">\n" +
                "    <a href=\"https://www.wyborcza.pl/article3\">Title 3</a>\n" +
                "</div>\n" +
                "<div class=\"article\">\n" +
                "    <a href=\"https://www.wyborcza.pl/article4\">Title 4</a>\n" +
                "</div>\n" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(WYBORCZA.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(text(),'AKCEPTUJĘ')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            when(mockDriver.navigate()).thenReturn(mockNavigation);
            doNothing().when(mockNavigation).refresh();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            wyborczaParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title 1", "https://www.wyborcza.pl" +
                        "/article1", null, WYBORCZA.getMediaSite()),
                new Article(null, "Title 2", "https://www.wyborcza.pl" +
                        "/article2", null, WYBORCZA.getMediaSite()),
                new Article(null, "Title 3", "https://www.wyborcza.pl" +
                        "/article3", null, WYBORCZA.getMediaSite()),
                new Article(null, "Title 4", "https://www.wyborcza.pl" +
                        "/article4", null, WYBORCZA.getMediaSite())
        );

        List<Article> actual = wyborczaParser.getArticles();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
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
            doNothing().when(mockDriver).get(WYBORCZA.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[contains(text(),'AKCEPTUJĘ')]"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            when(mockDriver.navigate()).thenReturn(mockNavigation);
            doNothing().when(mockNavigation).refresh();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            wyborczaParser.findArticles();

            mockedUtils.verify(WebScraperUtils::waitRandomMilis);
            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = wyborczaParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
