package com.example.gateway.service;

import com.example.gateway.service.dto.RequestDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.example.gateway.domain.Request}.
 */
public interface RequestService {
    /**
     * Save a request.
     *
     * @param requestDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RequestDTO> save(RequestDTO requestDTO);

    /**
     * Updates a request.
     *
     * @param requestDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RequestDTO> update(RequestDTO requestDTO);

    /**
     * Partially updates a request.
     *
     * @param requestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RequestDTO> partialUpdate(RequestDTO requestDTO);

    /**
     * Get all the requests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RequestDTO> findAll(Pageable pageable);

    Flux<RequestDTO> findAllByCitizenId(Long citizenId, Pageable pageable);

    /**
     * Returns the number of requests available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" request.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RequestDTO> findOne(Long id);

    /**
     * Delete the "id" request.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
