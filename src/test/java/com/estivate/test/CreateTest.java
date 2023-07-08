package com.estivate.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

import com.estivate.EstivateBasic;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.TaskEntity;

public class CreateTest {

	@Test
	public void globalTest() throws SQLException {
		
		System.out.println(EstivateBasic.create(FragmentEntity.class));
		
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");

		
		connection.createStatement().execute(EstivateBasic.create(FragmentEntity.class));
		connection.createStatement().execute(EstivateBasic.create(TaskEntity.class));
		
		
		TaskEntity task = TaskEntity.builder().projectId(1).build();
		
		System.out.println(EstivateBasic.insert(task));
		
		connection.createStatement().execute(EstivateBasic.insert(task));
		
		
	}
	
	
}
