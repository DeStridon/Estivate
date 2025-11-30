package com.estivate.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFunctionAlias extends AttributeFunction{

	public String alias;

	public AttributeFunctionAlias toAttributeFunctionAlias() { return this; }

}
