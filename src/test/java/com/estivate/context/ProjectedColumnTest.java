package com.estivate.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.estivate.index.Annotations.ColumnDefaultValue;
import com.estivate.reconciliation.ProjectedColumn;
import com.estivate.reconciliation.ProjectedColumn.ColumnDimension;
import com.estivate.reconciliation.TableField;
import com.estivate.util.FieldUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.experimental.FieldNameConstants;

/**
 * Verifies {@link ProjectedColumn} maps entity fields to dialect-resolved {@link TableField}s.
 */
public class ProjectedColumnTest {

	MySQLContext mysql = new MySQLContext(null);
	H2Context h2 = new H2Context(null);

	public enum Status {
		ACTIVE, INACTIVE
	}

	public static class StringAsVarcharConverter implements AttributeConverter<Object, String> {
		@Override
		public String convertToDatabaseColumn(Object attribute) {
			return attribute == null ? null : attribute.toString();
		}

		@Override
		public Object convertToEntityAttribute(String dbData) {
			return dbData;
		}
	}

	@FieldNameConstants
	public static class SampleEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		Long id;

		int primitiveInt;

		Integer boxedInteger;

		Long boxedLong;

		Short boxedShort;

		Byte boxedByte;

		Float boxedFloat;

		Double boxedDouble;

		Boolean boxedBoolean;

		boolean primitiveBoolean;

		String plainString;

		@Column(length = 100)
		String stringWithLength;

		@Column(columnDefinition = "VARCHAR(50)")
		String varcharFromDefinition;

		@Column(columnDefinition = "TEXT")
		String textFromDefinition;

		@Column(precision = 10, scale = 2)
		BigDecimal decimalFromPrecisionScale;

		@Column(columnDefinition = "DECIMAL(8,4)")
		BigDecimal decimalFromDefinition;

		@Column(nullable = false, columnDefinition = "TIMESTAMP(6)")
		Instant timestampWithFsp;

		Instant plainInstant;

		LocalDateTime localDateTime;

		LocalDate localDate;

		Date legacyDate;

		byte[] binaryData;

		@Column(columnDefinition = "SMALLINT UNSIGNED")
		Integer unsignedSmallint;

		@Column(columnDefinition = "SMALLINT(5) UNSIGNED")
		Integer unsignedSmallintWithWidth;

		@Enumerated(EnumType.STRING)
		Status enumAsString;

		Status enumAsOrdinal;

		@Convert(converter = StringAsVarcharConverter.class)
		Object convertedValue;

		@Column(nullable = false)
		String notNullableString;

