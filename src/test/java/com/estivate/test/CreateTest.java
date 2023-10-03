package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.estivate.Context;
import com.estivate.NameMapper.UppercaseNameMapper;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.TaskEntity;

public class CreateTest {

	Connection connection;
	
	@Before
	public void prepare() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:h2:mem:test");
	}
	
	@Test
	public void globalTest() throws SQLException {
		
		Query.nameMapper = new UppercaseNameMapper();
		Context ce = new Context(connection);
		
		System.out.println(ce.create(FragmentEntity.class));
		System.out.println(ce.create(TaskEntity.class));
		
		
		TaskEntity task1 = TaskEntity.builder().projectId(1).name("old name").build();
		ce.saveOrUpdate(task1);
		assertEquals(1L, task1.getId());
		
		Query query = new Query(TaskEntity.class).eq(TaskEntity.class, AbstractEntity.Fields.id, 1);
		TaskEntity task2 = ce.uniqueResult(query, TaskEntity.class);
		
		task1.setName("new name");
		ce.saveOrUpdate(task1);
		
		task2.setProjectId(2);
		ce.saveOrUpdate(task2);
		
		
		TaskEntity task3 = ce.uniqueResult(query, TaskEntity.class);
		assertEquals(2, task3.getProjectId());
		assertEquals("new name", task3.getName());
	}
	
	
	

	
	
	
	
}
