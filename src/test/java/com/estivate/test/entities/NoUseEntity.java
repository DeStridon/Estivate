package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.Entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class NoUseEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = HomeEntity.class)
	long projectId;
	
	@VirtualForeignKey(entity = ParentEntity.class)
	long taskId;

}
