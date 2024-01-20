package com.buatss.ArticleTracker.service;

import com.buatss.ArticleTracker.db.ArticleRepository;
import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.util.MediaSiteType;
import com.buatss.ArticleTracker.util.WebScraperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private WebDriver mockDriver;
    @Mock
    private AbstractArticleFinder mockParser;
    @Mock
    WebDriver.TargetLocator locator;
    @InjectMocks
    ArticleService service;
    Article article = new Article(null, "title", "link", null, null);
    Article article2 = new Article(null, "title2", "link2", null, null);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "driver", mockDriver);
        ReflectionTestUtils.setField(service, "repository", articleRepository);
    }

    @Test
    public void testScrapAllSequentialNotFound() {
        ReflectionTestUtils.setField(service, "parsers", List.of(mockParser));

        doNothing().when(mockParser).findArticles();
        when(mockParser.getArticles()).thenReturn(List.of());

        service.scrapAllSequential();

        verify(articleRepository, times(0)).findByLink(anyString());
        verify(articleRepository, times(0)).saveAndFlush(any());
        verify(mockDriver, times(1)).quit();
    }

    @Test
    public void testScrapAllSequentialFound() throws InterruptedException {
        ReflectionTestUtils.setField(service, "parsers", List.of(mockParser));

        doNothing().when(mockParser).findArticles();
        when(mockParser.getArticles()).thenReturn(List.of(article, article2));
        when(articleRepository.saveAndFlush(article)).thenReturn(article);
        when(articleRepository.saveAndFlush(article2)).thenReturn(article2);

        service.scrapAllSequential();

        verify(articleRepository, times(2)).findByLink(anyString());
        verify(articleRepository, times(2)).saveAndFlush(any());
        verify(mockDriver, times(1)).quit();
    }

    private static Stream<Arguments> provideManyParsersAndResults() {
        return Stream.of(
                Arguments.of("1 parsers, 3 max max", 1, 3, 8, 1, 1),
                Arguments.of("4 parsers, 3 max", 4, 3, 16, 2, 4),
                Arguments.of("8 parsers, 3 max", 8, 3, 24, 3, 8),
                Arguments.of("24 parsers, 5 max", 24, 5, 40, 5, 24),
                Arguments.of("12 parsers, 1 max", 12, 1, 96, 12, 12)
        );
    }

    @ParameterizedTest
    @MethodSource("provideManyParsersAndResults")
    void testScrapAllParallelManyParsersFound(String caseName, int parsers, int maxTabs, int windowHandles,
                                              int windowHandle, int repositoryInvocations) {
        List<AbstractArticleFinder> parserList = new ArrayList<>();
        for (int i = 0; i < parsers; i++) {
            parserList.add(mockParser);
        }
        ReflectionTestUtils.setField(service, "parsers", parserList);

        when(mockDriver.getWindowHandles()).thenReturn(new HashSet<>());
        when(mockDriver.getWindowHandle()).thenReturn("mainHandle");

        Set<String> handles = Set.of("handle");
        when(mockDriver.getWindowHandles()).thenReturn(handles);
        when(mockDriver.switchTo()).thenReturn(locator);
        when(mockDriver.getCurrentUrl()).thenReturn("https://www.se.pl/");
        when(locator.window(handles.toArray()[0].toString())).thenReturn(mockDriver);
        doNothing().when(mockParser).findArticles();
        when(mockParser.getArticles()).thenReturn(List.of(article));
        when(mockParser.getMediaSite()).thenReturn(MediaSiteType.SE.getMediaSite());

        try (MockedStatic<WebScraperUtils> ignored = mockStatic(WebScraperUtils.class)
        ) {
            service.scrapAllParallel(maxTabs);
        }

        verify(mockDriver, times(windowHandles)).getWindowHandles();
        verify(mockDriver, times(windowHandle)).getWindowHandle();

        verify(articleRepository, times(repositoryInvocations)).findByLink(anyString());
        verify(articleRepository, times(repositoryInvocations)).saveAndFlush(any());
        verify(mockDriver, times(1)).quit();
    }

    @Test
    public void testScrapAllParallelNotFound() {
        ReflectionTestUtils.setField(service, "parsers", List.of(mockParser));

        when(mockDriver.getWindowHandles()).thenReturn(new HashSet<>());
        when(mockDriver.getWindowHandle()).thenReturn("mainHandle");

        Set<String> handles = Set.of("handle");
        when(mockDriver.getWindowHandles()).thenReturn(handles);
        when(mockDriver.switchTo()).thenReturn(locator);
        when(mockDriver.getCurrentUrl()).thenReturn("https://www.se.pl/");
        when(locator.window(handles.toArray()[0].toString())).thenReturn(mockDriver);
        doNothing().when(mockParser).findArticles();
        when(mockParser.getArticles()).thenReturn(List.of());
        when(mockParser.getMediaSite()).thenReturn(MediaSiteType.SE.getMediaSite());

        try (MockedStatic<WebScraperUtils> ignored = mockStatic(WebScraperUtils.class)
        ) {
            service.scrapAllParallel(3);
        }

        verify(mockDriver, times(8)).getWindowHandles();
        verify(mockDriver, times(1)).getWindowHandle();

        verify(articleRepository, times(0)).findByLink(anyString());
        verify(articleRepository, times(0)).saveAndFlush(any());
        verify(mockDriver, times(1)).quit();
    }
}
