package com.estivate.test.query.h2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.H2Context;
import com.estivate.util.CachedEntity;
import com.estivate.util.EntityMetaCache;
import com.estivate.util.EntityMetaCache.FieldBinding;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Regression: {@code @Convert} on a superclass field must apply when mapping a subclass
 * through the typed {@code ResultSet} path ({@code EntityMapper.map(ResultSet)}).
 *
 * <p>Broken when {@code EntityMetaCache} does not inherit {@code fieldBindings}:
 * {@code getFieldBinding(inheritedField)} is null on the subclass cache and
 * {@code readTyped} skips the converter (raw JDBC value → {@code Field.set} →
 * {@code IllegalArgumentException}).
 */
public class InheritedConvertMappingTest {

	private H2Context context;

	@BeforeEach
	void setUp() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:inherited_convert_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1;MODE=MySQL");
		dataSource.setUser("sa");
		dataSource.setPassword("");
		context = new H2Context(dataSource);
		Estivate.Tools.createTableFullQuery(WithInheritedConvertEntity.class, context)
			.ifNotExists()
			.execute(context);
	}

	@Test
	void subclassMetaCache_exposesConverterBindingForInheritedField() throws Exception {
		Field properties = PropertyHolderBase.class.getDeclaredField("properties");
		EntityMetaCache subclassMeta = EntityMetaCache.of(WithInheritedConvertEntity.class);

		FieldBinding binding = subclassMeta.getFieldBinding(properties);

		assertNotNull(binding,
			"Subclass EntityMetaCache must expose FieldBinding for inherited @Convert fields "
				+ "(inherit parent.fieldBindings or equivalent)");
		assertTrue(binding.hasConverter(),
			"Inherited FieldBinding must retain the AttributeConverter");
		assertEquals(String.class, binding.getConvertDatabaseType(),
			"Database type should resolve from AttributeConverter<List, String>");
	}

	@Test
	void declaringClassLookup_stillFindsConverter() throws Exception {
		Field properties = PropertyHolderBase.class.getDeclaredField("properties");
		FieldBinding binding = EntityMetaCache.fieldBinding(properties);

		assertNotNull(binding);
		assertTrue(binding.hasConverter());
	}

	@Test
	void insertAndSelect_appliesInheritedConvertOnSubclass() {
		WithInheritedConvertEntity inserted = new WithInheritedConvertEntity();
		inserted.setName("channel-two");
		inserted.setProperties(List.of("WORKFLOW_TRIGGER", "ATTR"));

		context.insert(inserted);
		assertTrue(inserted.getId() > 0L, "generated id should be applied after insert");

		WithInheritedConvertEntity loaded = context.extractSingle(
			Estivate.selectQuery(WithInheritedConvertEntity.class)
				.eq("id", inserted.getId()));

		assertNotNull(loaded);
		assertEquals("channel-two", loaded.getName());
		assertNotNull(loaded.getProperties());
		assertInstanceOf(List.class, loaded.getProperties());
		assertEquals(List.of("WORKFLOW_TRIGGER", "ATTR"), loaded.getProperties());
		assertFalse(loaded.getProperties().getClass().getName().equals("[B"),
			"must not leave raw byte[] on the List field");
	}

	@Test
	void insertAndSelect_nullInheritedConvertColumn_doesNotThrow() {
		WithInheritedConvertEntity inserted = new WithInheritedConvertEntity();
		inserted.setName("channel-one");
		inserted.setProperties(null);

		context.insert(inserted);

		WithInheritedConvertEntity loaded = context.extractSingle(
			Estivate.selectQuery(WithInheritedConvertEntity.class)
				.eq("id", inserted.getId()));

		assertNotNull(loaded);
		assertEquals("channel-one", loaded.getName());
		// converter may map null → empty list or leave null; either is fine as long as no throw
		if (loaded.getProperties() != null) {
			assertTrue(loaded.getProperties().isEmpty());
		}
	}

	@Converter
	public static class StringListConverter implements AttributeConverter<List<String>, String> {

		@Override
		public String convertToDatabaseColumn(List<String> attribute) {
			if (attribute == null || attribute.isEmpty()) {
				return null;
			}
			return String.join(",", attribute);
		}

		@Override
		public List<String> convertToEntityAttribute(String dbData) {
			if (dbData == null || dbData.isBlank()) {
				return new ArrayList<>();
			}
			return new ArrayList<>(List.of(dbData.split(",")));
		}
	}

	@MappedSuperclass
	@Getter
	@Setter
	@NoArgsConstructor
	public abstract static class PropertyHolderBase extends CachedEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		long id;

		@Convert(converter = StringListConverter.class)
		List<String> properties;
	}

	@Entity
	@Getter
	@Setter
	@NoArgsConstructor
	public static class WithInheritedConvertEntity extends PropertyHolderBase {

		String name;
	}
}
