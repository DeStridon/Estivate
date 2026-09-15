package com.estivate.test.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.query.Attribute;
import com.estivate.test.entities.ActiveBooleanEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.IsActiveBooleanEntity;

/**
 * Lombok generates {@code isActive()} for a boolean field named {@code isActive}.
 * {@link com.estivate.util.FieldUtils} strips a leading {@code is} prefix from the getter,
 * so {@code Entity::isActive} is resolved as attribute {@code "active"} instead of {@code "isActive"}.
 */
public class AttributeGetterIsActiveFieldTest {

	@Test
	public void attributeGetter_forBooleanFieldNamedIsActive_keepsIsActivePropertyName() {
		assertEquals("isActive", IsActiveBooleanEntity.Fields.isActive);

		Attribute attribute = Estivate.attribute(IsActiveBooleanEntity::isActive);

		assertEquals(
				"isActive",
				attribute.getAttribute(),
				"AttributeGetter for Lombok isActive() on field isActive must resolve to field name isActive, not active");
	}

	@Test
	public void attributeGetter_forBooleanFieldNamedActive_mapsToActive() {
		assertEquals("active", ActiveBooleanEntity.Fields.active);

		Attribute attribute = Estivate.attribute(ActiveBooleanEntity::isActive);

		assertEquals("active", attribute.getAttribute());
	}

	@Test
	public void attributeGetter_forBooleanFieldWithoutIsPrefix_stripsIsFromGetter() {
		Attribute attribute = Estivate.attribute(CustomerEntity::isEmailVerified);

		assertEquals("emailVerified", attribute.getAttribute());
	}

	@Test
	public void attributeFromString_usesLiteralFieldNameIsActive() {
		Attribute attribute = Estivate.attribute(IsActiveBooleanEntity.class, IsActiveBooleanEntity.Fields.isActive);

		assertEquals("isActive", attribute.getAttribute());
	}
}
