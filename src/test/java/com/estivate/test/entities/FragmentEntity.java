package com.estivate.test.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.estivate.entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@Entity
@FieldNameConstants
public class FragmentEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	
	String externalName;
	
	String externalReference;
	
}
