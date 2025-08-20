package com.example.gateway.domain;

import com.example.gateway.domain.enumeration.RequestStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Request.
 */
@Table("request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("type")
    private String type;

    @NotNull(message = "must not be null")
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("status")
    private RequestStatus status;

    @Column("created_date")
    private Instant createdDate;

    @Column("resolved_date")
    private Instant resolvedDate;

    @Column("citizen_id")
    private Long citizenId;

    @Column("municipality_id")
    private Long municipalityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Request id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public Request type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public Request description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestStatus getStatus() {
        return this.status;
    }

    public Request status(RequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Request createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getResolvedDate() {
        return this.resolvedDate;
    }

    public Request resolvedDate(Instant resolvedDate) {
        this.setResolvedDate(resolvedDate);
        return this;
    }

    public void setResolvedDate(Instant resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public Long getCitizenId() {
        return this.citizenId;
    }

    public Request citizenId(Long citizenId) {
        this.setCitizenId(citizenId);
        return this;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public Long getMunicipalityId() {
        return this.municipalityId;
    }

    public Request municipalityId(Long municipalityId) {
        this.setMunicipalityId(municipalityId);
        return this;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Request)) {
            return false;
        }
        return getId() != null && getId().equals(((Request) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Request{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", resolvedDate='" + getResolvedDate() + "'" +
            ", citizenId=" + getCitizenId() +
            ", municipalityId=" + getMunicipalityId() +
            "}";
    }
}
