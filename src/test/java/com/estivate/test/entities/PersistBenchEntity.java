package com.estivate.test.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Small mapped type shared by the Estivate / JDBC / Hibernate persist benchmark.
 * The note column uses a converter so the benchmark includes converter handling.
 */
@Entity
@Table(name = "PERSISTBENCHENTITY")
public class PersistBenchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long id;

    @Column(name = "NAME")
    public String name;

    @Column(name = "EMAIL")
    public String email;

    @Column(name = "SCORE")
    public int score;

    @Column(name = "ACTIVE")
    public boolean active;

    @Convert(converter = NoteConverter.class)
    @Column(name = "NOTE")
    public String note;

    public static class NoteConverter implements AttributeConverter<String, String> {

        @Override
        public String convertToDatabaseColumn(String attribute) {
            return attribute;
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return dbData;
        }
    }
}
