package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.VirtualForeignKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@FieldNameConstants
public class TaskEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;

}
