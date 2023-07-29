package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.estivate.ConnectionExecutor;
import com.estivate.EstivateBasic;
import com.estivate.EstivateNameMapper.UppercaseNameMapper;
import com.estivate.EstivateQuery;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;

public class CreateTest {

	Connection connection;
	
	@Before
	public void prepare() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:h2:mem:test");
	}
	
	@Test
	public void globalTest() throws SQLException {
		
		EstivateQuery.nameMapper = new UppercaseNameMapper();
		
		execute(EstivateBasic.create(FragmentEntity.class));
		execute(EstivateBasic.create(TaskEntity.class));
		
		ConnectionExecutor ce = new ConnectionExecutor(connection);
		
		TaskEntity task1 = TaskEntity.builder().projectId(1).name("old name").build();
		ce.saveOrUpdate(task1);
		assertEquals(1L, task1.getId());
		
		EstivateQuery query = new EstivateQuery(TaskEntity.class).eq(TaskEntity.class, TaskEntity.Fields.id, 1);
		TaskEntity task2 = ce.uniqueResult(query, TaskEntity.class);
		
		task1.setName("new name");
		ce.saveOrUpdate(task1);
		
		task2.setProjectId(2);
		ce.saveOrUpdate(task2);
		
		
		TaskEntity task3 = ce.uniqueResult(query, TaskEntity.class);
		assertEquals(2, task3.getProjectId());
		assertEquals("new name", task3.getName());
	}
	
	public void execute(String request) {
		
		try {
			connection.createStatement().execute(request);
		}
		catch(Exception e) {
			System.err.println("Error with request : "+request);
			e.printStackTrace();
		}
	}
	

	
	
	
	
}
