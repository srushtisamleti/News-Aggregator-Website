package com.example.news.controller;

import com.example.news.dto.SourceDTO;
import com.example.news.entity.Source;
import com.example.news.exception.SourceNotFoundException;
import com.example.news.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sources")
public class SourceController {

    private static final Logger logger = LoggerFactory.getLogger(SourceController.class);

    private final SourceService sourceService;

    @Autowired
    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @PostMapping("/create")
    public ResponseEntity<Source> createSource(@Validated @RequestBody SourceDTO sourceDTO) {
        logger.info("Request received to create a new source with name '{}'", sourceDTO.getName());
        try {
            Source createdSource = sourceService.createSource(sourceDTO);
            logger.info("Source created successfully with id {}", createdSource.getId());
            return new ResponseEntity<>(createdSource, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating source with name '{}'", sourceDTO.getName(), e);
            throw e;
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Source> updateSource(@PathVariable Long id, @Validated @RequestBody SourceDTO sourceDTO) {
        logger.info("Request received to update source with id {}", id);
        try {
            Source updatedSource = sourceService.updateSource(id, sourceDTO);
            logger.info("Source updated successfully with id {}", updatedSource.getId());
            return new ResponseEntity<>(updatedSource, HttpStatus.OK);
        } catch (SourceNotFoundException e) {
            logger.error("Source not found with id {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating source with id {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Optional<Source>> deleteSource(@PathVariable Long id) {
        logger.info("Request received to delete source with id {}", id);
        try {
            Optional<Source> deletedSource = sourceService.deleteSource(id);
            if (deletedSource.isPresent()) {
                logger.info("Source with id {} deleted successfully", id);
                return new ResponseEntity<>(deletedSource, HttpStatus.OK);
            } else {
                logger.warn("Attempted to delete source with id {} that does not exist", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting source with id {}", id, e);
            throw e;
        }
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<Source> getSourceById(@PathVariable Long id) {
        logger.info("Request received to fetch source with id {}", id);
        try {
            Source source = sourceService.getSourceById(id);
            logger.info("Source with id {} fetched successfully", id);
            return new ResponseEntity<>(source, HttpStatus.OK);
        } catch (SourceNotFoundException e) {
            logger.error("Source not found with id {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error fetching source with id {}", id, e);
            throw e;
        }
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<Source>> getAllSources() {
        logger.info("Request received to fetch all sources");
        try {
            List<Source> sources = sourceService.getAllSources();
            logger.info("Fetched {} sources", sources.size());
            return new ResponseEntity<>(sources, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all sources", e);
            throw e;
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Source>> getSources(Pageable pageable) {
        logger.info("Request received to fetch sources with pagination and sorting");
        try {
            Page<Source> sources = sourceService.getSources(pageable);
            logger.info("Fetched {} sources (page {} of {})",
                    sources.getNumberOfElements(),
                    sources.getNumber() + 1,
                    sources.getTotalPages());
            return new ResponseEntity<>(sources, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching sources with pagination and sorting", e);
            throw e;
        }
    }

}
