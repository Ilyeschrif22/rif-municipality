package com.example.municipality.service.mapper;

import static com.example.municipality.domain.MunicipalityAsserts.*;
import static com.example.municipality.domain.MunicipalityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MunicipalityMapperTest {

    private MunicipalityMapper municipalityMapper;

    @BeforeEach
    void setUp() {
        municipalityMapper = new MunicipalityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMunicipalitySample1();
        var actual = municipalityMapper.toEntity(municipalityMapper.toDto(expected));
        assertMunicipalityAllPropertiesEquals(expected, actual);
    }
}