		@ColumnDefaultValue("hello")
		String withDefaultValue;
	}

	private ProjectedColumn project(String fieldName) {
		return mysql.projectedColumn(FieldUtils.findField(SampleEntity.class, fieldName));
	}

	private TableField tableField(String fieldName) {
		return project(fieldName).getTableField();
	}

	private void assertTableField(String fieldName, String type, Integer dimension, Integer scale, boolean nullable, boolean autoIncrement) {
		TableField tf = tableField(fieldName);
		assertEquals(fieldName, tf.getName(), fieldName + " name");
		assertEquals(type, tf.getType(), fieldName + " type");
		assertEquals(dimension, tf.getDimension(), fieldName + " dimension");
		assertEquals(scale, tf.getScale(), fieldName + " scale");
		assertEquals(nullable, tf.isNullable(), fieldName + " nullable");
		assertEquals(autoIncrement, tf.isAutoIncrement(), fieldName + " autoIncrement");
	}

	// --- Java type defaults (MySQL) ---

	@Test
	public void mapsNumericJavaTypes() {
		assertTableField(SampleEntity.Fields.boxedInteger, "INT", null, null, true, false);
		assertTableField(SampleEntity.Fields.boxedLong, "BIGINT", null, null, true, false);
		assertTableField(SampleEntity.Fields.boxedShort, "SMALLINT", null, null, true, false);
		assertTableField(SampleEntity.Fields.boxedByte, "TINYINT", null, null, true, false);
		assertTableField(SampleEntity.Fields.boxedFloat, "FLOAT", null, null, true, false);
		assertTableField(SampleEntity.Fields.boxedDouble, "DOUBLE", null, null, true, false);
	}

	@Test
	public void mapsBooleanAndBit() {
		assertTableField(SampleEntity.Fields.boxedBoolean, "BIT", 1, null, true, false);
		assertTableField(SampleEntity.Fields.primitiveBoolean, "BIT", 1, null, false, false);
	}

	@Test
	public void mapsStringDefaultsAndOverrides() {
		assertTableField(SampleEntity.Fields.plainString, "VARCHAR", 255, null, true, false);
		assertTableField(SampleEntity.Fields.stringWithLength, "VARCHAR", 100, null, true, false);
		assertTableField(SampleEntity.Fields.varcharFromDefinition, "VARCHAR", 50, null, true, false);
		assertTableField(SampleEntity.Fields.textFromDefinition, "TEXT", null, null, true, false);
	}

	@Test
	public void mapsDecimalFromAnnotationAndDefinition() {
		ProjectedColumn fromAnnotation = project(SampleEntity.Fields.decimalFromPrecisionScale);
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, fromAnnotation.getDimensionType());
		assertTableField(SampleEntity.Fields.decimalFromPrecisionScale, "DECIMAL", 10, 2, true, false);

		ProjectedColumn fromDefinition = project(SampleEntity.Fields.decimalFromDefinition);
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, fromDefinition.getDimensionType());
		assertTableField(SampleEntity.Fields.decimalFromDefinition, "DECIMAL", 8, 4, true, false);
	}

	@Test
	public void mapsTemporalTypes() {
		ProjectedColumn timestamp = project(SampleEntity.Fields.timestampWithFsp);
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, timestamp.getDimensionType());
		assertTableField(SampleEntity.Fields.timestampWithFsp, "TIMESTAMP", 6, null, false, false);

		assertTableField(SampleEntity.Fields.plainInstant, "DATETIME", null, null, true, false);
		assertTableField(SampleEntity.Fields.localDateTime, "DATETIME", null, null, true, false);
		assertTableField(SampleEntity.Fields.localDate, "DATE", null, null, true, false);
		assertTableField(SampleEntity.Fields.legacyDate, "DATETIME", null, null, true, false);
	}

	@Test
	public void mapsBinary() {
		assertTableField(SampleEntity.Fields.binaryData, "BLOB", null, null, true, false);
	}

	// --- columnDefinition special cases ---

	@Test
	public void mapsUnsignedTypes() {
		assertTableField(SampleEntity.Fields.unsignedSmallint, "SMALLINT UNSIGNED", null, null, true, false);
		assertTableField(SampleEntity.Fields.unsignedSmallintWithWidth, "SMALLINT UNSIGNED", 5, null, true, false);
	}

	// --- enums / convert / identity ---

	@Test
	public void mapsEnumAsStringToEnumType() {
		TableField tf = tableField(SampleEntity.Fields.enumAsString);
		assertEquals("ENUM('ACTIVE','INACTIVE')", tf.getType());
		assertNull(tf.getDimension());
		assertTrue(tf.isNullable());
	}

	@Test
	public void mapsEnumOrdinalToTinyint() {
		assertTableField(SampleEntity.Fields.enumAsOrdinal, "TINYINT", null, null, true, false);
	}

	@Test
	public void mapsConvertToVarchar() {
		ProjectedColumn projected = project(SampleEntity.Fields.convertedValue);
		assertEquals(String.class, projected.getJavaType());
		assertTableField(SampleEntity.Fields.convertedValue, "VARCHAR", 255, null, true, false);
	}

	@Test
	public void mapsIdentityPrimaryKey() {
		ProjectedColumn projected = project(SampleEntity.Fields.id);
		assertTrue(projected.getPrimaryKey());
		assertTableField(SampleEntity.Fields.id, "BIGINT", null, null, true, true);
	}

	@Test
	public void mapsPrimitiveAsNotNullable() {
		assertTableField(SampleEntity.Fields.primitiveInt, "INT", null, null, false, false);
	}

	@Test
	public void mapsNullableFalseFromColumn() {
		assertFalse(tableField(SampleEntity.Fields.notNullableString).isNullable());
		assertEquals("VARCHAR", tableField(SampleEntity.Fields.notNullableString).getType());
		assertEquals(255, tableField(SampleEntity.Fields.notNullableString).getDimension());
	}

	@Test
	public void mapsColumnDefaultValue() {
		TableField tf = tableField(SampleEntity.Fields.withDefaultValue);
		assertEquals("hello", tf.getDefaultValue());
		assertEquals("VARCHAR", tf.getType());
	}

	// --- H2 dialect defaults ---

	@Test
	public void h2MapsCommonJavaTypes() {
		assertEquals("INTEGER", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.boxedInteger)).getTableField().getType());
		assertEquals("INTEGER", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.boxedLong)).getTableField().getType());
		assertEquals("CHARACTER VARYING", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.plainString)).getTableField().getType());
		assertEquals("BOOLEAN", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.boxedBoolean)).getTableField().getType());
		assertEquals("TIMESTAMP", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.plainInstant)).getTableField().getType());
		assertEquals("DECIMAL", h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.decimalFromPrecisionScale)).getTableField().getType());

		TableField decimal = h2.projectedColumn(FieldUtils.findField(SampleEntity.class, SampleEntity.Fields.decimalFromPrecisionScale)).getTableField();
		assertEquals(10, decimal.getDimension());
		assertEquals(2, decimal.getScale());
	}
}
