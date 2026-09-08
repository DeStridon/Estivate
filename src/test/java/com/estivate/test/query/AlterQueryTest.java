package com.estivate.test.query;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.AlterQuery;
import com.estivate.reconciliation.ProjectedColumn;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;


public class AlterQueryTest {

    Context context = DatabaseGenerator.getContext();

    
    @Test
    public void testAlterQuery() {
        AlterQuery<CustomerEntity> query = Estivate.alterQuery(CustomerEntity.class)
            .modifyColumn("name", ProjectedColumn.builder().name("name").javaType(String.class).type("VARCHAR").dimension(100).defaultValue("").build());
        System.out.println(context.queryAsString(query));
    }

}
