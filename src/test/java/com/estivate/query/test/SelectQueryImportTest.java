package com.estivate.query.test;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.QueryBuilder;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SelectQueryImportTest {

    Context context = DatabaseGenerator.getContext();


    @Test
    void importTest(){

        CustomerFilter filter = CustomerFilter.builder()
            .name("john")
            .createdFrom(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
            .createdTo(new Date(System.currentTimeMillis()))
            .build();



        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .importCriterion(filter);

        System.out.println(context.queryAsString(query));
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerFilter{

        @QueryBuilder.EqIfNotNull
        private String name;

        @QueryBuilder.GtIfNotNull(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.created)
        private Date createdFrom;

        @QueryBuilder.LtIfNotNull(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.created)
        private Date createdTo;

    }

}
