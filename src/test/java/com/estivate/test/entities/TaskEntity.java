package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class TaskEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;

}
