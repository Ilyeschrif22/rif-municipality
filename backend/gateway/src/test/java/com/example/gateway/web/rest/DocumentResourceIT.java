package com.example.gateway.web.rest;

import static com.example.gateway.domain.DocumentAsserts.*;
import static com.example.gateway.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.example.gateway.IntegrationTest;
import com.example.gateway.domain.Document;
import com.example.gateway.repository.DocumentRepository;
import com.example.gateway.repository.EntityManager;
import com.example.gateway.service.dto.DocumentDTO;
import com.example.gateway.service.mapper.DocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
 * Integration tests for the {@link DocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DocumentResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";
    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";
    private static final Instant DEFAULT_ISSUE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ISSUE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";
    private static final Long DEFAULT_CITIZEN_ID = 1L;
    private static final Long UPDATED_CITIZEN_ID = 2L;

    private static final String ENTITY_API_URL = "/api/documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Document document;

    private Document insertedDocument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createEntity() {
        return new Document()
            .title(DEFAULT_TITLE)
            .type(DEFAULT_TYPE)
            .issueDate(DEFAULT_ISSUE_DATE)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .citizenId(DEFAULT_CITIZEN_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createUpdatedEntity() {
        return new Document()
            .title(UPDATED_TITLE)
            .type(UPDATED_TYPE)
            .issueDate(UPDATED_ISSUE_DATE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .citizenId(UPDATED_CITIZEN_ID);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Document.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        document = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDocument != null) {
            documentRepository.delete(insertedDocument).block();
            insertedDocument = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDocument() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);
        var returnedDocumentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DocumentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Document in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDocument = documentMapper.toEntity(returnedDocumentDTO);
        assertDocumentUpdatableFieldsEquals(returnedDocument, getPersistedDocument(returnedDocument));

        insertedDocument = returnedDocument;
    }

    @Test
    void createDocumentWithExistingId() throws Exception {
        // Create the Document with an existing ID
        document.setId(1L);
        DocumentDTO documentDTO = documentMapper.toDto(document);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        document.setTitle(null);

        // Create the Document, which fails.
        DocumentDTO documentDTO = documentMapper.toDto(document);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        document.setType(null);

        // Create the Document, which fails.
        DocumentDTO documentDTO = documentMapper.toDto(document);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDocuments() {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        // Get all the documentList
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
            .value(hasItem(document.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].issueDate")
            .value(hasItem(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.[*].fileContentType")
            .value(hasItem(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.[*].file")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE)))
            .jsonPath("$.[*].fileContentType")
            .value(hasItem(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.[*].citizenId")
            .value(hasItem(DEFAULT_CITIZEN_ID.intValue()));
    }

    @Test
    void getDocument() {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        // Get the document
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, document.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(document.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE))
            .jsonPath("$.issueDate")
            .value(is(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.fileContentType")
            .value(is(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.file")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_FILE)))
            .jsonPath("$.fileContentType")
            .value(is(DEFAULT_FILE_CONTENT_TYPE))
            .jsonPath("$.citizenId")
            .value(is(DEFAULT_CITIZEN_ID.intValue()));
    }

    @Test
    void getNonExistingDocument() {
        // Get the document
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDocument() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the document
        Document updatedDocument = documentRepository.findById(document.getId()).block();
        updatedDocument
            .title(UPDATED_TITLE)
            .type(UPDATED_TYPE)
            .issueDate(UPDATED_ISSUE_DATE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .citizenId(UPDATED_CITIZEN_ID);
        DocumentDTO documentDTO = documentMapper.toDto(updatedDocument);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, documentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocumentToMatchAllProperties(updatedDocument);
    }

    @Test
    void putNonExistingDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, documentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .type(UPDATED_TYPE)
            .issueDate(UPDATED_ISSUE_DATE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .citizenId(UPDATED_CITIZEN_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDocument))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Document in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDocument, document), getPersistedDocument(document));
    }

    @Test
    void fullUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .title(UPDATED_TITLE)
            .type(UPDATED_TYPE)
            .issueDate(UPDATED_ISSUE_DATE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .citizenId(UPDATED_CITIZEN_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDocument))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Document in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentUpdatableFieldsEquals(partialUpdatedDocument, getPersistedDocument(partialUpdatedDocument));
    }

    @Test
    void patchNonExistingDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, documentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        document.setId(longCount.incrementAndGet());

        // Create the Document
        DocumentDTO documentDTO = documentMapper.toDto(document);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(documentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Document in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDocument() {
        // Initialize the database
        insertedDocument = documentRepository.save(document).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the document
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, document.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return documentRepository.count().block();
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

    protected Document getPersistedDocument(Document document) {
        return documentRepository.findById(document.getId()).block();
    }

    protected void assertPersistedDocumentToMatchAllProperties(Document expectedDocument) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDocumentAllPropertiesEquals(expectedDocument, getPersistedDocument(expectedDocument));
        assertDocumentUpdatableFieldsEquals(expectedDocument, getPersistedDocument(expectedDocument));
    }

    protected void assertPersistedDocumentToMatchUpdatableProperties(Document expectedDocument) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDocumentAllUpdatablePropertiesEquals(expectedDocument, getPersistedDocument(expectedDocument));
        assertDocumentUpdatableFieldsEquals(expectedDocument, getPersistedDocument(expectedDocument));
    }
}
