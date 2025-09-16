package com.estivate.test.query;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.QueryMapping;
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

        @QueryMapping.EqIfNotNull
        private String name;

        @QueryMapping.GtIfNotNull(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.created)
        private Date createdFrom;

        @QueryMapping.LtIfNotNull(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.created)
        private Date createdTo;

    }

}
