package com.example.gateway.service;

import com.example.gateway.service.dto.MunicipalityDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.example.gateway.domain.Municipality}.
 */
public interface MunicipalityService {
    /**
     * Save a municipality.
     *
     * @param municipalityDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MunicipalityDTO> save(MunicipalityDTO municipalityDTO);

    /**
     * Updates a municipality.
     *
     * @param municipalityDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MunicipalityDTO> update(MunicipalityDTO municipalityDTO);

    /**
     * Partially updates a municipality.
     *
     * @param municipalityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MunicipalityDTO> partialUpdate(MunicipalityDTO municipalityDTO);

    /**
     * Get all the municipalities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MunicipalityDTO> findAll(Pageable pageable);

    /**
     * Returns the number of municipalities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" municipality.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MunicipalityDTO> findOne(Long id);

    /**
     * Delete the "id" municipality.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
