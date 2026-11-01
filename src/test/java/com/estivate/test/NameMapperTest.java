package com.estivate.test;

import com.estivate.test.query.h2.H2TestDatabase;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.NoUseEntity;
import com.estivate.util.EntityMetaCache;

public class NameMapperTest {

    Context context = H2TestDatabase.getContext();
	
    @Test
    public void annotatedTableNameFromEntityMetaCache() {
        Assert.assertEquals("no_use", EntityMetaCache.of(NoUseEntity.class).getAnnotatedTableName());
        Assert.assertEquals("no_use", context.nameMapper.toTableName(NoUseEntity.class));
        Assert.assertEquals("no_use.ID", context.nameMapper.toTableNameAttribute(NoUseEntity.class, AbstractEntity.Fields.id));
    }

    @Test
    public void selectFromUsesAnnotatedTableName() {
        String sql = context.queryAsString(Estivate.selectQuery(NoUseEntity.class).select(NoUseEntity.Fields.id));
        Assert.assertTrue(sql, sql.contains("FROM no_use"));
        Assert.assertFalse(sql, sql.contains("FROM NOUSEENTITY") || sql.contains("FROM NoUseEntity"));
    }

    
}
