package com.example.gateway.web.rest;

import com.example.gateway.repository.MunicipalityRepository;
import com.example.gateway.service.MunicipalityService;
import com.example.gateway.service.dto.MunicipalityDTO;
import com.example.gateway.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.example.gateway.domain.Municipality}.
 */
@RestController
@RequestMapping("/api/municipalities")
public class MunicipalityResource {

    private static final Logger LOG = LoggerFactory.getLogger(MunicipalityResource.class);

    private static final String ENTITY_NAME = "municipality";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MunicipalityService municipalityService;

    private final MunicipalityRepository municipalityRepository;

    public MunicipalityResource(MunicipalityService municipalityService, MunicipalityRepository municipalityRepository) {
        this.municipalityService = municipalityService;
        this.municipalityRepository = municipalityRepository;
    }

    /**
     * {@code POST  /municipalities} : Create a new municipality.
     *
     * @param municipalityDTO the municipalityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new municipalityDTO, or with status {@code 400 (Bad Request)} if the municipality has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MunicipalityDTO>> createMunicipality(@Valid @RequestBody MunicipalityDTO municipalityDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Municipality : {}", municipalityDTO);
        if (municipalityDTO.getId() != null) {
            throw new BadRequestAlertException("A new municipality cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return municipalityService
            .save(municipalityDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/municipalities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /municipalities/:id} : Updates an existing municipality.
     *
     * @param id the id of the municipalityDTO to save.
     * @param municipalityDTO the municipalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated municipalityDTO,
     * or with status {@code 400 (Bad Request)} if the municipalityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the municipalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MunicipalityDTO>> updateMunicipality(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MunicipalityDTO municipalityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Municipality : {}, {}", id, municipalityDTO);
        if (municipalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, municipalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return municipalityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return municipalityService
                    .update(municipalityDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /municipalities/:id} : Partial updates given fields of an existing municipality, field will ignore if it is null
     *
     * @param id the id of the municipalityDTO to save.
     * @param municipalityDTO the municipalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated municipalityDTO,
     * or with status {@code 400 (Bad Request)} if the municipalityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the municipalityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the municipalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MunicipalityDTO>> partialUpdateMunicipality(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MunicipalityDTO municipalityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Municipality partially : {}, {}", id, municipalityDTO);
        if (municipalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, municipalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return municipalityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MunicipalityDTO> result = municipalityService.partialUpdate(municipalityDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /municipalities} : get all the municipalities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of municipalities in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MunicipalityDTO>>> getAllMunicipalities(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Municipalities");
        return municipalityService
            .countAll()
            .zipWith(municipalityService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /municipalities/:id} : get the "id" municipality.
     *
     * @param id the id of the municipalityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the municipalityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MunicipalityDTO>> getMunicipality(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Municipality : {}", id);
        Mono<MunicipalityDTO> municipalityDTO = municipalityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(municipalityDTO);
    }

    /**
     * {@code DELETE  /municipalities/:id} : delete the "id" municipality.
     *
     * @param id the id of the municipalityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMunicipality(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Municipality : {}", id);
        return municipalityService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
