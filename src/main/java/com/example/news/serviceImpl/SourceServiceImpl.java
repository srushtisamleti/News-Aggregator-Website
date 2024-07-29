package com.example.news.serviceImpl;

import com.example.news.dto.SourceDTO;
import com.example.news.entity.Source;
import com.example.news.exception.SourceNotFoundException;
import com.example.news.repository.SourceRepository;
import com.example.news.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SourceServiceImpl implements SourceService {

    private static final Logger logger = LoggerFactory.getLogger(SourceServiceImpl.class);

    private final SourceRepository sourceRepository;

    @Autowired
    public SourceServiceImpl(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Override
    @Transactional
    public Source createSource(SourceDTO sourceDTO) {
        logger.info("Creating a new Source with name '{}'", sourceDTO.getName());
        try {
            Source source = SourceDTO.convertToSourceEntity(sourceDTO);
            Source savedSource = sourceRepository.save(source);
            logger.info("Source created successfully with id {}", savedSource.getId());
            return savedSource;
        } catch (Exception e) {
            logger.error("Error creating source with name '{}'", sourceDTO.getName(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Source updateSource(Long id, SourceDTO sourceDTO) {
        logger.info("Updating source with id {}", id);
        try {
            Source existingSource = sourceRepository.findById(id)
                    .orElseThrow(() -> new SourceNotFoundException("No Source found with id " + id));
            BeanUtils.copyProperties(sourceDTO, existingSource, "id");
            Source updatedSource = sourceRepository.save(existingSource);
            logger.info("Source updated successfully with id {}", id);
            return updatedSource;
        } catch (SourceNotFoundException e) {
            logger.error("Source not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating source with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Optional<Source> deleteSource(Long id) {
        logger.info("Attempting to delete source with id {}", id);
        try {
            Optional<Source> sourceOptional = sourceRepository.findById(id);
            if (sourceOptional.isPresent()) {
                sourceRepository.deleteById(id);
                logger.info("Source with id {} deleted successfully", id);
                return sourceOptional;
            } else {
                logger.warn("Attempted to delete source with id {} that does not exist", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error deleting source with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Source getSourceById(Long id) {
        logger.info("Fetching source with id {}", id);
        try {
            return sourceRepository.findById(id)
                    .orElseThrow(() -> new SourceNotFoundException("No Source found with id " + id));
        } catch (SourceNotFoundException e) {
            logger.error("Source not found with id {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching source with id {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Source> getAllSources() {
        logger.info("Fetching all sources");
        try {
            return sourceRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all sources", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Source> getSources(Pageable pageable) {
        logger.info("Fetching sources with pagination and sorting");
        try {
            return sourceRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error fetching sources with pagination and sorting", e);
            throw e;
        }
    }
}
