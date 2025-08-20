package com.example.request.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Request getRequestSample1() {
        return new Request().id(1L).type("type1").description("description1").citizenId(1L).municipalityId(1L);
    }

    public static Request getRequestSample2() {
        return new Request().id(2L).type("type2").description("description2").citizenId(2L).municipalityId(2L);
    }

    public static Request getRequestRandomSampleGenerator() {
        return new Request()
            .id(longCount.incrementAndGet())
            .type(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .citizenId(longCount.incrementAndGet())
            .municipalityId(longCount.incrementAndGet());
    }
}
