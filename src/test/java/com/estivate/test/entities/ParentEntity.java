package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import com.estivate.Entity.InsertDate;
import com.estivate.Entity.UpdateDate;
import com.estivate.Entity.VirtualForeignKey;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
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

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@TableIndexes({
	@TableIndex(name="created", columns= {@IndexColumn(ParentEntity.Fields.created)}),
	@TableIndex(name="updated", columns= {@IndexColumn(ParentEntity.Fields.updated)}),
	@TableIndex(name="homeNameUnicity", columns= {@IndexColumn(ParentEntity.Fields.homeId), @IndexColumn(ParentEntity.Fields.name)}, type=IndexType.UNIQUE)
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
