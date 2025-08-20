package com.example.gateway.domain;

import static com.example.gateway.domain.RequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Request.class);
        Request request1 = getRequestSample1();
        Request request2 = new Request();
        assertThat(request1).isNotEqualTo(request2);

        request2.setId(request1.getId());
        assertThat(request1).isEqualTo(request2);

        request2 = getRequestSample2();
        assertThat(request1).isNotEqualTo(request2);
    }
}
