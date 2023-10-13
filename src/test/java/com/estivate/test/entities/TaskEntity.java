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
import com.estivate.test.entities.misc.Language;
import com.estivate.test.entities.misc.LanguageConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class TaskEntity extends AbstractEntity {
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	String name;
	
	@InsertDate
	Date created;
	
	@UpdateDate
	Date updated;
	
	@Enumerated(EnumType.ORDINAL)
	MacroState status;

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
	
	
	public static enum MacroState {
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
}
