package com.estivate.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFunction extends Attribute{

	public Function function;

	public AttributeFunctionAlias toAttributeFunctionAlias(String alias) { return Estivate.attributeFunctionAlias(entity, attribute, function, alias); }
	public AttributeFunctionAlias toAttributeFunctionAlias() { return toAttributeFunctionAlias(null); }

	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Function {
		String prefix;
		String suffix;

		public String render(String attribute){
			return prefix + attribute + suffix;
		}

		public boolean equals(Function function){
			if(function == null) return false;
			boolean result = prefix.equals(function.prefix) && suffix.equals(function.suffix);
			return result;
		}

        public static Function compose(List<Function> functions) {
            String prefix = functions.stream().map(x -> x.prefix).collect(Collectors.joining());
            Collections.reverse(functions);
			String suffix =  functions.stream() .map(x -> x.suffix).collect(Collectors.joining());
			return new Function(prefix, suffix);
        }

	}
}
