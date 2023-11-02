package com.estivate.test.entities;

import java.util.Date;

import com.estivate.entity.VirtualForeignKey;
import com.estivate.test.entities.TaskEntity.MacroState;
import com.estivate.test.entities.misc.Language;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class SegmentEntity extends AbstractEntity {
	
	@VirtualForeignKey(entity = ProjectEntity.class)
	long projectId;
	
	@VirtualForeignKey(entity = FragmentEntity.class)
	long sourceFragmentId;
	
	@VirtualForeignKey(entity = FragmentEntity.class)
	long targetFragmentId;
	
	@VirtualForeignKey(entity = TaskEntity.class)
	long taskId;
	
	String sourceContent;
	
	String targetContent;
	
	Language sourceLanguage;
	
	Language targetLanguage;
	
	MacroState macroStatus;
	
	MicroState microStatus;
	
	Date archived;

	public static enum MicroState{
		Waiting,
		InProgress,
		Done,
		OutOfScope,
		Code;
	}
}
