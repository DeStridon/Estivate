package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.entity.VirtualForeignKey;
import com.estivate.index.Annotations.ColumnIndex;
import com.estivate.index.Annotations.CompositeIndex;
import com.estivate.index.Annotations.TableIndexes;
import com.estivate.test.entities.misc.Language;
import com.estivate.test.entities.misc.LanguageConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import com.estivate.index.Annotations.Type;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@TableIndexes({
	@CompositeIndex(name="created", columns= {@ColumnIndex(ParentEntity.Fields.created)}),
	@CompositeIndex(name="updated", columns= {@ColumnIndex(ParentEntity.Fields.updated)}),
	@CompositeIndex(name="homeNameUnicity", columns= {@ColumnIndex(ParentEntity.Fields.homeId), @ColumnIndex(ParentEntity.Fields.name)}, type=Type.UNIQUE)
})
public class ParentEntity extends AbstractEntity {
	
	@VirtualForeignKey(entity = HomeEntity.class)
	long homeId;
	
	String name;
	
	@InsertDate
	Date created;
	
	@UpdateDate
	Date updated;
	
	@Enumerated(EnumType.ORDINAL)
	JobEnum status;
	
	@Enumerated(EnumType.STRING)
	StringEnum stringEnum;

	@Convert(converter=LanguageConverter.class)
	Language sourceLanguage;

	@Convert(converter=LanguageConverter.class)
	Language targetLanguage;
	
	double matchingPoint;
	
	boolean archived;
	
	String externalName;
	
	@PrePersist
	public void prePersist() {
		System.out.println("prepersist");
	}
	
	@PostPersist
	public void postPersist() {
		System.out.println("postPersist");
	}
	
	
	public static enum JobEnum {
		Analysis, 		//0
		Preproc, 		//1
		Translation, 	//2
		Review, 		//3
		SEO,			//4
		Correction, 	//5 (4)
		Validation, 	//6 (5)
		Approval, 		//7 (6)
		Legal,			//8 
		Final,			//9
		PostEdition, 	//10 (7)
		Delivered; 		//11 (8)

		
	}
	
	public static enum StringEnum{
		ABC,
		DEF,
		GHI
	}
}
