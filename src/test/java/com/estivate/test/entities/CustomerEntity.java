package com.estivate.test.entities;

import java.util.Date;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class CustomerEntity {

    long id;

    String name;

    String email;

    String address;

    Country country;

    Date created;
    
    enum Country{
    	GERMANY,
    	SPAIN,
    	FRANCE,
    	UNITED_STATES,
    }

}
