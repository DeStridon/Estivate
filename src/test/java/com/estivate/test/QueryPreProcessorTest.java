package com.estivate.test;

import org.junit.Assert;
import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;


public class QueryPreProcessorTest {
 

    @Test
    public void testPreProcessor() {
    	
    	Context context = DatabaseGenerator.getContext();

        context.fetchQueryPreProcessor = (query) -> {
            if(query.getOrders().isEmpty()) {
                query.orderAsc(AbstractEntity.Fields.id);
            }
        };

        Query<ParentEntity> query = Estivate.query(ParentEntity.class);

        String queryString = context.queryAsString(query);

        System.out.println(queryString);
        Assert.assertTrue(queryString.contains("ORDER BY"));
        
        
    }


}