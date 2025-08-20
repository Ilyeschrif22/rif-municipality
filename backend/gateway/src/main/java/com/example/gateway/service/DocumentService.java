package com.example.gateway.service;

import com.example.gateway.service.dto.DocumentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.example.gateway.domain.Document}.
 */
public interface DocumentService {
    /**
     * Save a document.
     *
     * @param documentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DocumentDTO> save(DocumentDTO documentDTO);

    /**
     * Updates a document.
     *
     * @param documentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DocumentDTO> update(DocumentDTO documentDTO);

    /**
     * Partially updates a document.
     *
     * @param documentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DocumentDTO> partialUpdate(DocumentDTO documentDTO);

    /**
     * Get all the documents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DocumentDTO> findAll(Pageable pageable);

    /**
     * Returns the number of documents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" document.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DocumentDTO> findOne(Long id);

    /**
     * Delete the "id" document.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
