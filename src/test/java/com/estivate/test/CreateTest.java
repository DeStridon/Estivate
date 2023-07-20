package com.estivate.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.estivate.ConnectionExecutor;
import com.estivate.EstivateBasic;
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
		
		execute(EstivateBasic.create(FragmentEntity.class));
		execute(EstivateBasic.create(TaskEntity.class));
		
		
		TaskEntity task = TaskEntity.builder().projectId(1).build();
		ConnectionExecutor ce = new ConnectionExecutor(connection);
		
		ce.insert(task);
		assertEquals(1L, task.getId());
		
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
