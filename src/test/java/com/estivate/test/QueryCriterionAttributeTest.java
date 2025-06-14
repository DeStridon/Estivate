package com.estivate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.Result;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Query;
import com.estivate.query.Query.Entity;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;
import com.estivate.test.entities.ParentEntity.JobEnum;
import com.estivate.test.entities.misc.Language;

public class QueryCriterionAttributeTest {

	Context context = DatabaseGenerator.getContext();
	
	
	@Test
	public void attributeTest() {
		
		Query query = Estivate.query(ParentEntity.class)
				.lt(Estivate.attribute(ParentEntity.class, ParentEntity.Fields.archived, Estivate.Functions.date_add(1, "DAY")), Estivate.attribute(ParentEntity.class, ParentEntity.Fields.created, Estivate.Functions.date_add(1, "DAY")));
		
		System.out.println(context.queryAsString(query));
		
	}

}
