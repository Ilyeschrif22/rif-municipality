package com.example.gateway.service.impl;

import com.example.gateway.repository.MunicipalityRepository;
import com.example.gateway.service.MunicipalityService;
import com.example.gateway.service.dto.MunicipalityDTO;
import com.example.gateway.service.mapper.MunicipalityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.example.gateway.domain.Municipality}.
 */
@Service
@Transactional
public class MunicipalityServiceImpl implements MunicipalityService {

    private static final Logger LOG = LoggerFactory.getLogger(MunicipalityServiceImpl.class);

    private final MunicipalityRepository municipalityRepository;

    private final MunicipalityMapper municipalityMapper;

    public MunicipalityServiceImpl(MunicipalityRepository municipalityRepository, MunicipalityMapper municipalityMapper) {
        this.municipalityRepository = municipalityRepository;
        this.municipalityMapper = municipalityMapper;
    }

    @Override
    public Mono<MunicipalityDTO> save(MunicipalityDTO municipalityDTO) {
        LOG.debug("Request to save Municipality : {}", municipalityDTO);
        return municipalityRepository.save(municipalityMapper.toEntity(municipalityDTO)).map(municipalityMapper::toDto);
    }

    @Override
    public Mono<MunicipalityDTO> update(MunicipalityDTO municipalityDTO) {
        LOG.debug("Request to update Municipality : {}", municipalityDTO);
        return municipalityRepository.save(municipalityMapper.toEntity(municipalityDTO)).map(municipalityMapper::toDto);
    }

    @Override
    public Mono<MunicipalityDTO> partialUpdate(MunicipalityDTO municipalityDTO) {
        LOG.debug("Request to partially update Municipality : {}", municipalityDTO);

        return municipalityRepository
            .findById(municipalityDTO.getId())
            .map(existingMunicipality -> {
                municipalityMapper.partialUpdate(existingMunicipality, municipalityDTO);

                return existingMunicipality;
            })
            .flatMap(municipalityRepository::save)
            .map(municipalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MunicipalityDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Municipalities");
        return municipalityRepository.findAllBy(pageable).map(municipalityMapper::toDto);
    }

    public Mono<Long> countAll() {
        return municipalityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MunicipalityDTO> findOne(Long id) {
        LOG.debug("Request to get Municipality : {}", id);
        return municipalityRepository.findById(id).map(municipalityMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Municipality : {}", id);
        return municipalityRepository.deleteById(id);
    }
}
