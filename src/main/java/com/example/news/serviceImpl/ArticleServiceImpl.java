package com.example.news.serviceImpl;

import com.example.news.dto.ArticleDTO;
import com.example.news.entity.Article;
import com.example.news.exception.ArticleNotFoundException;
import com.example.news.repository.ArticleRepository;
import com.example.news.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    @Transactional
    public Article createArticle(ArticleDTO articleDTO) {
        logger.info("Creating a new Article with title '{}'", articleDTO.getTitle());
        try {
            Article article = ArticleDTO.convertToArticleEntity(articleDTO);
            Article savedArticle = articleRepository.save(article);
            logger.info("Article created successfully with id {}", savedArticle.getId());
            return savedArticle;
        } catch (Exception e) {
            logger.error("Error creating article with title '{}'", articleDTO.getTitle(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Article updateArticle(Long id, ArticleDTO articleDTO) {
        logger.info("Updating article with id {}", id);
        try {
            Article existingArticle = articleRepository.findById(id)
                    .orElseThrow(() -> new ArticleNotFoundException("No Article found with id " + id));
            BeanUtils.copyProperties(articleDTO, existingArticle, "id");
            Article updatedArticle = articleRepository.save(existingArticle);
            logger.info("Article updated successfully with id {}", id);
            return updatedArticle;
        } catch (ArticleNotFoundException e) {
            logger.error("Article not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating article with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<Article> deleteArticle(Long id) {
        logger.info("Attempting to delete article with id {}", id);
        try {
            Optional<Article> articleOptional = articleRepository.findById(id);
            if (articleOptional.isPresent()) {
                articleRepository.deleteById(id);
                logger.info("Article with id {} deleted successfully", id);
                return articleOptional;
            } else {
                logger.warn("Attempted to delete article with id {} that does not exist", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error deleting article with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Article getArticleById(Long id) {
        logger.info("Fetching article with id {}", id);
        try {
            return articleRepository.findById(id)
                    .orElseThrow(() -> new ArticleNotFoundException("No Article found with id " + id));
        } catch (ArticleNotFoundException e) {
            logger.error("Article not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching article with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Article> getAllArticles(Pageable pageable) {
        logger.info("Fetching all articles");
        try {
            Page<Article> articlePage = articleRepository.findAll(pageable);
            logger.info("Fetched {} articles (page {} of {})",
                    articlePage.getNumberOfElements(),
                    articlePage.getNumber() + 1,
                    articlePage.getTotalPages()
            );
            return articlePage.getContent();
        } catch (Exception e) {
            logger.error("Error fetching articles with pagination and sorting", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDTO> getLatestArticles() {
        logger.info("Fetching the latest articles");
        try {
            List<Article> latestArticles = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"));
            List<ArticleDTO> latestArticleDTOs = latestArticles.stream()
                    .map(ArticleDTO::convertToDTO)
                    .collect(Collectors.toList());
            logger.info("Fetched {} latest articles", latestArticleDTOs.size());
            return latestArticleDTOs;
        } catch (Exception e) {
            logger.error("Error fetching the latest articles", e);
            throw e;
        }
    }
}
