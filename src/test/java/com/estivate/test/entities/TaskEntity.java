package com.estivate.test.entities;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.estivate.VirtualForeignKey;
import com.estivate.test.entities.misc.Language;
import com.estivate.test.entities.misc.LanguageConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@FieldNameConstants
public class TaskEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	long id;
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	@Enumerated(EnumType.ORDINAL)
	MacroState status;

	@Convert(converter=LanguageConverter.class)
	Language sourceLanguage;

	@Convert(converter=LanguageConverter.class)
	Language targetLanguage;
	
	
	
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
