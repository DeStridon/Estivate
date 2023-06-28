package com.estivate.test.entities;

import javax.persistence.Id;

import com.estivate.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class SegmentEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectNodeId;
	
	@VirtualForeignKey(entity = FragmentEntity.class)
	long sourceFragmentId;
	
	@VirtualForeignKey(entity = FragmentEntity.class)
	long targetFragmentId;
	
	@VirtualForeignKey(entity = TaskEntity.class)
	long taskId;
	
	

}
