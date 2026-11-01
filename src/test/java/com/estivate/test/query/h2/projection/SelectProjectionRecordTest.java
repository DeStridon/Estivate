package com.estivate.test.query.h2.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;
import com.estivate.util.EntityMetaCache;

/**
 * Record components can carry {@code @Projection.*} annotations ({@link java.lang.annotation.ElementType#RECORD_COMPONENT}
 * + {@link java.lang.annotation.ElementType#FIELD}); they propagate to the generated fields that Estivate reads.
 */
public class SelectProjectionRecordTest {

	Context context = DatabaseGenerator.getContext();

	private CustomerEntity customer;

	public record CustomerBasicRecord(
		@Projection.Attribute(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
		Long id,
		@Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.name)
		String name,
		@Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.email)
		String email
	) {}

	public record CustomerCountByCountryRecord(
		@Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.country)
		CustomerEntity.Country country,
		@Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
		Long count
	) {}

	public record NestedLineRecord(
		@Projection.Attribute(entity = ProductEntity.class, attribute = ProductEntity.Fields.category)
		ProductCategory category,
		@Projection.Count(entity = ProductEntity.class, attribute = AbstractEntity.Fields.id)
		Integer productCount
	) {}

	public record NestedOrderDetailsRecord(
		@Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.status)
		OrderEntity.OrderStatus status
	) {}

	/**
	 * Nested {@link List} is seeded as a mutable {@link java.util.ArrayList} in the canonical constructor,
	 * then filled by {@code @Projection.Nested} mapping. Nested singles are ctor args.
	 */
	public record OrderWithNestedRecord(
		@Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.customerId)
		Long customerId,
		@Projection.Nested
		List<NestedLineRecord> lines,
		@Projection.Nested
		NestedOrderDetailsRecord details
	) {}

	@BeforeEach
	public void setUp() {
		Estivate.Tools.createTableFullQuery(CustomerEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(CustomerEntity.class);

		customer = new CustomerEntity();
		customer.setName("Ada Lovelace");
		customer.setEmail("ada@example.com");
		customer.setAddress("Analytical Engine Rd");
		customer.setCountry(CustomerEntity.Country.UK);
		customer.setEmailVerified(true);
		customer.setCreated(new Date());
		context.insert(customer);

		CustomerEntity other = new CustomerEntity();
		other.setName("Alan Turing");
		other.setEmail("alan@example.com");
		other.setAddress("Bletchley Park");
		other.setCountry(CustomerEntity.Country.UK);
		other.setEmailVerified(true);
		other.setCreated(new Date());
		context.insert(other);
	}

	@Test
	public void projectionAnnotationsPresentOnRecordFields() throws Exception {
		assertTrue(CustomerBasicRecord.class.isRecord());

		RecordComponent nameComponent = CustomerBasicRecord.class.getRecordComponents()[1];
		assertNotNull(nameComponent.getAnnotation(Projection.Attribute.class));

		Field nameField = CustomerBasicRecord.class.getDeclaredField("name");
		Projection.Attribute annotation = nameField.getAnnotation(Projection.Attribute.class);
		assertNotNull(annotation);
		assertEquals(CustomerEntity.class, annotation.entity());
		assertEquals(CustomerEntity.Fields.name, annotation.attribute());

		assertTrue(EntityMetaCache.of(CustomerBasicRecord.class).isRecord());
		assertEquals(3, EntityMetaCache.of(CustomerBasicRecord.class).getRecordComponentFields().size());
	}

	@Test
	public void extractListMapsOntoRecordProjection() {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.selectAll(CustomerBasicRecord.class);

		List<CustomerBasicRecord> rows = context.extractList(query, CustomerBasicRecord.class);

		assertEquals(1, rows.size());
		CustomerBasicRecord row = rows.get(0);
		assertEquals(customer.getId(), row.id());
		assertEquals("Ada Lovelace", row.name());
		assertEquals("ada@example.com", row.email());
	}

	@Test
	public void extractListMapsAggregateOntoRecordProjection() {
		SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
			.selectAll(CustomerCountByCountryRecord.class)
			.groupBy(CustomerEntity.class, CustomerEntity.Fields.country);

		List<CustomerCountByCountryRecord> rows = context.extractList(query, CustomerCountByCountryRecord.class);

		assertEquals(1, rows.size());
		assertEquals(CustomerEntity.Country.UK, rows.get(0).country());
		assertEquals(2L, rows.get(0).count());
	}

	@Test
	public void extractListMapsNestedListAndSingleOntoRecord() {
		Estivate.Tools.createTableFullQuery(OrderEntity.class, context).ifNotExists().execute(context);
		Estivate.Tools.createTableFullQuery(OrderLineEntity.class, context).ifNotExists().execute(context);
		Estivate.Tools.createTableFullQuery(ProductEntity.class, context).ifNotExists().execute(context);
		context.truncateTable(OrderLineEntity.class);
		context.truncateTable(OrderEntity.class);
		context.truncateTable(ProductEntity.class);

		ProductEntity beauty = ProductEntity.builder().name("Lipstick").category(ProductCategory.Beauty).price(2.0f).build();
		ProductEntity clothing = ProductEntity.builder().name("Dress").category(ProductCategory.Clothing).price(3.0f).build();
		context.insert(Arrays.asList(beauty, clothing));

		OrderEntity order = OrderEntity.builder()
			.customerId(100L)
			.status(OrderEntity.OrderStatus.COMPLETED)
			.created(new Date())
			.build();
		context.insert(order);
		context.insert(OrderLineEntity.builder().orderId(order.getId()).productId(beauty.getId()).build());
		context.insert(OrderLineEntity.builder().orderId(order.getId()).productId(clothing.getId()).build());

		List<OrderWithNestedRecord> rows = Estivate.selectQuery(OrderEntity.class)
			.joinInner(OrderEntity.class, OrderLineEntity.class)
			.joinInner(OrderLineEntity.class, ProductEntity.class)
			.eq(OrderEntity::getId, order.getId())
			.groupBy(OrderEntity::getCustomerId)
			.groupBy(ProductEntity::getCategory)
			.extractList(context, OrderWithNestedRecord.class);

		assertEquals(2, rows.size());
		for(OrderWithNestedRecord row : rows) {
			assertEquals(100L, row.customerId());
			assertNotNull(row.details());
			assertEquals(OrderEntity.OrderStatus.COMPLETED, row.details().status());
			assertNotNull(row.lines());
			assertEquals(1, row.lines().size());
			assertNotNull(row.lines().get(0).category());
			assertTrue(row.lines().get(0).productCount() > 0);
		}
	}
}
