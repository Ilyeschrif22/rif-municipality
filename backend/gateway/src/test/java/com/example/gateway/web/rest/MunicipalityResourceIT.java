package com.example.gateway.web.rest;

import static com.example.gateway.domain.MunicipalityAsserts.*;
import static com.example.gateway.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.example.gateway.IntegrationTest;
import com.example.gateway.domain.Municipality;
import com.example.gateway.repository.EntityManager;
import com.example.gateway.repository.MunicipalityRepository;
import com.example.gateway.service.dto.MunicipalityDTO;
import com.example.gateway.service.mapper.MunicipalityMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link MunicipalityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MunicipalityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/municipalities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private MunicipalityMapper municipalityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Municipality municipality;

    private Municipality insertedMunicipality;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Municipality createEntity() {
        return new Municipality().name(DEFAULT_NAME).region(DEFAULT_REGION).country(DEFAULT_COUNTRY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Municipality createUpdatedEntity() {
        return new Municipality().name(UPDATED_NAME).region(UPDATED_REGION).country(UPDATED_COUNTRY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Municipality.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        municipality = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMunicipality != null) {
            municipalityRepository.delete(insertedMunicipality).block();
            insertedMunicipality = null;
        }
        deleteEntities(em);
    }

    @Test
    void createMunicipality() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);
        var returnedMunicipalityDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MunicipalityDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Municipality in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMunicipality = municipalityMapper.toEntity(returnedMunicipalityDTO);
        assertMunicipalityUpdatableFieldsEquals(returnedMunicipality, getPersistedMunicipality(returnedMunicipality));

        insertedMunicipality = returnedMunicipality;
    }

    @Test
    void createMunicipalityWithExistingId() throws Exception {
        // Create the Municipality with an existing ID
        municipality.setId(1L);
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setName(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRegionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setRegion(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setCountry(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMunicipalities() {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        // Get all the municipalityList
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
            .value(hasItem(municipality.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].region")
            .value(hasItem(DEFAULT_REGION))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY));
    }

    @Test
    void getMunicipality() {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        // Get the municipality
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, municipality.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(municipality.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.region")
            .value(is(DEFAULT_REGION))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY));
    }

    @Test
    void getNonExistingMunicipality() {
        // Get the municipality
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMunicipality() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality
        Municipality updatedMunicipality = municipalityRepository.findById(municipality.getId()).block();
        updatedMunicipality.name(UPDATED_NAME).region(UPDATED_REGION).country(UPDATED_COUNTRY);
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(updatedMunicipality);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, municipalityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMunicipalityToMatchAllProperties(updatedMunicipality);
    }

    @Test
    void putNonExistingMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, municipalityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMunicipalityWithPatch() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality using partial update
        Municipality partialUpdatedMunicipality = new Municipality();
        partialUpdatedMunicipality.setId(municipality.getId());

        partialUpdatedMunicipality.name(UPDATED_NAME).country(UPDATED_COUNTRY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMunicipality.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMunicipality))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipality in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMunicipalityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMunicipality, municipality),
            getPersistedMunicipality(municipality)
        );
    }

    @Test
    void fullUpdateMunicipalityWithPatch() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality using partial update
        Municipality partialUpdatedMunicipality = new Municipality();
        partialUpdatedMunicipality.setId(municipality.getId());

        partialUpdatedMunicipality.name(UPDATED_NAME).region(UPDATED_REGION).country(UPDATED_COUNTRY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMunicipality.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMunicipality))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipality in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMunicipalityUpdatableFieldsEquals(partialUpdatedMunicipality, getPersistedMunicipality(partialUpdatedMunicipality));
    }

    @Test
    void patchNonExistingMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, municipalityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(municipalityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMunicipality() {
        // Initialize the database
        insertedMunicipality = municipalityRepository.save(municipality).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the municipality
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, municipality.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return municipalityRepository.count().block();
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

    protected Municipality getPersistedMunicipality(Municipality municipality) {
        return municipalityRepository.findById(municipality.getId()).block();
    }

    protected void assertPersistedMunicipalityToMatchAllProperties(Municipality expectedMunicipality) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMunicipalityAllPropertiesEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
        assertMunicipalityUpdatableFieldsEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
    }

    protected void assertPersistedMunicipalityToMatchUpdatableProperties(Municipality expectedMunicipality) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMunicipalityAllUpdatablePropertiesEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
        assertMunicipalityUpdatableFieldsEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
    }
}
