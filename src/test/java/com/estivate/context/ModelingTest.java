package com.estivate.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.index.Annotations.ColumnDefaultValue;
import com.estivate.reconciliation.DatabaseColumn;
import com.estivate.test.DatabaseGenerator;
import com.estivate.util.FieldUtils;

import jakarta.persistence.Column;

public class ModelingTest {

	Context context = DatabaseGenerator.getContext();
	
	public static class SingleIntegerFieldClass{
		@ColumnDefaultValue("0")
		int singleField;
	}
	
	@Test
	public void modelingTest1() {
		Set<Field> fields = FieldUtils.getEntityFields(SingleIntegerFieldClass.class);
		DatabaseColumn tableField = context.databaseColumn(fields.iterator().next());
		
		assertEquals("0", tableField.getDefaultValue());
		
	}
	
	
	public static class LongTextFieldClass{
		@Column(columnDefinition = "LONGTEXT")
		String longtext;
	}
	
	
	@Test
	public void modelingTest2() {
		Set<Field> fields = FieldUtils.getEntityFields(LongTextFieldClass.class);
		DatabaseColumn tableField = context.databaseColumn(fields.iterator().next());
		
		assertEquals(null, tableField.getDimension());
		
	}
	
}
