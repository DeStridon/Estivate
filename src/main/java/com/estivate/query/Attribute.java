package com.estivate.query;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
    public Function function;
    public String alias;


    @Override
    public boolean equals(Object object){
        if(object == null) return false;
        if(!(object instanceof Attribute)) return false;
        Attribute attribute = (Attribute) object;

		if(!Objects.equals(this.entity, attribute.entity)){
			return false;
		}
		if(!Objects.equals(this.attribute, attribute.attribute)){
			return false;
		}
		if(!Objects.equals(this.function, attribute.function)){
			return false;
		}
		
		if(StringUtils.isBlank(this.alias) && StringUtils.isBlank(attribute.alias)){
			return true;
		}
		
		return Objects.equals(this.alias, attribute.alias);


    }

	
	
    @Data
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
