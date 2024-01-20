package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.impl.BankierParser;
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

import static com.buatss.ArticleTracker.util.MediaSiteType.BANKIER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class BankierParserTest {

    @InjectMocks
    private BankierParser parser;

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
                "<a href=\"https://www.bankier.pl/article1\">Title1</a>" +
                "<a href=\"https://www.bankier.pl/article2\">" +
                "<span class=\"m-title-with-label-item__title\">Title2</span>" +
                "</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(BANKIER.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[@id='onetrust-accept-btn-handler' and text()='Akceptuję']"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();
        }

        List<Article> expected = List.of(
                new Article(null, "Title1", "https://www.bankier.pl/article1", null, BANKIER.getMediaSite()),
                new Article(null, "Title2", "https://www.bankier.pl/article2", null, BANKIER.getMediaSite())
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
            doNothing().when(mockDriver).get(BANKIER.getMediaSite().getLink());
            when(mockDriver.findElement(
                    By.xpath("//button[@id='onetrust-accept-btn-handler' and text()='Akceptuję']"))).thenReturn(
                    mockButton);
            doNothing().when(mockButton).click();
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();
        }

        List<Article> expected = List.of();

        List<Article> actual = parser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
