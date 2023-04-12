package com.buatss.ArticleTracker.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, unique = true)
    private String link;
    private LocalDateTime uploadDate;
    @ManyToOne
    @JoinColumn(name = "media_site_id", referencedColumnName = "id")
    private MediaSite mediaSite;

    @PrePersist
    public void prePersist() {
        uploadDate = LocalDateTime.now();
    }
}