package com.estivate.test.entities;

import javax.persistence.Table;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * Example entity whose physical table name is set explicitly via {@code @Table(name=…)},
 * instead of the NameMapper class-name mapping ({@code CUSTOMTABLENAMEENTITY}).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Entity
@Table(name = "legacy_widget_inventory")
public class CustomTableNameEntity extends AbstractEntity {

	String sku;

	int quantity;
}
