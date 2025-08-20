package com.example.municipality.web.rest;

import static com.example.municipality.domain.MunicipalityAsserts.*;
import static com.example.municipality.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.municipality.IntegrationTest;
import com.example.municipality.domain.Municipality;
import com.example.municipality.repository.MunicipalityRepository;
import com.example.municipality.service.dto.MunicipalityDTO;
import com.example.municipality.service.mapper.MunicipalityMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link MunicipalityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restMunicipalityMockMvc;

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

    @BeforeEach
    void initTest() {
        municipality = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMunicipality != null) {
            municipalityRepository.delete(insertedMunicipality);
            insertedMunicipality = null;
        }
    }

    @Test
    @Transactional
    void createMunicipality() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);
        var returnedMunicipalityDTO = om.readValue(
            restMunicipalityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MunicipalityDTO.class
        );

        // Validate the Municipality in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMunicipality = municipalityMapper.toEntity(returnedMunicipalityDTO);
        assertMunicipalityUpdatableFieldsEquals(returnedMunicipality, getPersistedMunicipality(returnedMunicipality));

        insertedMunicipality = returnedMunicipality;
    }

    @Test
    @Transactional
    void createMunicipalityWithExistingId() throws Exception {
        // Create the Municipality with an existing ID
        municipality.setId(1L);
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMunicipalityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setName(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        restMunicipalityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRegionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setRegion(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        restMunicipalityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        municipality.setCountry(null);

        // Create the Municipality, which fails.
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        restMunicipalityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMunicipalities() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        // Get all the municipalityList
        restMunicipalityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(municipality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @Test
    @Transactional
    void getMunicipality() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        // Get the municipality
        restMunicipalityMockMvc
            .perform(get(ENTITY_API_URL_ID, municipality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(municipality.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getNonExistingMunicipality() throws Exception {
        // Get the municipality
        restMunicipalityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMunicipality() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality
        Municipality updatedMunicipality = municipalityRepository.findById(municipality.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMunicipality are not directly saved in db
        em.detach(updatedMunicipality);
        updatedMunicipality.name(UPDATED_NAME).region(UPDATED_REGION).country(UPDATED_COUNTRY);
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(updatedMunicipality);

        restMunicipalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, municipalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(municipalityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMunicipalityToMatchAllProperties(updatedMunicipality);
    }

    @Test
    @Transactional
    void putNonExistingMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, municipalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(municipalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(municipalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMunicipalityWithPatch() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality using partial update
        Municipality partialUpdatedMunicipality = new Municipality();
        partialUpdatedMunicipality.setId(municipality.getId());

        restMunicipalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMunicipality.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMunicipality))
            )
            .andExpect(status().isOk());

        // Validate the Municipality in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMunicipalityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMunicipality, municipality),
            getPersistedMunicipality(municipality)
        );
    }

    @Test
    @Transactional
    void fullUpdateMunicipalityWithPatch() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the municipality using partial update
        Municipality partialUpdatedMunicipality = new Municipality();
        partialUpdatedMunicipality.setId(municipality.getId());

        partialUpdatedMunicipality.name(UPDATED_NAME).region(UPDATED_REGION).country(UPDATED_COUNTRY);

        restMunicipalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMunicipality.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMunicipality))
            )
            .andExpect(status().isOk());

        // Validate the Municipality in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMunicipalityUpdatableFieldsEquals(partialUpdatedMunicipality, getPersistedMunicipality(partialUpdatedMunicipality));
    }

    @Test
    @Transactional
    void patchNonExistingMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, municipalityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(municipalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(municipalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMunicipality() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        municipality.setId(longCount.incrementAndGet());

        // Create the Municipality
        MunicipalityDTO municipalityDTO = municipalityMapper.toDto(municipality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMunicipalityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(municipalityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Municipality in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMunicipality() throws Exception {
        // Initialize the database
        insertedMunicipality = municipalityRepository.saveAndFlush(municipality);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the municipality
        restMunicipalityMockMvc
            .perform(delete(ENTITY_API_URL_ID, municipality.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return municipalityRepository.count();
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
        return municipalityRepository.findById(municipality.getId()).orElseThrow();
    }

    protected void assertPersistedMunicipalityToMatchAllProperties(Municipality expectedMunicipality) {
        assertMunicipalityAllPropertiesEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
    }

    protected void assertPersistedMunicipalityToMatchUpdatableProperties(Municipality expectedMunicipality) {
        assertMunicipalityAllUpdatablePropertiesEquals(expectedMunicipality, getPersistedMunicipality(expectedMunicipality));
    }
}
