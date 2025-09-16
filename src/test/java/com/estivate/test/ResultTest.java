package com.estivate.test;

import static org.junit.Assert.assertEquals;

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
import com.estivate.result.Result;
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
				.email("test@example.com")
				.address("Test Address")
				.country(CustomerEntity.Country.FRANCE)
				.created(new Date())
				.build();
		
		for(int i = 0; i < 5000; i++) {
			customer.setName(customer.getName() + " - " + i);
			customer.setId(0);
			context.updateOrInsert(customer);
		}
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class);
		query.eq(CustomerEntity.class, CustomerEntity.Fields.name, "parallel test customer");
		
		List<CustomerEntity> customers = context.fetchList(query);
		
		
	}
	
	@Test
	public void testMapEnum() {
		
		ProductEntity product = ProductEntity.builder()
				.id(10)
				.name("Test Product")
				.price(99.99f)
				.available(100)
				.category(ProductCategory.Computer)
				.build();
		
		context.updateOrInsert(product);
		
		SelectQuery<ProductEntity> query = new SelectQuery<>(ProductEntity.class);
		query.eq(ProductEntity.class, AbstractEntity.Fields.id, product.getId());
		
		Result results = context.fetchListAsResults(query).get(0);
		
		ProductCategory category = (ProductCategory) results.attributeAsEnum(ProductEntity.class, ProductEntity.Fields.category);
		assertEquals(ProductCategory.Computer, category);
		
		Float price = results.attributeAsFloat(ProductEntity.class, ProductEntity.Fields.price);
		assertEquals(Float.valueOf(99.99f), price);
		
		
		
	}

	@Test
	public void testMap() {
		
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
		
		
		
		SelectQuery<CustomerEntity> query = new SelectQuery<>(CustomerEntity.class)
				.likeStartsWith(CustomerEntity.Fields.name, "map test customer");
		
		Map<String, CustomerEntity.Country> map = context.aggregateToMap(query, x -> x.attributeAsString(CustomerEntity.class, CustomerEntity.Fields.name), x -> (CustomerEntity.Country) x.attributeAsEnum(CustomerEntity.class, CustomerEntity.Fields.country));
		
		assertEquals(CustomerEntity.Country.GERMANY, map.get("map test customer 1"));
		assertEquals(CustomerEntity.Country.SPAIN, map.get("map test customer 2"));
		
		
	}
	
	
	
	
	
	

}
