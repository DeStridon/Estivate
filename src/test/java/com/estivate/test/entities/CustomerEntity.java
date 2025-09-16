package com.estivate.test.entities;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class CustomerEntity extends AbstractEntity{

    String name;

    String email;

    String address;

    Country country;

    boolean emailVerified;

    Date created;

    Date updated;

    Date archived;
    
    public enum Country{
    	GERMANY,
    	SPAIN,
    	FRANCE,
    	USA,
    	UK,
    	AUSTRALIA,
    	NEW_ZEALAND,
    	SOUTH_AFRICA,
    	INDIA,
    	CHINA,
    	JAPAN,
    	KOREA,
    	INDONESIA,
    	PHILIPPINES,
    	THAILAND,
    	VIETNAM,
    }

}
