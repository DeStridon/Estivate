package com.estivate.test.query.h2;

import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * AttributeConverter.convertToEntityAttribute must be invoked for SQL NULL values.
 */
public class AttributeConverterNullMappingTest {

    Context context = DatabaseGenerator.getContext();

    @BeforeEach
    public void setUp() {
        Estivate.Tools.createTableFullQuery(NullDefaultEntity.class, context).ifNotExists().execute(context);
        context.truncateTable(NullDefaultEntity.class);
    }

    @Test
    public void mapsNullDatabaseValueThroughAttributeConverter() {
        NullDefaultEntity inserted = NullDefaultEntity.builder().name("with-null-tags").build();
        inserted.setTags(null);
        context.insert(inserted);

        NullDefaultEntity loaded = Estivate.selectQuery(NullDefaultEntity.class)
            .eq(NullDefaultEntity::getId, inserted.getId())
            .extractSingle(context);

        Assert.assertNotNull(loaded);
        Assert.assertNotNull("Converter should turn SQL NULL into an empty list", loaded.getTags());
        Assert.assertTrue(loaded.getTags().isEmpty());
        Assert.assertEquals("unset", loaded.getLabel());
    }

    @Test
    public void stillMapsNonNullDatabaseValueThroughAttributeConverter() {
        NullDefaultEntity inserted = NullDefaultEntity.builder()
            .name("with-tags")
            .tags(List.of("a", "b"))
            .label("set")
            .build();
        context.insert(inserted);

        NullDefaultEntity loaded = Estivate.selectQuery(NullDefaultEntity.class)
            .eq(NullDefaultEntity::getId, inserted.getId())
            .extractSingle(context);

        Assert.assertEquals(List.of("a", "b"), loaded.getTags());
        Assert.assertEquals("set", loaded.getLabel());
    }

    @Data
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class NullDefaultEntity extends AbstractEntity {

        String name;

        @Convert(converter = NullToEmptyListConverter.class)
        List<String> tags;

        @Convert(converter = NullToUnsetLabelConverter.class)
        String label;
    }

    @Converter
    public static class NullToEmptyListConverter implements AttributeConverter<List<String>, String> {

        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            if(attribute == null || attribute.isEmpty()) {
                return null;
            }
            return String.join(",", attribute);
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            if(dbData == null || dbData.isBlank()) {
                return Collections.emptyList();
            }
            return List.of(dbData.split(","));
        }
    }

    @Converter
    public static class NullToUnsetLabelConverter implements AttributeConverter<String, String> {

        @Override
        public String convertToDatabaseColumn(String attribute) {
            if(attribute == null || "unset".equals(attribute)) {
                return null;
            }
            return attribute;
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return dbData == null ? "unset" : dbData;
        }
    }
}
