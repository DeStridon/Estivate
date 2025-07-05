package com.estivate.test;


import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;

import com.estivate.manager.ManagerInterceptor.EntityManager;

import com.estivate.test.entities.ParentEntity;

public class ManagerImplementationTest {

    Context context = DatabaseGenerator.getContext();
    
	

    @Test
    public void testOneLiner() {

    	ParentManager parentManager = Estivate.implementManager(ParentManager.class, context);
        ParentEntity parent1 = parentManager.findById(1L);
        List<ParentEntity> parents1 = parentManager.findByIdInIfNotEmpty(Arrays.asList(1L, 2L, 3L));
        List<ParentEntity> parents2 = parentManager.findByHomeIdAndName(1L, "John");
        List<ParentEntity> parents3 = parentManager.findByHomeIdBetween(1L, 2L);

        parentManager.findByHomeIdBetween(1L, 2L);

        
    }
    
    public static abstract class ParentManager extends EntityManager<ParentEntity>{
    	
        public abstract ParentEntity findById(long id);

        public abstract List<ParentEntity> findByIdInIfNotEmpty(List<Long> ids);

        public abstract List<ParentEntity> findByHomeIdAndName(long homeId, String name);

        public abstract List<ParentEntity> findByHomeIdAndNameAndAge(long homeId, String name, int age);

        public abstract List<ParentEntity> findByHomeIdBetween(long homeId1, long homeId2);
        
    }


}
