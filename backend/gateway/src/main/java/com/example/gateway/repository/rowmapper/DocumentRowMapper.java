package com.example.gateway.repository.rowmapper;

import com.example.gateway.domain.Document;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Document}, with proper type conversions.
 */
@Service
public class DocumentRowMapper implements BiFunction<Row, String, Document> {

    private final ColumnConverter converter;

    public DocumentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Document} stored in the database.
     */
    @Override
    public Document apply(Row row, String prefix) {
        Document entity = new Document();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setIssueDate(converter.fromRow(row, prefix + "_issue_date", Instant.class));
        entity.setFileContentType(converter.fromRow(row, prefix + "_file_content_type", String.class));
        entity.setFile(converter.fromRow(row, prefix + "_file", byte[].class));
        entity.setFileContentType(converter.fromRow(row, prefix + "_file_content_type", String.class));
        entity.setCitizenId(converter.fromRow(row, prefix + "_citizen_id", Long.class));
        return entity;
    }
}
