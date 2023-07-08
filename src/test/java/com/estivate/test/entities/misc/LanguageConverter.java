package com.estivate.test.entities.misc;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LanguageConverter implements AttributeConverter<Language, String> {

	public Language convertToEntityAttribute(String attribute) {
		if ( attribute == null ) {
			return null;
		}

		return Language.forValue(attribute);
	}

	public String convertToDatabaseColumn(Language dbData) {
		
		if ( dbData == null ) {
			return null;
		}

		return dbData.code;
		
	}

	
	
}