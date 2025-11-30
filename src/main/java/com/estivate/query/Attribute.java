package com.estivate.query;

import com.estivate.Entity;
import com.estivate.Estivate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attribute  {

    public Entity<?> entity;
	public String attribute;

    public AttributeFunction toAttributeFunction(AttributeFunction.Function function) { return Estivate.attributeFunction(entity, attribute, function); }
    public AttributeFunction toAttributeFunction() { return toAttributeFunction(null); }

    public AttributeFunctionAlias toAttributeFunctionAlias(AttributeFunction.Function function, String alias) { return Estivate.attributeFunctionAlias(entity, attribute, function, alias); }
    public AttributeFunctionAlias toAttributeFunctionAlias(String alias) { return toAttributeFunctionAlias(null, alias); }
    public AttributeFunctionAlias toAttributeFunctionAlias() { return toAttributeFunctionAlias(null, null); }


	
}
