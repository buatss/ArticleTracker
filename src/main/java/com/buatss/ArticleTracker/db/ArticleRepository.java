package com.buatss.ArticleTracker.db;

import com.buatss.ArticleTracker.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article findByLink(String link);
}