package com.buatss.ArticleTracker.db;

import com.buatss.ArticleTracker.model.MediaSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaSiteRepository extends JpaRepository<MediaSite, Integer> {
}
