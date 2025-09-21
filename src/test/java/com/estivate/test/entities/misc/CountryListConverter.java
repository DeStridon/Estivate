package com.estivate.test.entities.misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

import com.estivate.test.entities.CustomerEntity.Country;

public class CountryListConverter implements AttributeConverter<List<Country>, String> {

    public List<Country> convertToEntityAttribute(String attribute) {
		
		if ( attribute == null ) {
			return null;
		}

		return Arrays.asList(attribute.split(",")).stream().map(x -> Country.valueOf(x)).collect(Collectors.toList());
        
	}

	public String convertToDatabaseColumn(List<Country> dbData) {
		
		if ( dbData == null ) {
			return null;
		}

		return dbData.stream().map(x -> x.name()).collect(Collectors.joining(","));
		
	}

}
