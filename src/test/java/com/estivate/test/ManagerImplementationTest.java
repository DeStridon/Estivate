package com.estivate.test;


import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;

import com.estivate.manager.ManagerInterceptor.EntityManager;

import com.estivate.test.entities.CustomerEntity;

public class ManagerImplementationTest {

    Context context = DatabaseGenerator.getContext();
    
	

    @Test
    public void testOneLiner() {

    	CustomerManager customerManager = Estivate.implementManager(CustomerManager.class, context);
        CustomerEntity customer1 = customerManager.findById(1L);
        List<CustomerEntity> customers1 = customerManager.findByIdInIfNotEmpty(Arrays.asList(1L, 2L, 3L));
        List<CustomerEntity> customers2 = customerManager.findByNameAndEmail("John", "john@example.com");
        List<CustomerEntity> customers3 = customerManager.findByIdBetween(1L, 2L);

        customerManager.findByIdBetween(1L, 2L);

        
    }
    
    public static abstract class CustomerManager extends EntityManager<CustomerEntity>{
    	
        public abstract CustomerEntity findById(long id);

        public abstract List<CustomerEntity> findByIdInIfNotEmpty(List<Long> ids);

        public abstract List<CustomerEntity> findByNameAndEmail(String name, String email);

        public abstract List<CustomerEntity> findByNameAndEmailAndAddress(String name, String email, String address);

        public abstract List<CustomerEntity> findByIdBetween(long id1, long id2);
        
        public abstract List<CustomerEntity> findByCountryOrderByCreatedDesc(CustomerEntity.Country country);
        
    }


}
