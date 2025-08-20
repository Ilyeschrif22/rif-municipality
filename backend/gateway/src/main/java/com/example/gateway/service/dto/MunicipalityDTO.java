package com.example.gateway.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.example.gateway.domain.Municipality} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MunicipalityDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @NotNull(message = "must not be null")
    private String region;

    @NotNull(message = "must not be null")
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MunicipalityDTO)) {
            return false;
        }

        MunicipalityDTO municipalityDTO = (MunicipalityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, municipalityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MunicipalityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", region='" + getRegion() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
