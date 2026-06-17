package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.estivate.Statement;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.result.ResultRow;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;
import com.estivate.util.Chronometer;

public class ResultTest {
	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void testParallel() {
		
		
		CustomerEntity customer = CustomerEntity.builder()
				.id(10)
				.name("parallel test customer")
				.address("Test Address")
				.country(CustomerEntity.Country.FRANCE)
				.created(new Date())
				.build();
		
		for(int i = 0; i < 5000; i++) {
			customer.setEmail("test"+i+"@test.com");
			customer.setName(customer.getName() + " - " + i);
			context.updateOrInsert(customer);
		}
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class);
		query.eq(CustomerEntity.class, CustomerEntity.Fields.name, "parallel test customer");
		
		List<CustomerEntity> customers = context.fetchList(query);
		
		
	}
	
	@Test
	public void testMapEnum() {
		
		ProductEntity product = ProductEntity.builder()
				.name("Test Product")
				.price(99.99f)
				.stock(100)
				.category(ProductCategory.Electronics)
				.build();
		
		context.updateOrInsert(product);
		
		SelectQuery<ProductEntity> query = new SelectQuery<>(ProductEntity.class)
			.eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());

		ResultRow result = query.fetch(context).getFirst();
		
		ProductCategory category = (ProductCategory) result.asEnum(ProductEntity.class, ProductEntity.Fields.category);
		assertEquals(ProductCategory.Electronics, category);
		
		Float price = result.asFloat(ProductEntity.class, ProductEntity.Fields.price);
		assertEquals(Float.valueOf(99.99f), price);
		
		
		
	}

	@Test
	public void testMap() throws NoSuchFieldException, SecurityException {
		
		CustomerEntity customer1 = CustomerEntity.builder()
				.id(22)
				.name("map test customer 1")
				.email("customer1@test.com")
				.address("Address 1")
				.country(CustomerEntity.Country.GERMANY)
				.created(new Date())
				.build();
		
		context.insert(customer1);
		
		CustomerEntity customer2 = CustomerEntity.builder()
				.id(23)
				.name("map test customer 2")
				.email("customer2@test.com")
				.address("Address 2")
				.country(CustomerEntity.Country.SPAIN)
				.created(new Date())
				.build();
		
		context.insert(customer2);
		
		Field myField = CustomerEntity.class.getDeclaredField(CustomerEntity.Fields.country);
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class)
				.likeStartsWith(CustomerEntity.Fields.name, "map test customer");
		
		Map<String, CustomerEntity.Country> map = context.aggregateToMap(query, x -> x.asString(CustomerEntity.class, CustomerEntity.Fields.name), x -> (CustomerEntity.Country) x.asEnum(CustomerEntity.class, CustomerEntity.Fields.country));
		
		assertEquals(CustomerEntity.Country.GERMANY, map.get("map test customer 1"));
		assertEquals(CustomerEntity.Country.SPAIN, map.get("map test customer 2"));
		
		
	}
	
	
	
	
	
	

}
