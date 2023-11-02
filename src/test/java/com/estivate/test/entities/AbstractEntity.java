package com.estivate.test.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.estivate.entity.CachedEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class AbstractEntity extends CachedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;

}
