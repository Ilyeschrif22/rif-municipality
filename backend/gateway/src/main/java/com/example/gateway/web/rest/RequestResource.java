package com.example.gateway.web.rest;

import com.example.gateway.repository.RequestRepository;
import com.example.gateway.service.RequestService;
import com.example.gateway.service.dto.RequestDTO;
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
 * REST controller for managing {@link com.example.gateway.domain.Request}.
 */
@RestController
@RequestMapping("/api/requests")
public class RequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(RequestResource.class);

    private static final String ENTITY_NAME = "request";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RequestService requestService;

    private final RequestRepository requestRepository;

    private final com.example.gateway.repository.AppUserRepository appUserRepository;

    public RequestResource(RequestService requestService, RequestRepository requestRepository, com.example.gateway.repository.AppUserRepository appUserRepository) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@code POST  /requests} : Create a new request.
     *
     * @param requestDTO the requestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new requestDTO, or with status {@code 400 (Bad Request)} if the request has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RequestDTO>> createRequest(
        @Valid @RequestBody RequestDTO requestDTO,
        org.springframework.security.core.Authentication authentication
    ) throws URISyntaxException {
        LOG.debug("REST request to save Request : {}", requestDTO);
        if (requestDTO.getId() != null) {
            throw new BadRequestAlertException("A new request cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String cin = authentication.getName();
        return appUserRepository
            .findByCin(cin)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
            .flatMap(appUser -> {
                requestDTO.setCitizenId(appUser.getId());
                return requestService
                    .save(requestDTO)
                    .map(result -> {
                        try {
                            return ResponseEntity.created(new URI("/api/requests/" + result.getId()))
                                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                                .body(result);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    });
            });
    }

    /**
     * {@code PUT  /requests/:id} : Updates an existing request.
     *
     * @param id the id of the requestDTO to save.
     * @param requestDTO the requestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestDTO,
     * or with status {@code 400 (Bad Request)} if the requestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the requestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RequestDTO>> updateRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RequestDTO requestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Request : {}, {}", id, requestDTO);
        if (requestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return requestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return requestService
                    .update(requestDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /requests/:id} : Partial updates given fields of an existing request, field will ignore if it is null
     *
     * @param id the id of the requestDTO to save.
     * @param requestDTO the requestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requestDTO,
     * or with status {@code 400 (Bad Request)} if the requestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the requestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the requestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RequestDTO>> partialUpdateRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RequestDTO requestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Request partially : {}, {}", id, requestDTO);
        if (requestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return requestRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RequestDTO> result = requestService.partialUpdate(requestDTO);

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
     * {@code GET  /requests} : get all the requests.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requests in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RequestDTO>>> getAllRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Requests");
        return requestService
            .countAll()
            .zipWith(requestService.findAll(pageable).collectList())
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
     * {@code GET  /requests/mine} : get requests for current user.
     */
    @GetMapping(value = "/mine", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RequestDTO>>> getMyRequests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        org.springframework.security.core.Authentication authentication,
        ServerHttpRequest request
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String cin = authentication.getName();
        return appUserRepository
            .findByCin(cin)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
            .flatMap(appUser -> requestService.findAllByCitizenId(appUser.getId(), pageable).collectList())
            .map(ResponseEntity::ok);
    }

    /**
     * {@code GET  /requests/:id} : get the "id" request.
     *
     * @param id the id of the requestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RequestDTO>> getRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Request : {}", id);
        Mono<RequestDTO> requestDTO = requestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(requestDTO);
    }

    /**
     * {@code DELETE  /requests/:id} : delete the "id" request.
     *
     * @param id the id of the requestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Request : {}", id);
        return requestService
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
