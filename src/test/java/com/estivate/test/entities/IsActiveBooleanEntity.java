package com.estivate.test.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * Field literally named {@code isActive}. Lombok generates getter {@code isActive()}.
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class IsActiveBooleanEntity extends AbstractEntity {

	boolean isActive;
}
