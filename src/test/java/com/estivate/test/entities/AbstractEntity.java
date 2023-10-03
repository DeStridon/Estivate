package com.estivate.test.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.estivate.entity.CachedEntity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class AbstractEntity extends CachedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;

}
