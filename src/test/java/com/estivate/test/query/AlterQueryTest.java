package com.estivate.test.query;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.AlterQuery;
import com.estivate.reconciliation.ColumnModel.EntityColumn;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;


public class AlterQueryTest {

    Context context = DatabaseGenerator.getContext();

    
    @Test
    public void testAlterQuery() {
        AlterQuery<CustomerEntity> query = Estivate.alterQuery(CustomerEntity.class)
            .modifyColumn("name", EntityColumn.builder().name("name").type(String.class).designedLength(100).defaultValue("").build());
        System.out.println(context.queryAsString(query));
    }

}
