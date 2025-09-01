package com.example.gateway.service.impl;

import com.example.gateway.repository.RequestRepository;
import com.example.gateway.repository.AppUserRepository;
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

    private final AppUserRepository appUserRepository;

    public RequestServiceImpl(RequestRepository requestRepository, RequestMapper requestMapper, AppUserRepository appUserRepository) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Mono<RequestDTO> save(RequestDTO requestDTO) {
        LOG.debug("Request to save Request : {}", requestDTO);
        return requestRepository.save(requestMapper.toEntity(requestDTO)).map(requestMapper::toDto);
    }

    @Override
    public Mono<RequestDTO> update(RequestDTO requestDTO) {
        LOG.debug("Request to update Request : {}", requestDTO);
        return requestRepository
            .save(requestMapper.toEntity(requestDTO))
            .flatMap(request -> {
                RequestDTO updatedDTO = requestMapper.toDto(request);
                if (request.getCitizenId() != null) {
                    return appUserRepository
                        .findById(request.getCitizenId())
                        .map(appUser -> {
                            updatedDTO.setCitizenFirstName(appUser.getFirstName());
                            updatedDTO.setCitizenLastName(appUser.getLastName());
                            updatedDTO.setCitizenEmail(appUser.getEmail());
                            updatedDTO.setCitizenPhone(appUser.getPhone());
                            updatedDTO.setCitizenCin(appUser.getCin());
                            return updatedDTO;
                        })
                        .defaultIfEmpty(updatedDTO);
                }
                return Mono.just(updatedDTO);
            });
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
            .flatMap(request -> {
                RequestDTO updatedDTO = requestMapper.toDto(request);
                if (request.getCitizenId() != null) {
                    return appUserRepository
                        .findById(request.getCitizenId())
                        .map(appUser -> {
                            updatedDTO.setCitizenFirstName(appUser.getFirstName());
                            updatedDTO.setCitizenLastName(appUser.getLastName());
                            updatedDTO.setCitizenEmail(appUser.getEmail());
                            updatedDTO.setCitizenPhone(appUser.getPhone());
                            updatedDTO.setCitizenCin(appUser.getCin());
                            return updatedDTO;
                        })
                        .defaultIfEmpty(updatedDTO);
                }
                return Mono.just(updatedDTO);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RequestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Requests with citizen information");
        return requestRepository
            .findAllBy(pageable)
            .flatMap(request -> {
                RequestDTO requestDTO = requestMapper.toDto(request);
                if (request.getCitizenId() != null) {
                    return appUserRepository
                        .findById(request.getCitizenId())
                        .map(appUser -> {
                            requestDTO.setCitizenFirstName(appUser.getFirstName());
                            requestDTO.setCitizenLastName(appUser.getLastName());
                            requestDTO.setCitizenEmail(appUser.getEmail());
                            requestDTO.setCitizenPhone(appUser.getPhone());
                            requestDTO.setCitizenCin(appUser.getCin());
                            LOG.debug("Found citizen data for request {}: CIN={}, Name={} {}", 
                                request.getId(), appUser.getCin(), appUser.getFirstName(), appUser.getLastName());
                            return requestDTO;
                        })
                        .doOnError(error -> LOG.warn("Error fetching citizen data for request {} with citizenId {}: {}", 
                            request.getId(), request.getCitizenId(), error.getMessage()))
                        .onErrorReturn(requestDTO)
                        .switchIfEmpty(Mono.defer(() -> {
                            LOG.warn("No citizen found for request {} with citizenId {}", request.getId(), request.getCitizenId());
                            return Mono.just(requestDTO);
                        }));
                }
                return Mono.just(requestDTO);
            });
    }

    public Mono<Long> countAll() {
        return requestRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RequestDTO> findOne(Long id) {
        LOG.debug("Request to get Request : {}", id);
        return requestRepository
            .findById(id)
            .flatMap(request -> {
                RequestDTO requestDTO = requestMapper.toDto(request);
                if (request.getCitizenId() != null) {
                    return appUserRepository
                        .findById(request.getCitizenId())
                        .map(appUser -> {
                            requestDTO.setCitizenFirstName(appUser.getFirstName());
                            requestDTO.setCitizenLastName(appUser.getLastName());
                            requestDTO.setCitizenEmail(appUser.getEmail());
                            requestDTO.setCitizenPhone(appUser.getPhone());
                            requestDTO.setCitizenCin(appUser.getCin());
                            LOG.debug("Found citizen data for request {}: CIN={}, Name={} {}", 
                                request.getId(), appUser.getCin(), appUser.getFirstName(), appUser.getLastName());
                            return requestDTO;
                        })
                        .doOnError(error -> LOG.warn("Error fetching citizen data for request {} with citizenId {}: {}", 
                            request.getId(), request.getCitizenId(), error.getMessage()))
                        .onErrorReturn(requestDTO)
                        .switchIfEmpty(Mono.defer(() -> {
                            LOG.warn("No citizen found for request {} with citizenId {}", request.getId(), request.getCitizenId());
                            return Mono.just(requestDTO);
                        }));
                }
                return Mono.just(requestDTO);
            });
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Request : {}", id);
        return requestRepository.deleteById(id);
    }
}
