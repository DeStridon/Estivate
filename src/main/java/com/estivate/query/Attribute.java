package com.estivate.query;

import com.estivate.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    public Entity<?> entity;
	public String attribute;
	public Function function;

	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Function {
		String before;
		String after;

		public String render(String attribute){
			return before + attribute + after;
		}

		public boolean equals(Function function){
			if(function == null) return false;
			boolean result = before.equals(function.before) && after.equals(function.after);
			return result;
		}
	}
}
