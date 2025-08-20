package com.example.gateway.web.rest;

import static com.example.gateway.domain.RequestAsserts.*;
import static com.example.gateway.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.example.gateway.IntegrationTest;
import com.example.gateway.domain.Request;
import com.example.gateway.domain.enumeration.RequestStatus;
import com.example.gateway.repository.EntityManager;
import com.example.gateway.repository.RequestRepository;
import com.example.gateway.service.dto.RequestDTO;
import com.example.gateway.service.mapper.RequestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RequestResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final RequestStatus DEFAULT_STATUS = RequestStatus.PENDING;
    private static final RequestStatus UPDATED_STATUS = RequestStatus.IN_PROGRESS;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RESOLVED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_CITIZEN_ID = 1L;
    private static final Long UPDATED_CITIZEN_ID = 2L;

    private static final Long DEFAULT_MUNICIPALITY_ID = 1L;
    private static final Long UPDATED_MUNICIPALITY_ID = 2L;

    private static final String ENTITY_API_URL = "/api/requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Request request;

    private Request insertedRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Request createEntity() {
        return new Request()
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .createdDate(DEFAULT_CREATED_DATE)
            .resolvedDate(DEFAULT_RESOLVED_DATE)
            .citizenId(DEFAULT_CITIZEN_ID)
            .municipalityId(DEFAULT_MUNICIPALITY_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Request createUpdatedEntity() {
        return new Request()
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .citizenId(UPDATED_CITIZEN_ID)
            .municipalityId(UPDATED_MUNICIPALITY_ID);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Request.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        request = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRequest != null) {
            requestRepository.delete(insertedRequest).block();
            insertedRequest = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);
        var returnedRequestDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RequestDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Request in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRequest = requestMapper.toEntity(returnedRequestDTO);
        assertRequestUpdatableFieldsEquals(returnedRequest, getPersistedRequest(returnedRequest));

        insertedRequest = returnedRequest;
    }

    @Test
    void createRequestWithExistingId() throws Exception {
        // Create the Request with an existing ID
        request.setId(1L);
        RequestDTO requestDTO = requestMapper.toDto(request);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setType(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setDescription(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setStatus(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRequests() {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        // Get all the requestList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(request.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].resolvedDate")
            .value(hasItem(DEFAULT_RESOLVED_DATE.toString()))
            .jsonPath("$.[*].citizenId")
            .value(hasItem(DEFAULT_CITIZEN_ID.intValue()))
            .jsonPath("$.[*].municipalityId")
            .value(hasItem(DEFAULT_MUNICIPALITY_ID.intValue()));
    }

    @Test
    void getRequest() {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        // Get the request
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, request.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(request.getId().intValue()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.resolvedDate")
            .value(is(DEFAULT_RESOLVED_DATE.toString()))
            .jsonPath("$.citizenId")
            .value(is(DEFAULT_CITIZEN_ID.intValue()))
            .jsonPath("$.municipalityId")
            .value(is(DEFAULT_MUNICIPALITY_ID.intValue()));
    }

    @Test
    void getNonExistingRequest() {
        // Get the request
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRequest() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the request
        Request updatedRequest = requestRepository.findById(request.getId()).block();
        updatedRequest
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .citizenId(UPDATED_CITIZEN_ID)
            .municipalityId(UPDATED_MUNICIPALITY_ID);
        RequestDTO requestDTO = requestMapper.toDto(updatedRequest);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, requestDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRequestToMatchAllProperties(updatedRequest);
    }

    @Test
    void putNonExistingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, requestDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the request using partial update
        Request partialUpdatedRequest = new Request();
        partialUpdatedRequest.setId(request.getId());

        partialUpdatedRequest
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .citizenId(UPDATED_CITIZEN_ID)
            .municipalityId(UPDATED_MUNICIPALITY_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRequest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRequest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Request in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRequest, request), getPersistedRequest(request));
    }

    @Test
    void fullUpdateRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the request using partial update
        Request partialUpdatedRequest = new Request();
        partialUpdatedRequest.setId(request.getId());

        partialUpdatedRequest
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .citizenId(UPDATED_CITIZEN_ID)
            .municipalityId(UPDATED_MUNICIPALITY_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRequest.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRequest))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Request in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestUpdatableFieldsEquals(partialUpdatedRequest, getPersistedRequest(partialUpdatedRequest));
    }

    @Test
    void patchNonExistingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, requestDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requestDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRequest() {
        // Initialize the database
        insertedRequest = requestRepository.save(request).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the request
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, request.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return requestRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Request getPersistedRequest(Request request) {
        return requestRepository.findById(request.getId()).block();
    }

    protected void assertPersistedRequestToMatchAllProperties(Request expectedRequest) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRequestAllPropertiesEquals(expectedRequest, getPersistedRequest(expectedRequest));
        assertRequestUpdatableFieldsEquals(expectedRequest, getPersistedRequest(expectedRequest));
    }

    protected void assertPersistedRequestToMatchUpdatableProperties(Request expectedRequest) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRequestAllUpdatablePropertiesEquals(expectedRequest, getPersistedRequest(expectedRequest));
        assertRequestUpdatableFieldsEquals(expectedRequest, getPersistedRequest(expectedRequest));
    }
}
