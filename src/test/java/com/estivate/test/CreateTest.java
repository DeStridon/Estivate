package com.estivate.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

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
		
		System.out.println(insert(EstivateBasic.insert(task)));
		System.out.println(insert(EstivateBasic.insert(task)));
		System.out.println(insert(EstivateBasic.insert(task)));
		
	}
	
	public void execute(String request) {
		System.out.println(request);
		try {
			connection.createStatement().execute(request);
		}
		catch(Exception e) {
			System.err.println("Error with request : "+request);
			e.printStackTrace();
		}
	}
	
	public Long insert(String request) {
		System.out.println(request);
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(request, PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getLong(1);
			}
		}
		catch(Exception e) {
			System.err.println("Error with request : "+request);
			e.printStackTrace();
		}
		return null;
	}
	
	
}
