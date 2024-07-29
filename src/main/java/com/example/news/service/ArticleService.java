package com.example.news.service;

import com.example.news.dto.ArticleDTO;
import com.example.news.entity.Article;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    Article createArticle(ArticleDTO articleDTO);
    Article updateArticle(Long id, ArticleDTO articleDTO);
    Optional<Article> deleteArticle(Long id);
    Article getArticleById(Long id);
    List<Article> getAllArticles(Pageable pageable);

    List<ArticleDTO> getLatestArticles();
}