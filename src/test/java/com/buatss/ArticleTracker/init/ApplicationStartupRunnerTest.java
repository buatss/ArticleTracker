package com.buatss.ArticleTracker.init;

import com.buatss.ArticleTracker.db.MediaSiteRepository;
import com.buatss.ArticleTracker.service.ArticleService;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationStartupRunnerTest {

    @InjectMocks
    private ApplicationStartupRunner applicationStartupRunner;

    @Mock
    private MediaSiteRepository mediaSiteRepository;

    @Mock
    private ArticleService articleService;

    @Test
    public void scrapOnStartupTrue_scrapAll() throws Exception {
        ReflectionTestUtils.setField(applicationStartupRunner, "scrapOnStartup", true);
        ReflectionTestUtils.setField(applicationStartupRunner, "exitAfterScrapOnStartup", false);

        List<MediaSiteType> allMedias = new ArrayList<>();
        allMedias.add(MediaSiteType.WP);
        allMedias.add(MediaSiteType.ONET);

        when(mediaSiteRepository.existsById(MediaSiteType.WP.getMediaSite().getId())).thenReturn(true);
        when(mediaSiteRepository.existsById(MediaSiteType.ONET.getMediaSite().getId())).thenReturn(false);
        doNothing().when(articleService).scrapAll();

        applicationStartupRunner.run(null);

        verify(mediaSiteRepository, times(1)).saveAndFlush(MediaSiteType.ONET.getMediaSite());
        verify(mediaSiteRepository, never()).saveAndFlush(MediaSiteType.WP.getMediaSite());
        verify(articleService, times(1)).scrapAll();
    }

    @Test
    public void scrapOnStartupFalse_notScrap() throws Exception {
        ReflectionTestUtils.setField(applicationStartupRunner, "scrapOnStartup", false);
        ReflectionTestUtils.setField(applicationStartupRunner, "exitAfterScrapOnStartup", false);
        List<MediaSiteType> allMedias = new ArrayList<>();
        allMedias.add(MediaSiteType.WP);
        allMedias.add(MediaSiteType.ONET);

        when(mediaSiteRepository.existsById(MediaSiteType.WP.getMediaSite().getId())).thenReturn(true);
        when(mediaSiteRepository.existsById(MediaSiteType.ONET.getMediaSite().getId())).thenReturn(false);

        applicationStartupRunner.run(null);

        verify(mediaSiteRepository, times(1)).saveAndFlush(MediaSiteType.ONET.getMediaSite());
        verify(mediaSiteRepository, never()).saveAndFlush(MediaSiteType.WP.getMediaSite());
        verify(articleService, never()).scrapAll();
    }
}
