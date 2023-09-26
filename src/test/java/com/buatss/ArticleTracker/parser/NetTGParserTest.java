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

import static com.buatss.ArticleTracker.util.MediaSiteType.NETTG;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class NetTGParserTest {

    @InjectMocks
    private NetTGParser parser;

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
                "<div><a href=\"/article1\"><span>Title 1</span></a></div>" +
                "<div><a href=\"/article2\"><span>Title 2</span></a></div>" +
                "<div><a href=\"/article3\"><span>Title 3</span></a>" +
                "   <div>Randomtext</div>" +
                "</div>" +
                "<div><a href=\"/komentarze\"><span>Title 4</span></a></div>" +
                "<div><a href\"/randomlink\"<span></span></a></div>" +
                "</body></html>";


        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<WebScraperUtils> mockedUtils = mockStatic(WebScraperUtils.class);
             MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)
        ) {
            doNothing().when(mockDriver).get(NETTG.getMediaSite().getLink());
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of(
                new Article(null, "Title 1", "https://www.nettg.pl/article1", null, NETTG.getMediaSite()),
                new Article(null, "Title 2", "https://www.nettg.pl/article2", null, NETTG.getMediaSite()),
                new Article(null, "Title 3", "https://www.nettg.pl/article3", null, NETTG.getMediaSite())
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
            doNothing().when(mockDriver).get(NETTG.getMediaSite().getLink());
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            parser.findArticles();

            mockedUtils.verify(() -> WebScraperUtils.randomlyScrollPage(mockDriver));
        }

        List<Article> expected = List.of();

        List<Article> actual = parser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
