package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.Enumerated;

import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.entity.VirtualForeignKey;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.Annotations.TableIndexes;
import com.estivate.test.entities.ParentEntity.JobEnum;

import com.estivate.index.Annotations.IndexType;


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
	@TableIndex(name="born", columns= {@IndexColumn(ChildEntity.Fields.born)}),
	@TableIndex(name="lastseen", columns= {@IndexColumn(ChildEntity.Fields.lastSeen)}),
	@TableIndex(name="bornUnique", type=IndexType.UNIQUE, columns= {@IndexColumn(ChildEntity.Fields.parentId), @IndexColumn(ChildEntity.Fields.description)})
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
