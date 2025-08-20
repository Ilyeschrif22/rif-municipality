package com.example.gateway.service.impl;

import com.example.gateway.repository.DocumentRepository;
import com.example.gateway.service.DocumentService;
import com.example.gateway.service.dto.DocumentDTO;
import com.example.gateway.service.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.example.gateway.domain.Document}.
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentRepository documentRepository;

    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public Mono<DocumentDTO> save(DocumentDTO documentDTO) {
        LOG.debug("Request to save Document : {}", documentDTO);
        return documentRepository.save(documentMapper.toEntity(documentDTO)).map(documentMapper::toDto);
    }

    @Override
    public Mono<DocumentDTO> update(DocumentDTO documentDTO) {
        LOG.debug("Request to update Document : {}", documentDTO);
        return documentRepository.save(documentMapper.toEntity(documentDTO)).map(documentMapper::toDto);
    }

    @Override
    public Mono<DocumentDTO> partialUpdate(DocumentDTO documentDTO) {
        LOG.debug("Request to partially update Document : {}", documentDTO);

        return documentRepository
            .findById(documentDTO.getId())
            .map(existingDocument -> {
                documentMapper.partialUpdate(existingDocument, documentDTO);

                return existingDocument;
            })
            .flatMap(documentRepository::save)
            .map(documentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DocumentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Documents");
        return documentRepository.findAllBy(pageable).map(documentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return documentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DocumentDTO> findOne(Long id) {
        LOG.debug("Request to get Document : {}", id);
        return documentRepository.findById(id).map(documentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Document : {}", id);
        return documentRepository.deleteById(id);
    }
}
