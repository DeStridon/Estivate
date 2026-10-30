package com.estivate.test;

import com.estivate.test.query.h2.H2TestDatabase;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.test.entities.NoUseEntity;

public class NameMapperTest {

    Context context = H2TestDatabase.getContext();
	
    @Test
    public void test() {
        Assert.assertEquals("no_use", context.nameMapper.toTableName(NoUseEntity.class));
    }

    
}
