package com.example.gateway.repository;

import com.example.gateway.domain.Request;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Request entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RequestRepository extends ReactiveCrudRepository<Request, Long>, RequestRepositoryInternal {
    Flux<Request> findAllBy(Pageable pageable);

    Flux<Request> findByCitizenId(Long citizenId);

    @Override
    <S extends Request> Mono<S> save(S entity);

    @Override
    Flux<Request> findAll();

    @Override
    Mono<Request> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RequestRepositoryInternal {
    <S extends Request> Mono<S> save(S entity);

    Flux<Request> findAllBy(Pageable pageable);

    Flux<Request> findAll();

    Mono<Request> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Request> findAllBy(Pageable pageable, Criteria criteria);
}
