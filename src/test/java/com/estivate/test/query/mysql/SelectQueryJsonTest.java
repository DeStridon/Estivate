package com.estivate.test.query.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.JsonDocumentEntity;

/**
 * JSON filter/order fidelity against a real MySQL 8 server (Testcontainers).
 * <p>
 * Skipped automatically when Docker is unavailable.
 * {@code JSON_EXISTS} is not a MySQL function (H2-alias coverage only).
 */
@Testcontainers(disabledWithoutDocker = true)
public class SelectQueryJsonTest {

	@Container
	static MySQLContainer<?> mysql = MysqlTestDatabase.newContainer();

	static Context context;

	@BeforeAll
	static void openContext() {
		context = MysqlTestDatabase.createContext(mysql);
	}

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
		// MySQL returns a JSON type from JSON_EXTRACT; comparing to a bound VARCHAR
		// needs UNQUOTE (literal '"Paris"' works in raw SQL, prepared params do not).
		Attribute.Function unquotedCity = Estivate.function(
			Estivate.Functions.json_unquote,
			Estivate.Functions.json_extract("$.city"));

		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.eq(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, unquotedCity),
				"Paris");

		String queryString = context.queryAsString(query);
		assertTrue(queryString.contains("JSON_EXTRACT("), queryString);
		assertTrue(queryString.contains("JSON_UNQUOTE("), queryString);

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
	void filterOnJsonLength() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.gt(
				Estivate.attribute(JsonDocumentEntity.class, JsonDocumentEntity.Fields.payload, Estivate.Functions.json_length),
				2);

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high"), names);
	}

	@Test
	void orderByJsonExtractCityAsc() {
		SelectQuery<JsonDocumentEntity> query = Estivate.selectQuery(JsonDocumentEntity.class)
			.orderByAsc(
				JsonDocumentEntity.class,
				JsonDocumentEntity.Fields.payload,
				Estivate.Functions.json_extract("$.city"));

		List<String> cities = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(doc -> cityFromPayload(doc.getPayload()))
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
			.map(doc -> cityFromPayload(doc.getPayload()))
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

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-high", "paris-mid", "lyon-low"), names);
	}

	@Test
	void filterAndOrderByJsonValue() {
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

		List<String> names = context.extractList(query, JsonDocumentEntity.class).stream()
			.map(JsonDocumentEntity::getName)
			.collect(Collectors.toList());

		assertEquals(Arrays.asList("paris-mid", "paris-high"), names);
	}

	private static String cityFromPayload(String payload) {
		int key = payload.indexOf("\"city\":\"");
		if (key < 0) {
			return null;
		}
		int start = key + "\"city\":\"".length();
		int end = payload.indexOf('"', start);
		return payload.substring(start, end);
	}
}
