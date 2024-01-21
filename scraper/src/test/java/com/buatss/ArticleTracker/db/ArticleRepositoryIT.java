package com.buatss.ArticleTracker.db;

import com.buatss.ArticleTracker.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.annotation.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ArticleRepositoryIT {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void findByLink_found() {
        String link = "http://example.com/article";
        Article articleToSave = new Article();
        articleToSave.setTitle("article");
        articleToSave.setLink(link);
        articleRepository.save(articleToSave);

        Article article = articleRepository.findByLink(link);

        assertNotNull(article);
        assertEquals(link, article.getLink());
    }

    @Test
    void findByLink_notFound() {
        String link = "http://example.com/article";

        Article article = articleRepository.findByLink(link);

        assertNull(article);
    }
}
