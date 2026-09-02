package com.estivate.query;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.util.FieldUtils.AttributeGetter;

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
	public static class Function {
		final String prefix;
		final String suffix;

		final boolean emptyParameter;
		final Class<?> returnType;

		public String render(String attribute){
			return prefix + (emptyParameter ? "" : attribute) + suffix;
		}

		public boolean equals(Function function){
			if(function == null) return false;
			boolean result = prefix.equals(function.prefix) && suffix.equals(function.suffix);
			return result;
		}

		public static Function reduce(Function sup, Function sub){
			if(sup.emptyParameter){
				return sup;
			}
			
			return new Function(sup.prefix + sub.prefix, sub.suffix + sup.suffix, sub.emptyParameter);
		}

		public Function(String prefix, String suffix, boolean emptyParameter, Class<?> returnType){
			this.prefix = prefix;
			this.suffix = suffix;
			this.emptyParameter = emptyParameter;
			this.returnType = returnType;
		}

		public Function(String prefix, String suffix, boolean emptyParameter){
			this(prefix, suffix, emptyParameter, null);
		}

		public Function(String prefix, String suffix){
			this(prefix, suffix, false);
		}

        public static Function compose(List<Function> functions) {
            return functions.stream().reduce((sup, sub) -> Function.reduce(sup, sub)).orElse(null);
        }

	}

	public static class AttributeWindow extends Attribute {

		public AttributeWindow(Entity<?> entity, String attribute, Function function, String alias) {
			super(entity, attribute, function, alias);
		}

		public Attribute partitionBy;
		public Attribute orderBy;

		public AttributeWindow partitionBy(Attribute attribute) { this.partitionBy = attribute; return this; }
		public AttributeWindow partitionBy(Entity<?> entity, String attribute) { return partitionBy(Estivate.attribute(entity, attribute)); }
		public AttributeWindow partitionBy(Class<?> entity, String attribute) { return partitionBy(Estivate.attribute(new Entity<>(entity), attribute)); }
		public <E, P> AttributeWindow partitionBy(AttributeGetter<E, P> getter) { return partitionBy(Estivate.attribute(getter)); }

		public AttributeWindow orderBy(Attribute attribute) { this.orderBy = 	attribute; return this; }
		public AttributeWindow orderBy(Entity<?> entity, String attribute) { return orderBy(Estivate.attribute(entity, attribute)); }
		public AttributeWindow orderBy(Class<?> entity, String attribute) { return orderBy(Estivate.attribute(new Entity<>(entity), attribute)); }
		public <E, P> AttributeWindow orderBy(AttributeGetter<E, P> getter) { return orderBy(Estivate.attribute(getter)); }
		
	}
    
}
