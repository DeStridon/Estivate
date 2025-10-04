package com.estivate.test.entities;

import javax.persistence.Id;
import javax.persistence.Table;

import com.estivate.Entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@Table(name = "no_use")
@FieldNameConstants
public class NoUseEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = CustomerEntity.class)
	long customerId;
	
	@VirtualForeignKey(entity = OrderEntity.class)
	long orderId;

}
