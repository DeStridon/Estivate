package com.estivate.query.test;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.DeleteQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

public class DeleteQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	// ===== FOCUSED TESTS USING queryAsString() =====
	
	// === EQ Tests ===
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	// === NOT EQ Tests ===
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	// === LT Tests ===
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	// === LTE Tests ===
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	// === GT Tests ===
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	// === GTE Tests ===
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	// === BETWEEN Tests ===
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.between(ParentEntity.class, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.between(parentEntity, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	// === IN Tests ===
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	// === NOT IN Tests ===
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notIn(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	// === IS NULL Tests ===
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNull(ParentEntity.class, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNull(parentEntity, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	// === IS NOT NULL Tests ===
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNotNull(ParentEntity.class, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNotNull(parentEntity, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.isNotNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	// === LIKE Tests ===
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.like(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.like(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE Tests ===
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLike(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLike(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === LIKE CONTAINS Tests ===
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContains(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContains(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === EQ IF NOT NULL Tests ===
	@Test
	public void eqIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	// === EQ NULLABLE Tests ===
	@Test
	public void eqNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqNullable(ParentEntity.class, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqNullable(parentEntity, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	@Test
	public void eqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.externalName);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.eqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("EXTERNALNAME_D = ?"));
	}
	
	// === EXISTS Tests ===
	@Test
	public void existsQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.exists(subQuery);
		
		Assert.assertTrue("Should generate EXISTS operator", context.queryAsString(query).contains("EXISTS"));
	}
	
	// === NOT EXISTS Tests ===
	@Test
	public void notExistsQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notExists(subQuery);
		
		Assert.assertTrue("Should generate NOT EXISTS operator", context.queryAsString(query).contains("NOT EXISTS"));
	}
	
	// === IN SUB QUERY Tests ===
	@Test
	public void inSubQueryWithClassQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.description, "test");
		
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inSubQuery(ParentEntity.class, ParentEntity.Fields.homeId, subQuery);
		
		Assert.assertTrue("Should generate IN subquery", context.queryAsString(query).contains("HOMEID_D in ("));
	}
	
	@Test
	public void inSubQueryWithAttributeQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.description, "test");
		
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inSubQuery(homeIdAttribute, subQuery);
		
		Assert.assertTrue("Should generate IN subquery", context.queryAsString(query).contains("HOMEID_D in ("));
	}
	
	// === NOT IN SUB QUERY Tests ===
	@Test
	public void notInSubQueryWithClassQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.description, "test");
		
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInSubQuery(ParentEntity.class, ParentEntity.Fields.homeId, subQuery);
		
		Assert.assertTrue("Should generate NOT IN subquery", context.queryAsString(query).contains("HOMEID_D not in ("));
	}
	
	@Test
	public void notInSubQueryWithAttributeQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.query(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.description, "test");
		
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInSubQuery(homeIdAttribute, subQuery);
		
		Assert.assertTrue("Should generate NOT IN subquery", context.queryAsString(query).contains("HOMEID_D not in ("));
	}
	
	// === IN IF NOT EMPTY Tests ===
	@Test
	public void inIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmpty(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmpty(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmpty(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	// === IN IF NOT EMPTY NULLABLE Tests ===
	@Test
	public void inIfNotEmptyNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyNullable(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyNullable(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyNullable(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	// === IN OR NULL Tests ===
	@Test
	public void inOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	// === IN IF NOT EMPTY OR NULL Tests ===
	@Test
	public void inIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	@Test
	public void inIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.inIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?)"));
	}
	
	// === NOT IN OR NULL Tests ===
	@Test
	public void notInOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	// === NOT IN IF NOT EMPTY OR NULL Tests ===
	@Test
	public void notInIfNotEmptyOrNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInIfNotEmptyOrNull(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInIfNotEmptyOrNull(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	@Test
	public void notInIfNotEmptyOrNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notInIfNotEmptyOrNull(homeIdAttribute, Arrays.asList(1001, 1002));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?)"));
	}
	
	// === LIKE IN Tests ===
	@Test
	public void likeInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE IN Tests ===
	@Test
	public void notLikeInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeIn(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === LIKE STARTS WITH IN Tests ===
	@Test
	public void likeStartsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE STARTS WITH Tests ===
	@Test
	public void notLikeStartsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === LIKE ENDS WITH Tests ===
	@Test
	public void likeEndsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE ENDS WITH Tests ===
	@Test
	public void notLikeEndsWithWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWith(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWith(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWith(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === NOT LIKE CONTAINS Tests ===
	@Test
	public void notLikeContainsWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContains(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContains(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === LIKE ENDS WITH IN Tests ===
	@Test
	public void likeEndsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE CONTAINS IN Tests ===
	@Test
	public void likeContainsInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE STARTS WITH IN Tests ===
	@Test
	public void notLikeStartsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeStartsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeStartsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === NOT LIKE ENDS WITH IN Tests ===
	@Test
	public void notLikeEndsWithInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWithIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWithIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeEndsWithInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeEndsWithIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === NOT LIKE CONTAINS IN Tests ===
	@Test
	public void notLikeContainsInWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContainsIn(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContainsIn(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeContainsInWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notLikeContainsIn(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === NOT EQ IF NOT NULL Tests ===
	@Test
	public void notEqIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	// === NOT EQ NULLABLE Tests ===
	@Test
	public void notEqNullableWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqNullable(ParentEntity.class, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqNullable(parentEntity, ParentEntity.Fields.externalName, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	@Test
	public void notEqNullableWithAttributeQueryStringTest() throws SQLException {
		Attribute externalNameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.externalName);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.notEqNullable(externalNameAttribute, "external");
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("EXTERNALNAME_D != ?"));
	}
	
	// === LT IF NOT NULL Tests ===
	@Test
	public void ltIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.ltIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.ltIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.ltIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	// === LTE IF NOT NULL Tests ===
	@Test
	public void lteIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lteIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lteIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.lteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	// === GT IF NOT NULL Tests ===
	@Test
	public void gtIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gtIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gtIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gtIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	// === GTE IF NOT NULL Tests ===
	@Test
	public void gteIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gteIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gteIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.gteIfNotNull(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	// === BETWEEN IF NOT NULL Tests ===
	@Test
	public void betweenIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.betweenIfNotNull(ParentEntity.class, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.betweenIfNotNull(parentEntity, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.betweenIfNotNull(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	// === LIKE IF NOT NULL Tests ===
	@Test
	public void likeIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIfNotNull(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeIfNotNull(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE STARTS WITH IF NOT NULL Tests ===
	@Test
	public void likeStartsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE ENDS WITH IF NOT NULL Tests ===
	@Test
	public void likeEndsWithIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE CONTAINS IF NOT NULL Tests ===
	@Test
	public void likeContainsIfNotNullWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIfNotNull(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIfNotNull(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsIfNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsIfNotNull(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE IN IF NOT EMPTY Tests ===
	@Test
	public void likeInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeInIfNotEmpty(nameAttribute, Arrays.asList("test%", "demo%"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE STARTS WITH IN IF NOT EMPTY Tests ===
	@Test
	public void likeStartsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeStartsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeStartsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE ENDS WITH IN IF NOT EMPTY Tests ===
	@Test
	public void likeEndsWithInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeEndsWithInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeEndsWithInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === LIKE CONTAINS IN IF NOT EMPTY Tests ===
	@Test
	public void likeContainsInIfNotEmptyWithClassQueryStringTest() throws SQLException {
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsInIfNotEmpty(ParentEntity.class, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsInIfNotEmpty(parentEntity, ParentEntity.Fields.name, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsInIfNotEmptyWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		DeleteQuery<ParentEntity> query = new DeleteQuery<>(ParentEntity.class)
			.likeContainsInIfNotEmpty(nameAttribute, Arrays.asList("test", "demo"));
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
}
