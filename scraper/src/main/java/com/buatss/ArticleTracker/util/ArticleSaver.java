package com.buatss.ArticleTracker.util;

import com.buatss.ArticleTracker.model.Article;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ArticleSaver {

    private static final String FILE_PATH = "articles.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveArticleToFile(Article article) {
        try {
            Article[] existingArticles = readExistingArticles();
            if (articleAlreadyExists(existingArticles, article)) {
                System.out.println("Article with the same link or id already exists. Skipping saving to file.");
                return;
            }

            objectMapper.writeValue(new File(FILE_PATH), appendArticleToFile(existingArticles, article));
            System.out.println("Article saved successfully to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving article to file: " + e.getMessage());
        }
    }

    private static Article[] readExistingArticles() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, Article[].class);
            } catch (IOException e) {
                System.err.println("Error reading existing articles from file: " + e.getMessage());
                return new Article[]{};
            }
        } else {
            return new Article[]{};
        }
    }

    private static boolean articleAlreadyExists(Article[] existingArticles, Article newArticle) {
        return Arrays.stream(existingArticles)
                .anyMatch(article -> Objects.equals(article.getId(), newArticle.getId()) ||
                        article.getLink().equals(newArticle.getLink()));
    }

    private static Article[] appendArticleToFile(Article[] existingArticles, Article newArticle) {
        Article[] updatedArticles = Arrays.copyOf(existingArticles, existingArticles.length + 1);
        updatedArticles[existingArticles.length] = newArticle;
        return updatedArticles;
    }
}
