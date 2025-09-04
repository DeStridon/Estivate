package com.estivate.query.test;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.UpdateQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

public class UpdateQueryCriterionTest {

	Context context = DatabaseGenerator.getContext();
	
	// ===== FOCUSED TESTS USING queryAsString() =====
	
	// === EQ Tests ===
	@Test
	public void eqWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = Estivate.updateQuery(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.eq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	@Test
	public void eqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.eq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate = operator", context.queryAsString(query).contains("HOMEID_D = ?"));
	}
	
	// === NOT EQ Tests ===
	@Test
	public void notEqWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notEq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notEq(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	@Test
	public void notEqWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notEq(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate != operator", context.queryAsString(query).contains("HOMEID_D != ?"));
	}
	
	// === LT Tests ===
	@Test
	public void ltWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	@Test
	public void ltWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate < operator", context.queryAsString(query).contains("HOMEID_D < ?"));
	}
	
	// === LTE Tests ===
	@Test
	public void lteWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	@Test
	public void lteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.lte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate <= operator", context.queryAsString(query).contains("HOMEID_D <= ?"));
	}
	
	// === GT Tests ===
	@Test
	public void gtWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gt(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gt(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	@Test
	public void gtWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gt(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate > operator", context.queryAsString(query).contains("HOMEID_D > ?"));
	}
	
	// === GTE Tests ===
	@Test
	public void gteWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gte(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gte(parentEntity, ParentEntity.Fields.homeId, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	@Test
	public void gteWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.gte(homeIdAttribute, 1001);
		
		Assert.assertTrue("Should generate >= operator", context.queryAsString(query).contains("HOMEID_D >= ?"));
	}
	
	// === BETWEEN Tests ===
	@Test
	public void betweenWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.between(ParentEntity.class, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.between(parentEntity, ParentEntity.Fields.homeId, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	@Test
	public void betweenWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.between(homeIdAttribute, 1001, 1010);
		
		Assert.assertTrue("Should generate BETWEEN operator", context.queryAsString(query).contains("HOMEID_D between ? and ?"));
	}
	
	// === IN Tests ===
	@Test
	public void inWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.in(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.in(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	@Test
	public void inWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.in(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate IN operator", context.queryAsString(query).contains("HOMEID_D in (?, ?, ?)"));
	}
	
	// === NOT IN Tests ===
	@Test
	public void notInWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notIn(ParentEntity.class, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notIn(parentEntity, ParentEntity.Fields.homeId, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	@Test
	public void notInWithAttributeQueryStringTest() throws SQLException {
		Attribute homeIdAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.homeId);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notIn(homeIdAttribute, Arrays.asList(1001, 1002, 1003));
		
		Assert.assertTrue("Should generate NOT IN operator", context.queryAsString(query).contains("HOMEID_D not in (?, ?, ?)"));
	}
	
	// === IS NULL Tests ===
	@Test
	public void isNullWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNull(ParentEntity.class, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNull(parentEntity, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	@Test
	public void isNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NULL operator", actualSQL.contains("NAME_D  is null"));
	}
	
	// === IS NOT NULL Tests ===
	@Test
	public void isNotNullWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNotNull(ParentEntity.class, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNotNull(parentEntity, ParentEntity.Fields.name);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	@Test
	public void isNotNullWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.isNotNull(nameAttribute);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate IS NOT NULL operator", actualSQL.contains("NAME_D  is not null"));
	}
	
	// === LIKE Tests ===
	@Test
	public void likeWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.like(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.like(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.like(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === NOT LIKE Tests ===
	@Test
	public void notLikeWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.notLike(ParentEntity.class, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.notLike(parentEntity, ParentEntity.Fields.name, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	@Test
	public void notLikeWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.notLike(nameAttribute, "test%");
		
		Assert.assertTrue("Should generate NOT LIKE operator", context.queryAsString(query).contains("NAME_D not like ?"));
	}
	
	// === LIKE CONTAINS Tests ===
	@Test
	public void likeContainsWithClassQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.likeContains(ParentEntity.class, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithEntityQueryStringTest() throws SQLException {
		Entity<ParentEntity> parentEntity = new Entity<>(ParentEntity.class);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.likeContains(parentEntity, ParentEntity.Fields.name, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	@Test
	public void likeContainsWithAttributeQueryStringTest() throws SQLException {
		Attribute nameAttribute = Estivate.attribute(ParentEntity.class, ParentEntity.Fields.name);
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.externalName, "updated")
			.likeContains(nameAttribute, "test");
		
		Assert.assertTrue("Should generate LIKE operator", context.queryAsString(query).contains("NAME_D like ?"));
	}
	
	// === EXISTS Tests ===
	@Test
	public void existsQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.exists(subQuery);
		
		Assert.assertTrue("Should generate EXISTS operator", context.queryAsString(query).contains("EXISTS"));
	}
	
	// === NOT EXISTS Tests ===
	@Test
	public void notExistsQueryStringTest() throws SQLException {
		SelectQuery<ChildEntity> subQuery = Estivate.selectQuery(ChildEntity.class)
			.eq(ChildEntity.class, ChildEntity.Fields.parentId, Estivate.attribute(ParentEntity.class, AbstractEntity.Fields.id));
		
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated")
			.notExists(subQuery);
		
		Assert.assertTrue("Should generate NOT EXISTS operator", context.queryAsString(query).contains("NOT EXISTS"));
	}
	
	// === SET Method Tests ===
	@Test
	public void setWithFieldNameQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated value")
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause", actualSQL.contains("SET") && actualSQL.contains("NAME_D  = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("HOMEID_D = ?"));
	}
	
	@Test
	public void setWithStringFieldNameQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set("name", "updated value")
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause", actualSQL.contains("SET") && actualSQL.contains("NAME_D  = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("HOMEID_D = ?"));
	}
	
	@Test
	public void multipleSetQueryStringTest() throws SQLException {
		UpdateQuery<ParentEntity> query = new UpdateQuery<>(ParentEntity.class)
			.set(ParentEntity.Fields.name, "updated name")
			.set(ParentEntity.Fields.externalName, "updated external")
			.eq(ParentEntity.class, ParentEntity.Fields.homeId, 1001);
		
		String actualSQL = context.queryAsString(query);
		Assert.assertTrue("Should generate SET clause for name", actualSQL.contains("NAME_D  = ?"));
		Assert.assertTrue("Should generate SET clause for externalName", actualSQL.contains("EXTERNALNAME_D  = ?"));
		Assert.assertTrue("Should generate WHERE clause", actualSQL.contains("HOMEID_D = ?"));
	}
	
}
