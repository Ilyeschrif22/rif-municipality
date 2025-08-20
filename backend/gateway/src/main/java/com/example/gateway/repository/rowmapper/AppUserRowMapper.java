package com.example.gateway.repository.rowmapper;

import com.example.gateway.domain.AppUser;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppUser}, with proper type conversions.
 */
@Service
public class AppUserRowMapper implements BiFunction<Row, String, AppUser> {

    private final ColumnConverter converter;

    public AppUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppUser} stored in the database.
     */
    @Override
    public AppUser apply(Row row, String prefix) {
        AppUser entity = new AppUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", String.class));
        entity.setCin(converter.fromRow(row, prefix + "_cin", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setMunicipalityId(converter.fromRow(row, prefix + "_municipality_id", Long.class));
        entity.setPasswordHash(converter.fromRow(row, prefix + "_password_hash", String.class));
        return entity;
    }
}
