package com.estivate.test.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.estivate.entity.VirtualForeignKey;

import lombok.experimental.FieldNameConstants;

@Entity
@FieldNameConstants
@Table(indexes = {
	@Index(columnList = "projectId"),
	@Index(columnList = "projectId, externalName")
})
public class FragmentEntity {
	
	@Id
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	
	String externalName;
	
	String externalReference;
	
}
