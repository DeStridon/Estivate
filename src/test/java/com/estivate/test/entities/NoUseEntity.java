package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.Entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class NoUseEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = CustomerEntity.class)
	long customerId;
	
	@VirtualForeignKey(entity = OrderEntity.class)
	long orderId;

}
