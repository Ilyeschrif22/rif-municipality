package com.example.municipality.repository;

import com.example.municipality.domain.Municipality;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Municipality entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {}
