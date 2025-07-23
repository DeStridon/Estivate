package com.estivate.test.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.estivate.util.CachedEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants
public class AbstractEntity extends CachedEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;

}
