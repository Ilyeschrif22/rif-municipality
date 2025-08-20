package com.example.request.service.mapper;

import static com.example.request.domain.RequestAsserts.*;
import static com.example.request.domain.RequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

    private RequestMapper requestMapper;

    @BeforeEach
    void setUp() {
        requestMapper = new RequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRequestSample1();
        var actual = requestMapper.toEntity(requestMapper.toDto(expected));
        assertRequestAllPropertiesEquals(expected, actual);
    }
}
