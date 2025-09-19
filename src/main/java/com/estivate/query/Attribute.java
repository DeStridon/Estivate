package com.estivate.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        public static Function compose(List<Function> functions) {
            String before = functions.stream().map(x -> x.before).collect(Collectors.joining());
            Collections.reverse(functions);
			String after =  functions.stream() .map(x -> x.after).collect(Collectors.joining());
			return new Function(before, after);
        }

	}
}
