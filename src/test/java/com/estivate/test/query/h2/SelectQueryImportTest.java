package com.estivate.test.query.h2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.QueryMapping;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
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
            .importCriterionFromQueryMapping(filter);

        System.out.println(context.queryAsString(query));
        
    }

    @Test
    void importOrFalseIfNullTest() {

        OrFalseIfNullFilter present = OrFalseIfNullFilter.builder()
            .id(10L)
            .createdFrom(new Date(1))
            .build();

        String presentQuery = context.queryAsString(Estivate.selectQuery(CustomerEntity.class)
            .importCriterionFromQueryMapping(present));

        assertTrue(presentQuery.contains("ID = ?"));
        assertTrue(presentQuery.contains("CREATED >= ?"));

        OrFalseIfNullFilter absent = OrFalseIfNullFilter.builder().build();

        String absentQuery = context.queryAsString(Estivate.selectQuery(CustomerEntity.class)
            .importCriterionFromQueryMapping(absent));

        assertTrue(absentQuery.contains("false"));
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrFalseIfNullFilter {

        @QueryMapping.EqOrFalseIfNull(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
        private Long id;

        @QueryMapping.GteOrFalseIfNull(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.created)
        private Date createdFrom;

    }

}
