package com.example.request.web.rest;

import static com.example.request.domain.RequestAsserts.*;
import static com.example.request.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.request.IntegrationTest;
import com.example.request.domain.Request;
import com.example.request.domain.enumeration.RequestStatus;
import com.example.request.repository.RequestRepository;
import com.example.request.service.dto.RequestDTO;
import com.example.request.service.mapper.RequestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restRequestMockMvc;

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

    @BeforeEach
    void initTest() {
        request = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRequest != null) {
            requestRepository.delete(insertedRequest);
            insertedRequest = null;
        }
    }

    @Test
    @Transactional
    void createRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);
        var returnedRequestDTO = om.readValue(
            restRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RequestDTO.class
        );

        // Validate the Request in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRequest = requestMapper.toEntity(returnedRequestDTO);
        assertRequestUpdatableFieldsEquals(returnedRequest, getPersistedRequest(returnedRequest));

        insertedRequest = returnedRequest;
    }

    @Test
    @Transactional
    void createRequestWithExistingId() throws Exception {
        // Create the Request with an existing ID
        request.setId(1L);
        RequestDTO requestDTO = requestMapper.toDto(request);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setType(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        restRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setDescription(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        restRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        request.setStatus(null);

        // Create the Request, which fails.
        RequestDTO requestDTO = requestMapper.toDto(request);

        restRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRequests() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

        // Get all the requestList
        restRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(request.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].resolvedDate").value(hasItem(DEFAULT_RESOLVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].citizenId").value(hasItem(DEFAULT_CITIZEN_ID.intValue())))
            .andExpect(jsonPath("$.[*].municipalityId").value(hasItem(DEFAULT_MUNICIPALITY_ID.intValue())));
    }

    @Test
    @Transactional
    void getRequest() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

        // Get the request
        restRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, request.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(request.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.resolvedDate").value(DEFAULT_RESOLVED_DATE.toString()))
            .andExpect(jsonPath("$.citizenId").value(DEFAULT_CITIZEN_ID.intValue()))
            .andExpect(jsonPath("$.municipalityId").value(DEFAULT_MUNICIPALITY_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingRequest() throws Exception {
        // Get the request
        restRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRequest() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the request
        Request updatedRequest = requestRepository.findById(request.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRequest are not directly saved in db
        em.detach(updatedRequest);
        updatedRequest
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdDate(UPDATED_CREATED_DATE)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .citizenId(UPDATED_CITIZEN_ID)
            .municipalityId(UPDATED_MUNICIPALITY_ID);
        RequestDTO requestDTO = requestMapper.toDto(updatedRequest);

        restRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, requestDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO))
            )
            .andExpect(status().isOk());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRequestToMatchAllProperties(updatedRequest);
    }

    @Test
    @Transactional
    void putNonExistingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, requestDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(requestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

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
            .municipalityId(UPDATED_MUNICIPALITY_ID);

        restRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequest))
            )
            .andExpect(status().isOk());

        // Validate the Request in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRequest, request), getPersistedRequest(request));
    }

    @Test
    @Transactional
    void fullUpdateRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

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

        restRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRequest))
            )
            .andExpect(status().isOk());

        // Validate the Request in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequestUpdatableFieldsEquals(partialUpdatedRequest, getPersistedRequest(partialUpdatedRequest));
    }

    @Test
    @Transactional
    void patchNonExistingRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, requestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(requestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        request.setId(longCount.incrementAndGet());

        // Create the Request
        RequestDTO requestDTO = requestMapper.toDto(request);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(requestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Request in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRequest() throws Exception {
        // Initialize the database
        insertedRequest = requestRepository.saveAndFlush(request);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the request
        restRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, request.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return requestRepository.count();
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
        return requestRepository.findById(request.getId()).orElseThrow();
    }

    protected void assertPersistedRequestToMatchAllProperties(Request expectedRequest) {
        assertRequestAllPropertiesEquals(expectedRequest, getPersistedRequest(expectedRequest));
    }

    protected void assertPersistedRequestToMatchUpdatableProperties(Request expectedRequest) {
        assertRequestAllUpdatablePropertiesEquals(expectedRequest, getPersistedRequest(expectedRequest));
    }
}
