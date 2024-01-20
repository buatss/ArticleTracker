package com.buatss.ArticleTracker.parser.impl;

import com.buatss.ArticleTracker.model.Article;
import com.buatss.ArticleTracker.parser.AbstractArticleFinder;
import com.buatss.ArticleTracker.util.MediaSiteType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class NetTGParser extends AbstractArticleFinder {
    public NetTGParser() {
        super(MediaSiteType.NETTG.getMediaSite());
    }

    @Override
    public void findArticles() {
        Document doc = Jsoup.parse(driver.getPageSource());

        doc.select("div")
                .stream()
                .filter(excludeCommentsSection())
                .map(div -> div.select("a"))
                .flatMap(Collection::stream)
                .filter(hasLink())
                .filter(hasArticle())
                .map(createArticle())
                .peek(System.err::println)
                .forEach(this.getArticles()::add);
    }

    private Predicate<? super Element> excludeCommentsSection() {
        return div -> div.select("a")
                .stream()
                .noneMatch(e -> e.hasAttr("href")
                        && e.attr("href").startsWith("/komentarze"));
    }

    private Predicate<? super Element> hasLink() {
        return element -> element.hasAttr("href") && element.attr("href").startsWith("/");
    }

    private Predicate<Element> hasArticle() {
        return element -> element.select("span").hasText();
    }

    private Function<Element, Article> createArticle() {
        return e -> {
            String title = e.select("span").text();
            String link = buildArticleLink(mediaSite.getLink(), e.attr("href"));
            return new Article(null, title, link, null, mediaSite);
        };
    }

    private String buildArticleLink(String mediaSiteLink, String foundLink) {
        if (foundLink.startsWith("http://") || foundLink.startsWith("https://")) {
            return foundLink;
        } else if (foundLink.startsWith("/")) {
            return mediaSiteLink + foundLink.substring(1);
        } else {
            return mediaSiteLink + foundLink;
        }
    }
}
