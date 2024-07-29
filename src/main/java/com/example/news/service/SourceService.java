package com.example.news.service;

import com.example.news.dto.SourceDTO;
import com.example.news.entity.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SourceService {

    Source createSource(SourceDTO sourceDTO);
    Source updateSource(Long id, SourceDTO sourceDTO);
    Optional<Source> deleteSource(Long id);
    Source getSourceById(Long id);
    List<Source> getAllSources();
    Page<Source> getSources(Pageable pageable);

}
