package com.estivate.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.estivate.reconciliation.ColumnModel.EntityColumn;
import com.estivate.reconciliation.ColumnTypeParts;
import com.estivate.reconciliation.TableField;
import com.estivate.util.FieldUtils;

import jakarta.persistence.Column;

/**
 * Ensures {@code SMALLINT UNSIGNED} (and similar signedness modifiers) survive
 * parsing, entity modeling, and reconciliation type comparison.
 */
public class UnsignedColumnTypeTest {

	public static class UnsignedSmallintEntity {
		@Column(columnDefinition = "SMALLINT UNSIGNED")
		Integer code;
	}

	public static class UnsignedSmallintWithDisplayWidthEntity {
		@Column(columnDefinition = "SMALLINT(5) UNSIGNED")
		Integer code;
	}

	@Test
	public void parseSmallintUnsigned() {
		ColumnTypeParts parts = ColumnTypeParts.parse("SMALLINT UNSIGNED");
		assertEquals("SMALLINT UNSIGNED", parts.getType());
		assertEquals(null, parts.getLength());
		assertEquals(null, parts.getScale());
	}

	@Test
	public void parseMysqlShowColumnsStyle() {
		ColumnTypeParts parts = ColumnTypeParts.parse("smallint unsigned");
		assertEquals("smallint unsigned", parts.getType());
		assertEquals(null, parts.getLength());
	}

	@Test
	public void parseSmallintUnsignedWithDisplayWidth() {
		ColumnTypeParts parts = ColumnTypeParts.parse("SMALLINT(5) UNSIGNED");
		assertEquals("SMALLINT UNSIGNED", parts.getType());
		assertEquals(5, parts.getLength());
	}

	@Test
	public void entityColumnPreservesUnsignedFromColumnDefinition() {
		EntityColumn column = Context.getEntityColumn(FieldUtils.findField(UnsignedSmallintEntity.class, "code"));
		assertEquals("SMALLINT UNSIGNED", column.getDesignedType());
	}

	@Test
	public void entityColumnPreservesUnsignedWithDisplayWidth() {
		EntityColumn column = Context.getEntityColumn(FieldUtils.findField(UnsignedSmallintWithDisplayWidthEntity.class, "code"));
		assertEquals("SMALLINT UNSIGNED", column.getDesignedType());
		assertEquals(5, column.getDesignedLength());
	}

	@Test
	public void mysqlTableFieldPreservesUnsignedForReconciliation() {
		MySQLContext context = new MySQLContext(null);
		TableField projected = context.getTableField(
				Context.getEntityColumn(FieldUtils.findField(UnsignedSmallintEntity.class, "code")));

		assertEquals("SMALLINT UNSIGNED", projected.getType());

		// MySQL SHOW COLUMNS Type value after parse
		ColumnTypeParts dbParts = ColumnTypeParts.parse("smallint unsigned");
		TableField database = TableField.builder()
				.name(projected.getName())
				.type(dbParts.getType())
				.nullable(true)
				.build();

		assertTrue(projected.typeMatches(database.getType()), "Reconciliation should treat entity SMALLINT UNSIGNED as matching DB smallint unsigned");
		assertFalse(projected.typeMatches("SMALLINT"), "Reconciliation should detect signed vs unsigned as a TYPE mismatch");
	}

	@Test
	public void appendColumnTypeAndSize_unsignedSmallint() {
		MySQLContext context = new MySQLContext(null);
		TableField field = TableField.builder().type("SMALLINT UNSIGNED").build();
		assertEquals("SMALLINT UNSIGNED", columnTypeSql(context, field));
	}

	@Test
	public void appendColumnTypeAndSize_unsignedSmallintWithDisplayWidth() {
		MySQLContext context = new MySQLContext(null);
		TableField field = TableField.builder().type("SMALLINT UNSIGNED").dimension(5).build();
		assertEquals("SMALLINT(5) UNSIGNED", columnTypeSql(context, field));
	}

	@Test
	public void appendColumnTypeAndSize_signedDecimal() {
		MySQLContext context = new MySQLContext(null);
		TableField field = TableField.builder().type("DECIMAL").dimension(10).scale(2).build();
		assertEquals("DECIMAL(10,2)", columnTypeSql(context, field));
	}

	@Test
	public void appendColumnTypeAndSize_unsignedDecimal() {
		MySQLContext context = new MySQLContext(null);
		TableField field = TableField.builder().type("DECIMAL UNSIGNED").dimension(10).scale(2).build();
		assertEquals("DECIMAL(10,2) UNSIGNED", columnTypeSql(context, field));
	}

	@Test
	public void appendColumnTypeAndSize_enumTypeUnchanged() {
		MySQLContext context = new MySQLContext(null);
		TableField field = TableField.builder().type("ENUM('A','B')").build();
		assertEquals("ENUM('A','B')", columnTypeSql(context, field));
	}

	private static String columnTypeSql(MySQLContext context, TableField field) {
		return MySQLContext.formatMySQLColumnType(field);
	}

}
