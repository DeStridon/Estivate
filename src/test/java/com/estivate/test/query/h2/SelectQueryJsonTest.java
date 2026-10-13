package com.estivate.test.query.h2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.JsonDocumentEntity;

/**
 * JSON attribute functions in WHERE / ORDER BY: SQL shape and runtime fetch on H2
 * (MySQL JSON_* aliases registered in {@link DatabaseGenerator}).
 */
@NotThreadSafe
public class SelectQueryJsonTest {

	Context context = DatabaseGenerator.getContext();

	@BeforeEach
	void seedDocuments() {
		context.truncateTable(JsonDocumentEntity.class);

		context.insert(Arrays.asList(
			JsonDocumentEntity.builder()
				.name("paris-high")
				.payload("{\"city\":\"Paris\",\"score\":30,\"tags\":[\"a\",\"b\"]}")
				.build(),
			JsonDocumentEntity.builder()
				.name("lyon-low")
				.payload("{\"city\":\"Lyon\",\"score\":10}")
				.build(),
			JsonDocumentEntity.builder()
				.name("paris-mid")
				.payload("{\"city\":\"Paris\",\"score\":20}")
				.build()));
	}

	@Test
	void filterOnJsonExtractProperty() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_extract("$.city")), "\"Paris\"");

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_EXTRACT("), queryString);
		assertTrue(queryString.contains("$.city"), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.sorted()
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high", "paris-mid"), names);
	}

	@Test
	void filterOnJsonValueProperty() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_value("$.city")),
				"Paris");

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_VALUE("), queryString);
		assertTrue(queryString.contains("$.city"), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.sorted()
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high", "paris-mid"), names);
	}

	@Test
	void filterOnJsonValueSingleMatch() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_value("$.city")),
				"Lyon");

		List<JsonDocumentEntity> results = context.extractList(query, JsonDocumentEntity.class);

		assertEquals(1, results.size());
		assertEquals("lyon-low", results.get(0).getName());
	}

	@Test
	void filterOnUnquotedJsonExtract() {
		Attribute.Function unquotedCity = Estivate.function(
			Estivate.Functions.json_unquote,
			Estivate.Functions.json_extract("$.city"));

		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, unquotedCity),
				"Paris");

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_UNQUOTE(JSON_EXTRACT("), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.sorted()
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high", "paris-mid"), names);
	}

	@Test
	void filterOnJsonExists() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_exists("$.tags")),
				true);

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_EXISTS("), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high"), names);
	}

	@Test
	void filterOnJsonLength() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.gt(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_length),
				2);

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_LENGTH("), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.sorted()
			.collect(Collectors.toList());

		// only paris-high has 3 top-level keys (city/score/tags)
		assertEquals(Arrays.asList("paris-high"), names);
	}

	@Test
	void orderByJsonExtractPropertyAsc() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.orderByAsc(
				JsonDocumentEntity.class,
				JsonDocumentEntity.Fields.payload,
				Estivate.Functions.json_extract("$.city"));

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("ORDER BY"), queryString);
		assertTrue(queryString.contains("JSON_EXTRACT("), queryString);

		List<String> cities = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(doc -> H2JsonCity.fromPayload(doc.getPayload()))
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("Lyon", "Paris", "Paris"), cities);
	}

	@Test
	void orderByJsonValueCityAsc() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.orderByAsc(
				JsonDocumentEntity.class,
				JsonDocumentEntity.Fields.payload,
				Estivate.Functions.json_value("$.city"));

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("ORDER BY"), queryString);
		assertTrue(queryString.contains("JSON_VALUE("), queryString);

		List<String> cities = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(doc -> H2JsonCity.fromPayload(doc.getPayload()))
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("Lyon", "Paris", "Paris"), cities);
	}

	@Test
	void orderByJsonValueScoreDesc() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.orderByDesc(
				JsonDocumentEntity.class,
				JsonDocumentEntity.Fields.payload,
				Estivate.Functions.json_value("$.score"));

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("ORDER BY"), queryString);
		assertTrue(queryString.contains("JSON_VALUE("), queryString);
		assertTrue(queryString.contains("DESC"), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high", "paris-mid", "lyon-low"), names);
	}

	@Test
	void filterAndOrderBySameJsonProperty() {
		Attribute city = Estivate.attribute(
			JsonDocumentEntity.class,
			JsonDocumentEntity.Fields.payload,
			Estivate.Functions.json_value("$.city"));

		Attribute score = Estivate.attribute(
			JsonDocumentEntity.class,
			JsonDocumentEntity.Fields.payload,
			Estivate.Functions.json_value("$.score"));

		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(city, "Paris")
			.orderBy(score, Order.Direction.Asc);

		String queryString = context.queryAsString(query);
		assertTrue(queryString.toUpperCase().contains("WHERE"), queryString);
		assertTrue(queryString.contains("ORDER BY"), queryString);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-mid", "paris-high"), names);
	}

	/** Tiny helper so assertions stay readable without a full JSON dependency. */
	private static final class H2JsonCity {
		static String fromPayload(String payload) {
			int key = payload.indexOf("\"city\":\"");
			if (key < 0) {
				return null;
			}
			int start = key + "\"city\":\"".length();
			int end = payload.indexOf('"', start);
			return payload.substring(start, end);
		}
	}
}
