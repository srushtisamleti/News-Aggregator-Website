package com.example.news.dto;

import java.time.LocalDateTime;

import com.example.news.entity.Article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ArticleDTO {

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotBlank(message = "Source is mandatory")
    @Size(max = 255, message = "Source must be less than 255 characters")
    private String source;

    @NotNull(message = "Published date and time is mandatory")
    private LocalDateTime publishedAt;
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public static Article convertToArticleEntity(ArticleDTO articleDTO) {
        if (articleDTO == null) {
            return null;
        }

        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setSource(articleDTO.getSource());
        article.setPublishedAt(articleDTO.getPublishedAt());

        return article;
    }

	public static ArticleDTO convertToDTO(Article article) {
        if (article == null) {
            return null;
        }

        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setSource(article.getSource());
        articleDTO.setPublishedAt(article.getPublishedAt());

        return articleDTO;
    }
}
