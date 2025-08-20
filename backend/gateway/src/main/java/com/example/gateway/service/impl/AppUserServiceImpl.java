package com.example.gateway.service.impl;

import com.example.gateway.repository.AppUserRepository;
import com.example.gateway.service.AppUserService;
import com.example.gateway.service.dto.AppUserDTO;
import com.example.gateway.service.mapper.AppUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.example.gateway.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserServiceImpl.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<AppUserDTO> save(AppUserDTO appUserDTO) {
        LOG.debug("Request to save AppUser : {}", appUserDTO);
        return appUserRepository
            .save(appUserMapper.toEntity(appUserDTO))
            .map(entity -> {
                if (appUserDTO.getPassword() != null && !appUserDTO.getPassword().isEmpty()) {
                    entity.setPasswordHash(passwordEncoder.encode(appUserDTO.getPassword()));
                }
                return entity;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    @Override
    public Mono<AppUserDTO> update(AppUserDTO appUserDTO) {
        LOG.debug("Request to update AppUser : {}", appUserDTO);
        return appUserRepository
            .save(appUserMapper.toEntity(appUserDTO))
            .map(entity -> {
                if (appUserDTO.getPassword() != null && !appUserDTO.getPassword().isEmpty()) {
                    entity.setPasswordHash(passwordEncoder.encode(appUserDTO.getPassword()));
                }
                return entity;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    @Override
    public Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO) {
        LOG.debug("Request to partially update AppUser : {}", appUserDTO);

        return appUserRepository
            .findById(appUserDTO.getId())
            .map(existingAppUser -> {
                appUserMapper.partialUpdate(existingAppUser, appUserDTO);

                return existingAppUser;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppUserDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppUsers");
        return appUserRepository.findAllBy(pageable).map(appUserMapper::toDto);
    }

    public Mono<Long> countAll() {
        return appUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppUserDTO> findOne(Long id) {
        LOG.debug("Request to get AppUser : {}", id);
        return appUserRepository.findById(id).map(appUserMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AppUser : {}", id);
        return appUserRepository.deleteById(id);
    }
}
