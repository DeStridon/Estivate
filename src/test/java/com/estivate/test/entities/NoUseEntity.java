package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class NoUseEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	@VirtualForeignKey(entity = TaskEntity.class)
	long taskId;

}
