package com.estivate.test.entities;

import javax.persistence.Id;
import javax.persistence.Table;

import com.estivate.Entity.VirtualForeignKey;

import jakarta.persistence.Entity;
import lombok.experimental.FieldNameConstants;

@Entity
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
