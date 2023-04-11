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
    @PrePersist
    public void prePersist(){
        uploadDate = LocalDateTime.now();
    }
}
