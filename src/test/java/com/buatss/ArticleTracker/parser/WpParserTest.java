package com.buatss.ArticleTracker.parser;

import com.buatss.ArticleTracker.model.Article;
import org.jsoup.Connection;
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

import java.io.IOException;
import java.util.List;

import static com.buatss.ArticleTracker.util.MediaSiteType.WP;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class WpParserTest {

    @InjectMocks
    private WpParser wpParser;

    @Mock
    private WebDriver mockDriver;

    @Mock
    private Document mockDocument;

    @Mock
    private Connection mockConnection;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(wpParser, "driver", mockDriver);
    }

    @Test
    public void findArticles_found() throws IOException {
        String htmlString = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>WpParser Test Page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <a href=\"https://www.wp.pl/article1\" title=\"Title 1\">random text</a>\n" +
                "    <a href=\"https://www.wp.pl/article2\" title=\"Title 2\">random text</a>\n" +
                "    <a href=\"https://www.wp.pl/article3\">\n" +
                "        <div>Nested Title 3</div>\n" +
                "    </a>\n" +
                "    <a href=\"https://www.randomportal.pl/article4\" title=\"random text\">\n" +
                "        <div>Title 4</div>\n" +
                "    </a>\n" +
                "</body>\n" +
                "</html>";

        mockDocument = Jsoup.parse(htmlString);

        List<Article> expected = List.of(
                new Article(null, "Title 1", "https://www.wp.pl/article1", null, WP.getMediaSite()),
                new Article(null, "Title 2", "https://www.wp.pl/article2", null, WP.getMediaSite()),
                new Article(null, "Nested Title 3", "https://www.wp.pl/article3", null, WP.getMediaSite())
        );

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            mockedJsoup.when(() -> Jsoup.connect(String.valueOf(wpParser.mediaSite.getLink()))).thenReturn(
                    mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            wpParser.findArticles();
        }

        List<Article> actual = wpParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findArticles_notFound() throws IOException {
        String htmlString = "<html><body>" +
                "<a href=\"https://randompage.pl/article4\">random title</a>" +
                "</body></html>";

        mockDocument = Jsoup.parse(htmlString);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            mockedJsoup.when(() -> Jsoup.connect(String.valueOf(wpParser.mediaSite.getLink()))).thenReturn(
                    mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            mockedJsoup.when(() -> Jsoup.parse(mockDriver.getPageSource())).thenReturn(mockDocument);

            wpParser.findArticles();
        }

        List<Article> expected = List.of();
        List<Article> actual = wpParser.getArticles();

        assertThat(actual).isEqualTo(expected);
    }
}
