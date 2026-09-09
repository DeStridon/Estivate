package com.estivate.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.estivate.reconciliation.DatabaseColumnDefinition;
import com.estivate.reconciliation.EntityColumn;
import com.estivate.reconciliation.EntityColumn.ColumnDimension;
import com.estivate.reconciliation.DatabaseColumn;
import com.estivate.util.FieldUtils;

import jakarta.persistence.Column;

/**
 * Ensures {@code SMALLINT UNSIGNED} (and similar signedness modifiers) survive
 * parsing, entity modeling, and reconciliation type comparison.
 */
public class UnsignedColumnTypeTest {

	MySQLContext mysqlContext = new MySQLContext(null);

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
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("SMALLINT UNSIGNED");
		assertEquals("SMALLINT UNSIGNED", parts.getType());
		assertEquals(null, parts.getDimension());
		assertEquals(null, parts.getScale());
	}

	@Test
	public void parseMysqlShowColumnsStyle() {
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("smallint unsigned");
		assertEquals("smallint unsigned", parts.getType());
		assertEquals(null, parts.getDimension());
	}

	@Test
	public void parseSmallintUnsignedWithDisplayWidth() {
		DatabaseColumnDefinition parts = DatabaseColumnDefinition.parse("SMALLINT(5) UNSIGNED");
		assertEquals("SMALLINT UNSIGNED", parts.getType());
		assertEquals(5, parts.getDimension());
	}

	@Test
	public void entityColumnPreservesUnsignedFromColumnDefinition() {
		EntityColumn column = mysqlContext.projectedColumn(FieldUtils.findField(UnsignedSmallintEntity.class, "code"));
		assertEquals("SMALLINT UNSIGNED", column.getType());
	}

	@Test
	public void entityColumnPreservesUnsignedWithDisplayWidth() {
		EntityColumn column = mysqlContext.projectedColumn(FieldUtils.findField(UnsignedSmallintWithDisplayWidthEntity.class, "code"));
		assertEquals("SMALLINT UNSIGNED", column.getType());
		assertEquals(5, column.getDimension());
		assertEquals(ColumnDimension.LENGTH_OPTIONAL, column.getDimensionType());
	}

	@Test
	public void mysqlTableFieldPreservesUnsignedForReconciliation() {
		MySQLContext context = new MySQLContext(null);
		DatabaseColumn projected = context.getTableField(FieldUtils.findField(UnsignedSmallintEntity.class, "code"));

		assertEquals("SMALLINT UNSIGNED", projected.getType());

		// MySQL SHOW COLUMNS Type value after parse
		DatabaseColumnDefinition dbParts = DatabaseColumnDefinition.parse("smallint unsigned");
		DatabaseColumn database = DatabaseColumn.builder()
				.name(projected.getName())
				.type(dbParts.getType())
				.nullable(true)
				.build();

		assertTrue(projected.typeMatches(database.getType()), "Reconciliation should treat entity SMALLINT UNSIGNED as matching DB smallint unsigned");
		assertFalse(projected.typeMatches("SMALLINT"), "Reconciliation should detect signed vs unsigned as a TYPE mismatch");
	}

	@Test
	public void appendColumnTypeAndSize_unsignedSmallint() {
		DatabaseColumn field = DatabaseColumn.builder().type("SMALLINT UNSIGNED").build();
		assertEquals("SMALLINT UNSIGNED", columnTypeSql(mysqlContext, field));
	}

	@Test
	public void appendColumnTypeAndSize_unsignedSmallintWithDisplayWidth() {
		DatabaseColumn field = DatabaseColumn.builder().type("SMALLINT UNSIGNED").dimension(5).build();
		assertEquals("SMALLINT(5) UNSIGNED", columnTypeSql(mysqlContext, field));
	}

	@Test
	public void appendColumnTypeAndSize_signedDecimal() {
		DatabaseColumn field = DatabaseColumn.builder().type("DECIMAL").dimension(10).scale(2).build();
		assertEquals("DECIMAL(10,2)", columnTypeSql(mysqlContext, field));
	}

	@Test
	public void appendColumnTypeAndSize_unsignedDecimal() {
		DatabaseColumn field = DatabaseColumn.builder().type("DECIMAL UNSIGNED").dimension(10).scale(2).build();
		assertEquals("DECIMAL(10,2) UNSIGNED", columnTypeSql(mysqlContext, field));
	}

	@Test
	public void appendColumnTypeAndSize_enumTypeUnchanged() {
		DatabaseColumn field = DatabaseColumn.builder().type("ENUM('A','B')").build();
		assertEquals("ENUM('A','B')", columnTypeSql(mysqlContext, field));
	}

	private static String columnTypeSql(MySQLContext context, DatabaseColumn field) {
		return MySQLContext.formatMySQLColumnType(field);
	}

}
