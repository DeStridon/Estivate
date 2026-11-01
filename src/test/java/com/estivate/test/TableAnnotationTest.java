package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomTableNameEntity;
import com.estivate.util.EntityMetaCache;

/**
 * {@code @Table(name = "…")} overrides the NameMapper-derived table name everywhere
 * Estivate renders SQL (FROM / UPDATE / column prefixes).
 */
public class TableAnnotationTest {

	private static final String TABLE = "legacy_widget_inventory";

	Context context = DatabaseGenerator.getContext();

	@BeforeEach
	public void setUp() {
		Estivate.Tools.createTableFullQuery(CustomTableNameEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(CustomTableNameEntity.class);
	}

	@Test
	public void annotatedTableNameIsCachedOnEntityMeta() {
		assertEquals(TABLE, EntityMetaCache.of(CustomTableNameEntity.class).getAnnotatedTableName());
		assertEquals(TABLE, context.nameMapper.toTableName(CustomTableNameEntity.class));
		assertEquals(TABLE + ".SKU", context.nameMapper.toTableNameAttribute(
			CustomTableNameEntity.class, CustomTableNameEntity.Fields.sku));
		// Without @Table, TestNameMapper would use the uppercase simple class name:
		assertEquals("CUSTOMTABLENAMEENTITY", context.nameMapper.mapDatabaseClass(CustomTableNameEntity.class));
	}

	@Test
	public void selectAndUpdateSqlUseAnnotatedTableName() {
		String selectSql = context.queryAsString(
			Estivate.selectQuery(CustomTableNameEntity.class)
				.select(CustomTableNameEntity.Fields.sku)
				.eq(AbstractEntity.Fields.id, 1L));
		assertTrue(selectSql.contains("FROM " + TABLE), selectSql);
		assertTrue(selectSql.contains(TABLE + ".SKU"), selectSql);
		assertFalse(selectSql.contains("CUSTOMTABLENAMEENTITY"), selectSql);

		String updateSql = context.queryAsString(
			Estivate.updateQuery(CustomTableNameEntity.class)
				.set(CustomTableNameEntity.Fields.quantity, 3)
				.eq(AbstractEntity.Fields.id, 1L));
		assertTrue(updateSql.startsWith("UPDATE " + TABLE + " "), updateSql);
		assertFalse(updateSql.contains("CUSTOMTABLENAMEENTITY"), updateSql);
	}

	@Test
	public void persistAndSelectAgainstAnnotatedTable() {
		CustomTableNameEntity inserted = CustomTableNameEntity.builder()
			.sku("WGT-42")
			.quantity(7)
			.build();
		context.insert(inserted);

		CustomTableNameEntity loaded = Estivate.selectQuery(CustomTableNameEntity.class)
			.eq(CustomTableNameEntity.Fields.sku, "WGT-42")
			.extractSingle(context);

		assertEquals(inserted.getId(), loaded.getId());
		assertEquals("WGT-42", loaded.getSku());
		assertEquals(7, loaded.getQuantity());
		assertTrue(context.showTables().contains(TABLE.toUpperCase())
			|| context.showTables().contains(TABLE));
	}
}
