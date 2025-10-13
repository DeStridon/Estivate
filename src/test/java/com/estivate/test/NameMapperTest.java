package com.estivate.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.test.entities.NoUseEntity;

public class NameMapperTest {

    Context context = DatabaseGenerator.getContext();
	
    @Test
    public void test() {
        Assert.assertEquals("no_use", context.nameMapper.toTableName(NoUseEntity.class));
    }

    
}
