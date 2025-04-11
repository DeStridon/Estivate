package com.estivate.query;

import java.util.List;

import com.estivate.query.Query.Entity;

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
	}
}
