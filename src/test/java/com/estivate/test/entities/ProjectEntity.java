package com.estivate.test.entities;

import javax.persistence.Id;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class ProjectEntity {
	
	@Id
	long id;

}
