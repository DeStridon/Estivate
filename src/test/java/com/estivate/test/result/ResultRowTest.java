package com.estivate.test.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.result.ResultRow;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

/**
 * TDD: {@link ResultRow#as(Attribute)} does not yet honor {@link Attribute#function}.
 * Function cases document the intended contract and currently fail.
 */
public class ResultRowTest {

	Context context = DatabaseGenerator.getContext();

	@Test
	public void as_plainAttribute() {
		CustomerEntity customer = insertCustomer("ResultRow Plain");

		Attribute nameAttr = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);

		ResultRow row = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.select(nameAttr)
			.fetch(context)
			.getFirst();

		assertEquals("ResultRow Plain", row.as(nameAttr));
	}

	@Test
	public void as_attributeWithFunction_whenOnlyFunctionColumn() {
		CustomerEntity customer = insertCustomer("ResultRow Lower");

		Attribute lowerNameAttr = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.lower);

		ResultRow row = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.select(lowerNameAttr)
			.fetch(context)
			.getFirst();

		assertEquals("resultrow lower", row.as(lowerNameAttr));
	}

	@Test
	public void as_attributeWithFunction_whenMultipleColumns() {
		CustomerEntity customer = insertCustomer("ResultRow Mixed");

		Attribute nameAttr = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name);
		Attribute lowerNameAttr = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.lower);

		ResultRow row = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.select(nameAttr)
			.select(lowerNameAttr)
			.fetch(context)
			.getFirst();

		assertEquals("ResultRow Mixed", row.as(nameAttr));
		assertEquals("resultrow mixed", row.as(lowerNameAttr));
	}

	@Test
	public void asString_attributeWithFunction() {
		CustomerEntity customer = insertCustomer("ResultRow Upper");

		Attribute upperNameAttr = Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.upper);

		ResultRow row = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.select(upperNameAttr)
			.fetch(context)
			.getFirst();

		assertEquals("RESULTROW UPPER", row.asString(upperNameAttr));
	}

	@Test
	public void as_attributeWithCountFunction() {
		CustomerEntity customer = insertCustomer("ResultRow Count");

		Attribute countAttr = Estivate.attribute(CustomerEntity.class, AbstractEntity.Fields.id, Estivate.Functions.count);

		ResultRow row = Estivate.selectQuery(CustomerEntity.class)
			.eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
			.select(countAttr)
			.fetch(context)
			.getFirst();

		Object count = row.as(countAttr);
		assertNotNull(count);
		assertEquals(1L, ((Number) count).longValue());
	}


	private CustomerEntity insertCustomer(String name) {
		CustomerEntity customer = CustomerEntity.builder()
			.name(name)
			.email(name.replace(" ", ".").toLowerCase() + "@resultrow.test")
			.country(CustomerEntity.Country.FRANCE)
			.build();
		context.updateOrInsert(customer);
		return customer;
	}

}
