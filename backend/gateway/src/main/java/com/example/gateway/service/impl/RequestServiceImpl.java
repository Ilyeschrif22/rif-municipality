package com.example.gateway.service.impl;

import com.example.gateway.repository.RequestRepository;
import com.example.gateway.service.RequestService;
import com.example.gateway.service.dto.RequestDTO;
import com.example.gateway.service.mapper.RequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.example.gateway.domain.Request}.
 */
@Service
@Transactional
public class RequestServiceImpl implements RequestService {

    private static final Logger LOG = LoggerFactory.getLogger(RequestServiceImpl.class);

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    public RequestServiceImpl(RequestRepository requestRepository, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
    }

    @Override
    public Mono<RequestDTO> save(RequestDTO requestDTO) {
        LOG.debug("Request to save Request : {}", requestDTO);
        return requestRepository.save(requestMapper.toEntity(requestDTO)).map(requestMapper::toDto);
    }

    @Override
    public Mono<RequestDTO> update(RequestDTO requestDTO) {
        LOG.debug("Request to update Request : {}", requestDTO);
        return requestRepository.save(requestMapper.toEntity(requestDTO)).map(requestMapper::toDto);
    }

    @Override
    public Flux<RequestDTO> findAllByCitizenId(Long citizenId, Pageable pageable) {
        return requestRepository
            .findByCitizenId(citizenId)
            .map(requestMapper::toDto);
    }

    @Override
    public Mono<RequestDTO> partialUpdate(RequestDTO requestDTO) {
        LOG.debug("Request to partially update Request : {}", requestDTO);

        return requestRepository
            .findById(requestDTO.getId())
            .map(existingRequest -> {
                requestMapper.partialUpdate(existingRequest, requestDTO);

                return existingRequest;
            })
            .flatMap(requestRepository::save)
            .map(requestMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Requests");
        return requestRepository.findAllBy(pageable).map(requestMapper::toDto);
    }

    public Mono<Long> countAll() {
        return requestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RequestDTO> findOne(Long id) {
        LOG.debug("Request to get Request : {}", id);
        return requestRepository.findById(id).map(requestMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Request : {}", id);
        return requestRepository.deleteById(id);
    }
}
