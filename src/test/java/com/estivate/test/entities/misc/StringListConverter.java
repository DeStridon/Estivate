package com.estivate.test.entities.misc;

import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    public List<String> convertToEntityAttribute(String attribute) {
		
		if ( attribute == null ) {
			return null;
		}

		return Arrays.asList(attribute.split(","));
        
	}

	public String convertToDatabaseColumn(List<String> dbData) {
		
		if ( dbData == null ) {
			return null;
		}

		return String.join(",", dbData);
		
	}

}
