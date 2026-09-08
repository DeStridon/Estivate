package com.estivate.context;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.estivate.reconciliation.ColumnTypeParts;
import com.estivate.reconciliation.ProjectedColumn;
import com.estivate.reconciliation.ProjectedColumn.ColumnDimension;
import com.estivate.reconciliation.TableField;
import com.estivate.util.FieldUtils;

import jakarta.persistence.Column;
import lombok.experimental.FieldNameConstants;

/**
 * Ensures column definitions such as {@code TIMESTAMP(6)}, {@code DECIMAL(10,3)},
 * and {@code SMALLINT UNSIGNED} are read correctly from {@code @Column(columnDefinition)}
 * into entity and table field models.
 */
public class TimestampColumnTypeTest {

	MySQLContext mysqlContext = new MySQLContext(null);

	@FieldNameConstants
	public static class TestEntity {

		@Column(nullable = false, columnDefinition = "TIMESTAMP(6)")
		Instant testTimestampWithDefinition;

		@Column(nullable = false, columnDefinition = "TIMESTAMP", precision = 6)
		Instant testTimestampWithPrecision;

		@Column(columnDefinition = "DATETIME(6)")
		Date testDatetimeWithDefinition;

		@Column(columnDefinition = "DATETIME", precision = 6)
		Date testDatetimeWithPrecision;

		@Column(columnDefinition = "DECIMAL(10,3)")
		BigDecimal testDecimalWithDefinition;

		@Column(columnDefinition = "DECIMAL", precision = 10, scale = 3)
		BigDecimal testDecimalWithPrecisionAndScale;

		@Column(columnDefinition = "SMALLINT UNSIGNED")
		Integer testSmallintUnsignedWithDefinition;

	}

	@Test
	public void manualTimestampColumnDefinitionParsing() {
		ColumnTypeParts parts = ColumnTypeParts.parse("TIMESTAMP(6)");
		assertEquals("TIMESTAMP", parts.getType());
		assertEquals(6, parts.getLength());
		assertNull(parts.getScale());
	}

	@Test
	public void timestampColumnWithDefinitionParsing() {
		ProjectedColumn column = mysqlContext.projectedColumn(FieldUtils.findField(TestEntity.class, TestEntity.Fields.testTimestampWithDefinition));

		assertEquals("TIMESTAMP", column.getType());
		assertEquals(6, column.getDimension());
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, column.getDimensionType());
		assertNull(column.getScale());
		assertFalse(column.isNullable());
	}

	@Test
	public void timestampColumnWithLengthParsing() {
		ProjectedColumn column = mysqlContext.projectedColumn(FieldUtils.findField(TestEntity.class, TestEntity.Fields.testTimestampWithPrecision));

		assertEquals("TIMESTAMP", column.getType());
		assertEquals(6, column.getDimension());
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, column.getDimensionType());
		assertFalse(column.isNullable());
	}

	@Test
	public void decimalColumnWithDefinitionParsing() {
		ProjectedColumn column = mysqlContext.projectedColumn(FieldUtils.findField(TestEntity.class, TestEntity.Fields.testDecimalWithDefinition));

		assertEquals("DECIMAL", column.getType());
		assertEquals(10, column.getDimension());
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, column.getDimensionType());
		assertEquals(3, column.getScale());
	}

	@Test
	public void decimalColumnWithPrecisionAndScaleParsing() {
		ProjectedColumn column = mysqlContext.projectedColumn(FieldUtils.findField(TestEntity.class, TestEntity.Fields.testDecimalWithPrecisionAndScale));

		assertEquals("DECIMAL", column.getType());
		assertEquals(10, column.getDimension());
		assertEquals(ColumnDimension.PRECISION_OPTIONAL, column.getDimensionType());
		assertEquals(3, column.getScale());
	}


	@Test
	public void smallintUnsignedColumnWithDefinitionParsing() {
		ProjectedColumn column = mysqlContext.projectedColumn(FieldUtils.findField(TestEntity.class, TestEntity.Fields.testSmallintUnsignedWithDefinition));

		assertEquals("SMALLINT UNSIGNED", column.getType());
		assertEquals(ColumnDimension.LENGTH_OPTIONAL, column.getDimensionType());
		assertNull(column.getDimension());
		assertNull(column.getScale());
	}

	@Test
	public void appendColumnTypeAndSize_timestampWithFsp() {
		TableField field = TableField.builder().type("TIMESTAMP").dimension(6).build();
		assertEquals("TIMESTAMP(6)", MySQLContext.formatMySQLColumnType(field));
	}

	@Test
	public void appendColumnTypeAndSize_decimalWithPrecisionAndScale() {
		TableField field = TableField.builder().type("DECIMAL").dimension(10).scale(3).build();
		assertEquals("DECIMAL(10,3)", MySQLContext.formatMySQLColumnType(field));
	}

	@Test
	public void appendColumnTypeAndSize_smallintUnsigned() {
		TableField field = TableField.builder().type("SMALLINT UNSIGNED").build();
		assertEquals("SMALLINT UNSIGNED", MySQLContext.formatMySQLColumnType(field));
	}





}
