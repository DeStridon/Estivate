package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.estivate.Context;
import com.estivate.NameMapper.TestNameMapper;
import com.estivate.Result;
import com.estivate.query.Join;
import com.estivate.query.PropertyValue;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuerySelectMethodTest {

	
	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void selectMaxTest() {
		
		
		
		
	}

}
