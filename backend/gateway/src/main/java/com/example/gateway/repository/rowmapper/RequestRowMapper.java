package com.example.gateway.repository.rowmapper;

import com.example.gateway.domain.Request;
import com.example.gateway.domain.enumeration.RequestStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Request}, with proper type conversions.
 */
@Service
public class RequestRowMapper implements BiFunction<Row, String, Request> {

    private final ColumnConverter converter;

    public RequestRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Request} stored in the database.
     */
    @Override
    public Request apply(Row row, String prefix) {
        Request entity = new Request();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", RequestStatus.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setResolvedDate(converter.fromRow(row, prefix + "_resolved_date", Instant.class));
        entity.setCitizenId(converter.fromRow(row, prefix + "_citizen_id", Long.class));
        entity.setMunicipalityId(converter.fromRow(row, prefix + "_municipality_id", Long.class));
        return entity;
    }
}
