//package com.estivate.test_old;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.estivate.Context;
//import com.estivate.NameMapper;
//import com.estivate.query.Query;
//import com.estivate.test.entities.FragmentEntity;
//import com.estivate.test.entities.SegmentEntity;
//import com.estivate.test.entities.TaskEntity;
//import com.estivate.test.entities.TaskHistoryEntity;
//import com.estivate.test.entities.misc.Language;
//
//public class SimpleQueryTest {
//	
//	Context connection;
//	
//	@Test
//	public void prepare() throws SQLException {
//		this.connection = new Context(DriverManager.getConnection("jdbc:h2:mem:test"));
//		
//		Query.nameMapper = new NameMapper.UppercaseNameMapper();
//
//		connection.create(TaskEntity.class);
//		connection.create(SegmentEntity.class);
//		connection.create(FragmentEntity.class);
//		connection.create(TaskHistoryEntity.class);
//		
//		TaskEntity task1 = connection.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #1").sourceLanguage(Language.fr_FR).targetLanguage(Language.en_GB).build());
//		Assert.assertEquals(1, task1.getId());
//		TaskEntity task2 = connection.saveOrUpdate(TaskEntity.builder().projectId(1).name("task #2").sourceLanguage(Language.en_GB).targetLanguage(Language.ja_JP).build());
//		Assert.assertEquals(2, task2.getId());
//		TaskEntity task3 = connection.saveOrUpdate(TaskEntity.builder().projectId(2).name("task #2.3").sourceLanguage(Language.en_GB).targetLanguage(Language.it_IT).build());
//		Assert.assertEquals(3, task3.getId());
//
//		taskByLanguage();
//		taskGroupedByProject();
//	}
//	
//	
//	public void taskByLanguage() {
//		Query query = new Query(TaskEntity.class)
//				.eq(TaskEntity.class, TaskEntity.Fields.targetLanguage, Language.ja_JP);
//		
//		TaskEntity task = connection.uniqueResult(query, TaskEntity.class);
//		
//		Assert.assertEquals("task #2", task.getName());
//		
//	}
//	
//	
//	public void taskGroupedByProject() {
//		Query query = new Query(TaskEntity.class)
//				.groupBy(TaskEntity.class, TaskEntity.Fields.projectId)
//				.groupBy(TaskEntity.class, TaskEntity.Fields.sourceLanguage);
//		
//		List<TaskEntity> tasks = connection.listAs(query, TaskEntity.class);
//		
//		Assert.assertEquals(2, tasks.size());
//		Assert.assertEquals(1, tasks.get(0).getId());
//		Assert.assertEquals(2, tasks.get(1).getId());
//		
//	}
//	
//	
//
//}
