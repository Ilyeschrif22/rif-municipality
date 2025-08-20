package com.example.municipality.service;

import com.example.municipality.service.dto.MunicipalityDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.example.municipality.domain.Municipality}.
 */
public interface MunicipalityService {
    /**
     * Save a municipality.
     *
     * @param municipalityDTO the entity to save.
     * @return the persisted entity.
     */
    MunicipalityDTO save(MunicipalityDTO municipalityDTO);

    /**
     * Updates a municipality.
     *
     * @param municipalityDTO the entity to update.
     * @return the persisted entity.
     */
    MunicipalityDTO update(MunicipalityDTO municipalityDTO);

    /**
     * Partially updates a municipality.
     *
     * @param municipalityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MunicipalityDTO> partialUpdate(MunicipalityDTO municipalityDTO);

    /**
     * Get all the municipalities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MunicipalityDTO> findAll(Pageable pageable);

    /**
     * Get the "id" municipality.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MunicipalityDTO> findOne(Long id);

    /**
     * Delete the "id" municipality.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
