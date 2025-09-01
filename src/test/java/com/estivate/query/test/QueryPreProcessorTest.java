package com.estivate.query.test;

import org.junit.Assert;
import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ParentEntity;


public class QueryPreProcessorTest {
 

    @Test
    public void testPreProcessor() {
    	
    	Context context = DatabaseGenerator.getContext();

        context.fetchQueryPreProcessor = (query) -> {
            if(query instanceof SelectQuery && query.getOrders().isEmpty()) {
                ((SelectQuery) query).orderAsc(AbstractEntity.Fields.id);
            }
        };

        SelectQuery<ParentEntity> query = Estivate.selectQuery(ParentEntity.class);

        String queryString = context.queryAsString(query);

        System.out.println(queryString);
        Assert.assertTrue(queryString.contains("ORDER BY"));
        
        
    }


}