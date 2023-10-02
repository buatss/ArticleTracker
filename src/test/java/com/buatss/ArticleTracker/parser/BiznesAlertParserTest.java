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
import org.openqa.selenium.WebDriver;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.buatss.ArticleTracker.util.MediaSiteType.BIZNESALERT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class BiznesAlertParserTest {

    @InjectMocks
    private BiznesAlertParser parser;

    @Mock
    private WebDriver mockDriver;

    @Mock
    private Document mockDocument;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(parser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() {
        String htmlString = "<html><body>" +
                "<div><a href=\"http://biznesalert.pl/article1\">Title 1</a></div>" +
                "<div><a href=\"https://biznesalert.pl/article2\">Title 2</a></div>" +
                "<div><a href=\"www.biznesalert.pl/article3\">Title 3</a></div>" +
                "<div><a href=\"randompage.pl/article3\">Title 4</a></div>" +
                "</body></html>";


        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(BIZNESALERT.getMediaSite().getLink());
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title 1", "http://biznesalert.pl/article1", null, BIZNESALERT.getMediaSite()),
                new Article(null, "Title 2", "https://biznesalert.pl/article2", null, BIZNESALERT.getMediaSite()),
                new Article(null, "Title 3", "www.biznesalert.pl/article3", null, BIZNESALERT.getMediaSite())
        );

        List<Article> actual = parser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findArticles_notFound() {
        String htmlString = "<html><body>" +
                "<a href=\"https://randompage.pl/article4\"><span>random title</span></a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(BIZNESALERT.getMediaSite().getLink());
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = parser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
