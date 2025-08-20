package com.example.gateway.service.mapper;

import static com.example.gateway.domain.MunicipalityAsserts.*;
import static com.example.gateway.domain.MunicipalityTestSamples.*;

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
