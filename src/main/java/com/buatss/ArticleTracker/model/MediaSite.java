package com.buatss.ArticleTracker.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MediaSite {
    @Id
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String link;
    @OneToMany(mappedBy = "mediaSite")
    private List<Article> articles = new ArrayList<>();

    public MediaSite(int id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }
}
