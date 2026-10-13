package com.estivate.test.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
public class JsonDocumentEntity extends AbstractEntity {

	String name;

	/**
	 * JSON document stored as text (works with MySQL {@code JSON_*} on VARCHAR/TEXT).
	 * Example: {@code {"city":"Paris","score":10,"tags":["a","b"]}}
	 */
	@Column(length = 2000)
	String payload;

}
