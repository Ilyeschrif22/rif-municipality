package com.example.gateway.repository.rowmapper;

import com.example.gateway.domain.Municipality;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Municipality}, with proper type conversions.
 */
@Service
public class MunicipalityRowMapper implements BiFunction<Row, String, Municipality> {

    private final ColumnConverter converter;

    public MunicipalityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Municipality} stored in the database.
     */
    @Override
    public Municipality apply(Row row, String prefix) {
        Municipality entity = new Municipality();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setRegion(converter.fromRow(row, prefix + "_region", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        return entity;
    }
}
