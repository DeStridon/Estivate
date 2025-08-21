package com.estivate.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Query;
import com.estivate.test.entities.CustomerEntity;

public class QueryFunctionTest {

    Context context = DatabaseGenerator.getContext();


    @Test
	void lowerTest(){

        Query<CustomerEntity> query = Estivate.query(CustomerEntity.class)
            .eq(Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.lower), "john");

        String queryString = context.queryAsString(query);

        Assert.assertTrue(queryString.contains("WHERE lower("));

    }
}
