package com.example.gateway.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Document getDocumentSample1() {
        return new Document().id(1L).title("title1").type("type1").fileContentType("fileContentType1").citizenId(1L);
    }

    public static Document getDocumentSample2() {
        return new Document().id(2L).title("title2").type("type2").fileContentType("fileContentType2").citizenId(2L);
    }

    public static Document getDocumentRandomSampleGenerator() {
        return new Document()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString())
            .fileContentType(UUID.randomUUID().toString())
            .citizenId(longCount.incrementAndGet());
    }
}
