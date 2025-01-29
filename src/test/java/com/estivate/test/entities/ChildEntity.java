package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.Enumerated;

import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.entity.VirtualForeignKey;
import com.estivate.index.Annotations.ColumnIndex;
import com.estivate.index.Annotations.CompositeIndex;
import com.estivate.index.Annotations.TableIndexes;
import com.estivate.test.entities.ParentEntity.JobEnum;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@TableIndexes({
	@CompositeIndex(name="created", columns= {@ColumnIndex(ChildEntity.Fields.born)}),
	@CompositeIndex(name="updated", columns= {@ColumnIndex(ChildEntity.Fields.lastSeen)})
})
public class ChildEntity extends AbstractEntity {
	
	@VirtualForeignKey(entity = HomeEntity.class)
	long homeId;
	
	@VirtualForeignKey(entity = ParentEntity.class)
	long parentId;
	
	@InsertDate
	Date born;
	
	@Enumerated
	JobEnum job;
	
	@Enumerated
	MoodEnum mood;
	
	int age;
	
	@UpdateDate
	Date lastSeen;

	String description;
	

	public static enum MoodEnum{
		Wonderful,
		Great,
		Neutral,
		Bad,
		Awful;
	}
}
