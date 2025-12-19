package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Estivate;
import com.estivate.query.Attribute.Function;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.experimental.SuperBuilder;

public abstract class Query<Q extends Query<Q, E>, E> extends Aggregator {
	
	@lombok.Getter
	final Entity<E> entity;
	
	public Query(Class<E> baseClass) {
		super(GroupType.AND);
		this.entity = new Entity<E>(baseClass);
	}
	
	public Query(Entity<E> entity) {
		super(GroupType.AND);
		this.entity = entity;
	}
	
	
	@lombok.Getter
	List<String> comments = new ArrayList<>();

	// comes with "join" method, enables developer to join manually classes (for bridge classes without any criterion on it)
	@lombok.Getter
	Set<Join> joins = new LinkedHashSet<>();

	
	@lombok.Getter
	List<Order> orders = new ArrayList<>();

	
	@lombok.Getter
	Integer offset;

	@lombok.Getter
	Integer limit;
	
	
	public abstract Query<Q, E> clone();
	
	@SuppressWarnings("unchecked")
	protected Q self() {
        return (Q) this;
    }

	public Q comment(String comment) {
		comments.add(comment);
		return self();
	}

	public Q join(Join join) { 
		if(joins.stream().noneMatch(x -> x.leftEntity.equals(join.leftEntity) && x.rightEntity.equals(join.rightEntity) && x.joinType == join.joinType)) {
			joins.add(join);
		}
		return self();
	}

	public Q joinInner(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinInner(leftEntity, rightEntity)); }
	public Q joinInner(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass)));}
	public Q joinInner(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity));}
	public Q joinInner(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinOuter(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinOuter(leftEntity, rightEntity)); }
	public Q joinOuter(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass))); }
	public Q joinOuter(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity)); }
	public Q joinOuter(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinLeft(Entity<?> leftEntity, 	Entity<?> rightEntity)	{ return join(Estivate.joinLeft(leftEntity, rightEntity)); }
	public Q joinLeft(Entity<?> leftEntity, 	Class<?> rightClass)	{ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass))); }
	public Q joinLeft(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity)); }
	public Q joinLeft(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinRight(Entity<?> leftEntity, Entity<?> rightEntity)		{ return join(Estivate.joinRight(leftEntity, rightEntity)); }
	public Q joinRight(Entity<?> leftEntity, Class<?> rightClass)		{ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass))); }
	public Q joinRight(Class<?> leftClass, 	Entity<?> rightEntity)		{ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity)); }
	public Q joinRight(Class<?> leftClass, 	Class<?> rightClass)		{ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass))); }

	public Q joinInner(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinInner(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinInner(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinInner(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinOuter(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinOuter(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinOuter(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinOuter(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinLeft(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinLeft(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinLeft(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinLeft(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	public Q joinRight(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightEntity, leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, Entity<?> rightEntity, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightEntity, leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, Class<?> rightClass, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), new Entity<>(rightClass), leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, SubQueryEntity<?> rightSubQuery, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, rightSubQuery, leftAttribute, rightAttribute)); }
	public Q joinRight(Class<?> leftClass, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(new Entity<>(leftClass), Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }
	public Q joinRight(Entity<?> leftEntity, SelectQuery<?> rightQuery, String alias, String leftAttribute, String rightAttribute){ return join(Estivate.joinRight(leftEntity, Estivate.subQueryEntity(rightQuery, alias), leftAttribute, rightAttribute)); }

	
	
	
	public Q add(EstivateNode node) { super.add(node); return self(); }
	public Q addIf(boolean condition, EstivateNode node) { super.addIf(condition, node); return self(); }
	public Q and(EstivateNode... nodes) { criterions.add(Estivate.and(nodes)); return self(); }
	public Q and(Collection<EstivateNode> nodes) { criterions.add(Estivate.and(nodes)); return self(); }
	public Q or(EstivateNode... nodes) 	{ criterions.add(Estivate.or(nodes));  return self(); }
	public Q or(Collection<EstivateNode> nodes) { criterions.add(Estivate.or(nodes)); return self(); }
	
	public Q eq   			(Attribute attribute, Object value)	{ super.eq(attribute, value);  return self(); }
	public Q eqIfNotNull   	(Attribute attribute, Object value) { super.eqIfNotNull(attribute, value);  return self(); }
	public Q eqNullable		(Attribute attribute, Object value) { super.eqNullable(attribute, value); return self(); }
	public Q notEq			(Attribute attribute, Object value) { super.notEq(attribute, value);  return self(); }
	public Q notEqIfNotNull	(Attribute attribute, Object value) { super.notEqIfNotNull(attribute, value);  return self(); }
	public Q notEqNullable	(Attribute attribute, Object value)	{ super.notEqNullable(attribute, value); return self(); }
	
	public Q lt   			(Attribute attribute, Object value) { super.lt(attribute, value);  return self(); }
	public Q ltIfNotNull	(Attribute attribute, Object value) { super.ltIfNotNull(attribute, value);  return self(); }
	
	public Q lte  			(Attribute attribute, Object value) { super.lte(attribute, value);  return self(); }
	public Q lteIfNotNull  	(Attribute attribute, Object value) { super.lteIfNotNull(attribute, value);  return self(); }
	
	
	public Q gt   	(Attribute attribute, Object value)        		{ super.gt(attribute, value);  return self(); }
	public Q gtIfNotNull   		(Attribute attribute, Object value)   { super.gtIfNotNull(attribute, value);  return self(); }
	public Q gte  	(Attribute attribute, Object value)        		{ super.gte(attribute, value);  return self(); }
	public Q gteIfNotNull  		(Attribute attribute, Object value)   { super.gteIfNotNull(attribute, value);  return self(); }
	
	public Q between(Attribute attribute, Object min, Object max) 	{ super.between(attribute, min, max); return self(); }
	public Q betweenIfNotNull	(Attribute attribute, Object min, Object max) { super.betweenIfNotNull(attribute, min, max); return self(); }

	public Q in   					(Attribute attribute, Collection<?> values) { super.in(attribute, values); return self(); }
	public Q inIfNotEmpty  			(Attribute attribute, Collection<?> values) { super.inIfNotEmpty(attribute, values); return self(); }
	public Q inIfNotEmptyNullable  	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyNullable(attribute, values); return self(); }
	public Q inOrNull 				(Attribute attribute, Collection<?> values) { super.inOrNull(attribute, values); return self(); }
	public Q inIfNotEmptyOrNull 	(Attribute attribute, Collection<?> values) { super.inIfNotEmptyOrNull(attribute, values); return self(); }
	public Q notIn  				(Attribute attribute, Collection<?> values) { super.notIn(attribute, values); return self(); }
	public Q notInOrNull			(Attribute attribute, Collection<?> values) { super.notInOrNull(attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(Attribute attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(attribute, values); return self(); }


	public Q like 		(Attribute attribute, String value)				{ super.like(attribute, value);  return self(); }
	public Q likeIn 		(Attribute attribute, Collection<String> value)	{ super.likeIn(attribute, value);  return self(); }
	public Q notLike		(Attribute attribute, String value)				{ super.notLike(attribute, value);  return self(); }
	public Q notLikeIn	(Attribute attribute, Collection<String> value)	{ super.notLikeIn(attribute, value);  return self(); }
	
	
	public Q likeStartsWith 	(Attribute attribute, String value)	{ super.likeStartsWith(attribute, value);  return self(); }
	public Q likeStartsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeStartsWithIn(attribute, value);  return self(); }
	public Q notLikeStartsWith 	(Attribute attribute, String value)	{ super.notLikeStartsWith(attribute, value);  return self(); }
	
	public Q likeEndsWith 		(Attribute attribute, String value)	{ super.likeEndsWith(attribute, value);  return self(); }
	public Q notLikeEndsWith 	(Attribute attribute, String value)	{ super.notLikeEndsWith(attribute, value);  return self(); }
	
	public Q likeContains 		(Attribute attribute, String value)	{ super.likeContains(attribute, value);  return self(); }
	public Q notLikeContains 	(Attribute attribute, String value)	{ super.notLikeContains(attribute, value);  return self(); }

	public Q likeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.likeEndsWithIn(attribute, value);  return self(); }
	public Q likeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.likeContainsIn(attribute, value);  return self(); }
	public Q notLikeStartsWithIn(Attribute attribute, Collection<String> value)	{ super.notLikeStartsWithIn(attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeEndsWithIn(attribute, value);  return self(); }
	public Q notLikeContainsIn 	(Attribute attribute, Collection<String> value)	{ super.notLikeContainsIn(attribute, value);  return self(); }

	public Q nativeCriterion 	(Attribute attribute, String criterion) { super.nativeCriterion(attribute, criterion); return self(); }
	
	public Q in					(Attribute attribute, SelectQuery<?> subQuery){ super.in(attribute, subQuery); return self(); }
	public Q notIn				(Attribute attribute, SelectQuery<?> subQuery){ super.notIn(attribute, subQuery); return self(); }
	public Q exists				(SelectQuery<?> subQuery){ super.exists(subQuery); return self(); }
	public Q notExists			(SelectQuery<?> subQuery){ super.notExists(subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(Attribute attribute, Collection<?> values) { super.notInIfNotEmpty   (attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(Attribute attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Attribute attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(attribute, values); return self(); }
	
	
	public Q likeIfNotNull 				(Attribute attribute, String value)        	{ super.likeIfNotNull (attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(Attribute attribute, String value)        	{ super.likeStartsWithIfNotNull (attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 		(Attribute attribute, String value)        	{ super.likeEndsWithIfNotNull (attribute, value);  return self(); }
	public Q likeContainsIfNotNull 		(Attribute attribute, String value)        	{ super.likeContainsIfNotNull (attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { super.likeInIfNotEmpty(attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty  (Attribute attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(Attribute attribute, String value)        	{ super.notLikeIfNotNull (attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull (Attribute attribute, String value)        	{ super.notLikeStartsWithIfNotNull (attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeEndsWithIfNotNull (attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(Attribute attribute, String value)        	{ super.notLikeContainsIfNotNull (attribute, value);  return self(); }

	public Q matchAgainst(Attribute attribute, String value) { super.matchAgainst(attribute, value); return self(); }

	public Q matchAgainstIfNotNull(Attribute attribute, String value) { super.matchAgainstIfNotNull(attribute, value); return self(); }

	public Q notMatchAgainst(Attribute attribute, String value) { super.notMatchAgainst(attribute, value); return self(); }

	public Q notMatchAgainstIfNotNull(Attribute attribute, String value) { super.notMatchAgainstIfNotNull(attribute, value); return self(); }


	public Q matchAgainstIn(Attribute attribute, Collection<String> values) { super.matchAgainstIn(attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(attribute, values); return self(); }
	public Q notMatchAgainstIn(Attribute attribute, Collection<String> values) { super.notMatchAgainstIn(attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(attribute, values); return self(); }
	
	public Q isNotNull	(Attribute attribute) 				{ super.isNotNull(attribute); 		return self();}
	public Q isNull		(Attribute attribute) 				{ super.isNull(attribute); 			return self();}

	public Q eqOrNull	(Attribute attribute, Object value) 	{ super.eqOrNull(attribute, value); return self();	}
	public Q ltOrNull	(Attribute attribute, Object value) 	{ super.ltOrNull(attribute, value); return self();	}
	public Q gtOrNull	(Attribute attribute, Object value) 	{ super.gtOrNull(attribute, value); return self();	}
	public Q lteOrNull	(Attribute attribute, Object value) 	{ super.lteOrNull(attribute, value); return self();	}
	public Q gteOrNull	(Attribute attribute, Object value) 	{ super.gteOrNull(attribute, value); return self();	}


	// Attribute only wrappers

	public Q eq   			(String attribute, Object value) { super.eq(this.entity, attribute, value);  return self(); }
	public Q eqIfNotNull   	(String attribute, Object value) { super.eqIfNotNull(this.entity, attribute, value);  return self(); }
	public Q eqNullable		(String attribute, Object value) { super.eqNullable(this.entity, attribute, value); return self(); }
	public Q notEq			(String attribute, Object value) { super.notEq(this.entity, attribute, value);  return self(); }
	public Q notEqIfNotNull	(String attribute, Object value) { super.notEqIfNotNull(this.entity, attribute, value);  return self(); }
	public Q notEqNullable	(String attribute, Object value) { super.notEqNullable(this.entity, attribute, value); return self(); }
	
	public Q lt   			(String attribute, Object value) { super.lt(this.entity, attribute, value);  return self(); }
	public Q ltIfNotNull	(String attribute, Object value) { super.ltIfNotNull(this.entity, attribute, value);  return self(); }
	
	public Q lte  			(String attribute, Object value) { super.lte(this.entity, attribute, value);  return self(); }
	public Q lteIfNotNull  	(String attribute, Object value) { super.lteIfNotNull(this.entity, attribute, value);  return self(); }
	
	
	public Q gt   			(String attribute, Object value) { super.gt(this.entity, attribute, value);  return self(); }
	public Q gtIfNotNull   	(String attribute, Object value) { super.gtIfNotNull(this.entity, attribute, value);  return self(); }
	public Q gte  			(String attribute, Object value) { super.gte(this.entity, attribute, value);  return self(); }
	public Q gteIfNotNull  	(String attribute, Object value) { super.gteIfNotNull(this.entity, attribute, value);  return self(); }
	
	public Q between(String attribute, Object min, Object max) { super.between(this.entity, attribute, min, max); return self(); }
	public Q betweenIfNotNull	(String attribute, Object min, Object max) { super.betweenIfNotNull(this.entity, attribute, min, max); return self(); }

	public Q in   					(String attribute, Collection<?> values) { super.in(this.entity, attribute, values); return self(); }
	public Q inIfNotEmpty  			(String attribute, Collection<?> values) { super.inIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable  	(String attribute, Collection<?> values) { super.inIfNotEmptyNullable(this.entity, attribute, values); return self(); }
	public Q inOrNull				(String attribute, Collection<?> values) { super.inOrNull(this.entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull		(String attribute, Collection<?> values) { super.inIfNotEmptyOrNull(this.entity, attribute, values); return self(); }
	public Q notIn  				(String attribute, Collection<?> values) { super.notIn(this.entity, attribute, values); return self(); }
	public Q notInOrNull			(String attribute, Collection<?> values) { super.notInOrNull(this.entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(this.entity, attribute, values); return self(); }


	public Q like 		(String attribute, String value)				{ super.like(this.entity, attribute, value);  return self(); }
	public Q likeIn 	(String attribute, Collection<String> value)	{ super.likeIn(this.entity, attribute, value);  return self(); }
	public Q notLike	(String attribute, String value)				{ super.notLike(this.entity, attribute, value);  return self(); }
	public Q notLikeIn	(String attribute, Collection<String> value)	{ super.notLikeIn(this.entity, attribute, value);  return self(); }
	
	
	public Q likeStartsWith 	(String attribute, String value)	{ super.likeStartsWith(this.entity, attribute, value);  return self(); }
	public Q likeStartsWithIn 	(String attribute, Collection<String> value)	{ super.likeStartsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWith 	(String attribute, String value)	{ super.notLikeStartsWith(this.entity, attribute, value);  return self(); }
	
	public Q likeEndsWith 		(String attribute, String value)	{ super.likeEndsWith(this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWith 	(String attribute, String value)	{ super.notLikeEndsWith(this.entity, attribute, value);  return self(); }
	
	public Q likeContains 		(String attribute, String value)	{ super.likeContains(this.entity, attribute, value);  return self(); }
	public Q notLikeContains 	(String attribute, String value)	{ super.notLikeContains(this.entity, attribute, value);  return self(); }

	public Q likeEndsWithIn 	(String attribute, Collection<String> value)	{ super.likeEndsWithIn(this.entity, attribute, value);  return self(); }
	public Q likeContainsIn 	(String attribute, Collection<String> value)	{ super.likeContainsIn(this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIn(String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(this.entity, attribute, value);  return self(); }
	public Q notLikeContainsIn 	(String attribute, Collection<String> value)	{ super.notLikeContainsIn(this.entity, attribute, value);  return self(); }

	public Q nativeCriterion 	(String attribute, String criterion) { super.nativeCriterion(this.entity, attribute, criterion); return self(); }
	
	public Q inSubQuery			(String attribute, SelectQuery<?> subQuery){ super.inSubQuery(this.entity, attribute, subQuery); return self(); }
	public Q notInSubQuery		(String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(this.entity, attribute, subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(String attribute, Collection<?> values) { super.notInIfNotEmpty   (this.entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (this.entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(this.entity, attribute, values); return self(); }
	
	
	public Q likeIfNotNull 				(String attribute, String value)        	{ super.likeIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(String attribute, String value)        	{ super.likeStartsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 		(String attribute, String value)        	{ super.likeEndsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q likeContainsIfNotNull 		(String attribute, String value)        	{ super.likeContainsIfNotNull (this.entity, attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(String attribute, Collection<String> values) { super.likeInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty (String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(this.entity, attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(String attribute, String value)        	{ super.notLikeIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull (String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (this.entity, attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(String attribute, String value)        	{ super.notLikeContainsIfNotNull (this.entity, attribute, value);  return self(); }

	public Q matchAgainst(String attribute, String value) { super.matchAgainst(this.entity, attribute, value); return self(); }

	public Q matchAgainstIfNotNull(String attribute, String value) { super.matchAgainstIfNotNull(this.entity, attribute, value); return self(); }

	public Q notMatchAgainst(String attribute, String value) { super.notMatchAgainst(this.entity, attribute, value); return self(); }

	public Q notMatchAgainstIfNotNull(String attribute, String value) { super.notMatchAgainstIfNotNull(this.entity, attribute, value); return self(); }


	public Q matchAgainstIn(String attribute, Collection<String> values) { super.matchAgainstIn(this.entity, attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(this.entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(String attribute, Collection<String> values) { super.notMatchAgainstIn(this.entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(this.entity, attribute, values); return self(); }
	
	public Q isNotNull	(String attribute) 				{ super.isNotNull(this.entity, attribute); 		return self();}
	public Q isNull		(String attribute) 				{ super.isNull(this.entity, attribute); 			return self();}

	public Q eqOrNull	(String attribute, Object value) 	{ super.eqOrNull(this.entity, attribute, value); return self();	}
	public Q ltOrNull	(String attribute, Object value) 	{ super.ltOrNull(this.entity, attribute, value); return self();	}
	public Q gtOrNull	(String attribute, Object value) 	{ super.gtOrNull(this.entity, attribute, value); return self();	}
	public Q lteOrNull	(String attribute, Object value) 	{ super.lteOrNull(this.entity, attribute, value); return self();	}
	public Q gteOrNull	(String attribute, Object value) 	{ super.gteOrNull(this.entity, attribute, value); return self();	}


	// Class wrappers
	public Q eq   			(Class<?> entity, String attribute, Object value)	{ super.eq(entity, attribute, value);  return self(); }
	public Q eqIfNotNull   (Class<?> entity, String attribute, Object value)   { super.eqIfNotNull(entity, attribute, value);  return self(); }
	public Q eqNullable	(Class<?> entity, String attribute, Object value)   { super.eqNullable(entity, attribute, value); return self(); }
	public Q notEq			(Class<?> entity, String attribute, Object value)   { super.notEq(entity, attribute, value);  return self(); }
	public Q notEqIfNotNull(Class<?> entity, String attribute, Object value)   { super.notEqIfNotNull(entity, attribute, value);  return self(); }
	public Q notEqNullable	(Class<?> entity, String attribute, Object value)	{ super.notEqNullable(entity, attribute, value); return self(); }
	
	public Q lt   			(Class<?> entity, String attribute, Object value)   { super.lt(entity, attribute, value);  return self(); }
	public Q ltIfNotNull	(Class<?> entity, String attribute, Object value)   { super.ltIfNotNull(entity, attribute, value);  return self(); }
	
	public Q lte  			(Class<?> entity, String attribute, Object value)   { super.lte   (entity, attribute, value);  return self(); }
	public Q lteIfNotNull  (Class<?> entity, String attribute, Object value)   { super.lteIfNotNull  (entity, attribute, value);  return self(); }
	
	
	public Q gt   			(Class<?> entity, String attribute, Object value)   { super.gt    (entity, attribute, value);  return self(); }
	public Q gtIfNotNull 	(Class<?> entity, String attribute, Object value)   { super.gtIfNotNull   (entity, attribute, value);  return self(); }
	public Q gte  			(Class<?> entity, String attribute, Object value)   { super.gte   (entity, attribute, value);  return self(); }
	public Q gteIfNotNull	(Class<?> entity, String attribute, Object value)   { super.gteIfNotNull  (entity, attribute, value);  return self(); }
	
	public Q between			(Class<?> entity, String attribute, Object min, Object max) 	{ super.between(entity, attribute, min, max); return self(); }
	public Q betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max) { super.betweenIfNotNull(entity, attribute, min, max); return self(); }

	public Q in   					(Class<?> entity, String attribute, Collection<?> values) { super.in    				(entity, attribute, values); return self(); }
	public Q inIfNotEmpty  		(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmpty  		(entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable  (Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable  (entity, attribute, values); return self(); }
	public Q inOrNull			  	(Class<?> entity, String attribute, Collection<?> values) { super.inOrNull			    (entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyOrNull	(entity, attribute, values); return self(); }
	public Q notIn  				(Class<?> entity, String attribute, Collection<?> values) { super.notIn   				(entity, attribute, values); return self(); }
	public Q notInOrNull		  	(Class<?> entity, String attribute, Collection<?> values) { super.notInOrNull		    (entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull	(Class<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull (entity, attribute, values); return self(); }
	


	public Q like 		(Class<?> entity, String attribute, String value)				{ super.like(entity, attribute, value);  return self(); }
	public Q likeIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.likeIn(entity, attribute, value);  return self(); }
	public Q notLike	(Class<?> entity, String attribute, String value)				{ super.notLike(entity, attribute, value);  return self(); }
	public Q notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeIn(entity, attribute, value);  return self(); }
	
	
	public Q likeStartsWith 		(Class<?> entity, String attribute, String value)	{ super.likeStartsWith(entity, attribute, value);  return self(); }
	public Q likeStartsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeStartsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeStartsWith 	(Class<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);  return self(); }
	
	public Q likeEndsWith 			(Class<?> entity, String attribute, String value)	{ super.likeEndsWith(entity, attribute, value);  return self(); }
	public Q notLikeEndsWith 		(Class<?> entity, String attribute, String value)	{ super.notLikeEndsWith(entity, attribute, value);  return self(); }
	
	public Q likeContains 			(Class<?> entity, String attribute, String value)	{ super.likeContains(entity, attribute, value);  return self(); }
	public Q notLikeContains 		(Class<?> entity, String attribute, String value)	{ super.notLikeContains(entity, attribute, value);  return self(); }

	public Q likeEndsWithIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeEndsWithIn(entity, attribute, value);  return self(); }
	public Q likeContainsIn 		(Class<?> entity, String attribute, Collection<String> value)	{ super.likeContainsIn(entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeStartsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeEndsWithIn(entity, attribute, value);  return self(); }
	public Q notLikeContainsIn 	(Class<?> entity, String attribute, Collection<String> value)	{ super.notLikeContainsIn(entity, attribute, value);  return self(); }

	public Q nativeCriterion 		(Class<?> entity, String attribute, String criterion) { super.nativeCriterion(entity, attribute, criterion); return self(); }
	
	public Q inSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.inSubQuery(entity, attribute, subQuery); return self(); }
	public Q notInSubQuery			(Class<?> entity, String attribute, SelectQuery<?> subQuery){ super.notInSubQuery(entity, attribute, subQuery); return self(); }

	
	public Q notInIfNotEmpty   	(Class<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty		(Class<?> entity, String attribute, Collection<?> values)	{ super.inOrFalseIfEmpty  (entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ super.notInOrTrueIfEmpty(entity, attribute, values); return self(); }
	
	
	public Q likeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeStartsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q likeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeEndsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q likeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.likeContainsIfNotNull (entity, attribute, value);  return self(); }

	public Q likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return self(); }
	
	public Q notLikeIfNotNull 			(Class<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value)        	{ super.notLikeStartsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeEndsWithIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeEndsWithIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeContainsIfNotNull 	(Class<?> entity, String attribute, String value)        	{ super.notLikeContainsIfNotNull (entity, attribute, value);  return self(); }

	public Q matchAgainst(Class<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return self(); }
	public Q matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q notMatchAgainst(Class<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return self(); }
	public Q notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return self(); }

	public Q matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return self(); }
	public Q matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	
	public Q isNotNull	(Class<?> entity, String attribute) 				{ super.isNotNull(entity, attribute); 		return self();}
	public Q isNull	(Class<?> entity, String attribute) 				{ super.isNull(entity, attribute); 			return self();}

	public Q eqOrNull	(Class<?> entity, String attribute, Object value) 	{ super.eqOrNull(entity, attribute, value); return self();	}
	public Q ltOrNull	(Class<?> entity, String attribute, Object value) 	{ super.ltOrNull(entity, attribute, value); return self();	}
	public Q gtOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gtOrNull(entity, attribute, value); return self();	}
	public Q lteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.lteOrNull(entity, attribute, value); return self();	}
	public Q gteOrNull	(Class<?> entity, String attribute, Object value) 	{ super.gteOrNull(entity, attribute, value); return self();	}


	public Q eq   			(Entity<?> entity, String attribute, Object value)      { super.eq    	(entity, attribute, value);  		return self(); }
	public Q eqNullable	(Entity<?> entity, String attribute, Object value)		{ super.eqNullable(entity, attribute, value); 		return self(); }
	public Q notEq			(Entity<?> entity, String attribute, Object value)      { super.notEq 	(entity, attribute, value);  		return self(); }
	public Q notEqIfNotNull(Entity<?> entity, String attribute, Object value)		{ super.notEqIfNotNull(entity, attribute, value); 	return self(); }
	public Q notEqNullable	(Entity<?> entity, String attribute, Object value)		{ super.notEqNullable(entity, attribute, value); 	return self(); }
	

	public Q lt   	(Entity<?> entity, String attribute, Object value)        		{ super.lt    	(entity, attribute, value);  		return self(); }
	public Q lte  	(Entity<?> entity, String attribute, Object value)        		{ super.lte   	(entity, attribute, value);  		return self(); }
	
	public Q gt   	(Entity<?> entity, String attribute, Object value)        		{ super.gt    	(entity, attribute, value);  		return self(); }
	public Q gte  	(Entity<?> entity, String attribute, Object value)        		{ super.gte   	(entity, attribute, value);  		return self(); }
	public Q between(Entity<?> entity, String attribute, Object min, Object max) 	{ super.between	(entity, attribute, min, max); 		return self(); }

	public Q in   	(Entity<?> entity, String attribute, Collection<?> values) 		{ super.in    	(entity, attribute, values); 		return self(); }
	public Q inIfNotEmpty  (Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmpty      (entity, attribute, values); return self(); }
	public Q inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values) { super.inIfNotEmptyNullable(entity, attribute, values); return self(); }
	public Q inOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inOrNull(entity, attribute, values); return self(); }
	public Q inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) 	{ super.inIfNotEmptyOrNull(entity, attribute, values); return self(); }
	public Q notIn  (Entity<?> entity, String attribute, Collection<?> values) 		{ super.notIn   (entity, attribute, values); 		return self(); }
	public Q notInOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInOrNull(entity, attribute, values); return self(); }
	public Q notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values) { super.notInIfNotEmptyOrNull(entity, attribute, values); return self(); }
	
	public Q like 	(Entity<?> entity, String attribute, String value)	    		{ super.like  	(entity, attribute, value);  		return self(); }
	public Q likeStartsWith(Entity<?> entity, String attribute, String value)		{ super.likeStartsWith(entity, attribute, value);	return self(); }
	public Q likeEndsWith(Entity<?> entity, String attribute, String value)			{ super.likeEndsWith(entity, attribute, value);		return self(); }
	public Q likeContains(Entity<?> entity, String attribute, String value)			{ super.likeContains(entity, attribute, value);		return self(); }

	public Q notLike(Entity<?> entity, String attribute, String value)	    		{ super.notLike (entity, attribute, value);  		return self(); }
	public Q notLikeStartsWith(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWith(entity, attribute, value);return self(); }
	public Q notLikeEndsWith(Entity<?> entity, String attribute, String value)		{ super.notLikeEndsWith(entity, attribute, value);	return self(); }
	public Q notLikeContains(Entity<?> entity, String attribute, String value)		{ super.notLikeContains(entity, attribute, value);	return self(); }

	public Q likeIn 	(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.likeIn  	(entity, attribute, values);  		return self(); }
	public Q likeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.likeStartsWithIn(entity, attribute, values);	return self(); }
	public Q likeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeEndsWithIn(entity, attribute, values);		return self(); }
	public Q likeContainsIn(Entity<?> entity, String attribute, Collection<String> values)		{ super.likeContainsIn(entity, attribute, values);		return self(); }

	public Q notLikeIn(Entity<?> entity, String attribute, Collection<String> values)	    	{ super.notLikeIn (entity, attribute, values);  		return self(); }
	public Q notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeStartsWithIn(entity, attribute, values);return self(); }
	public Q notLikeEndsWithIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeEndsWithIn(entity, attribute, values);	return self(); }
	public Q notLikeContainsIn(Entity<?> entity, String attribute, Collection<String> values)	{ super.notLikeContainsIn(entity, attribute, values);	return self(); }

	public Q eqIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.eqIfNotNull   (entity, attribute, value);  return self(); }
	public Q ltIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.ltIfNotNull   (entity, attribute, value);  return self(); }
	public Q gtIfNotNull   		(Entity<?> entity, String attribute, Object value)        	{ super.gtIfNotNull   (entity, attribute, value);  return self(); }
	public Q lteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.lteIfNotNull  (entity, attribute, value);  return self(); }
	public Q gteIfNotNull  		(Entity<?> entity, String attribute, Object value)        	{ super.gteIfNotNull  (entity, attribute, value);  return self(); }
	public Q betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ super.betweenIfNotNull (entity, attribute, min, max); return self(); }
	
	public Q notInIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) 	{ super.notInIfNotEmpty   (entity, attribute, values); return self(); }
	public Q inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.inOrFalseIfEmpty  (entity, attribute, values); return self(); }
	public Q notInOrTrueIfEmpty	(Entity<?> entity, String attribute, Collection<?> values)  { super.notInOrTrueIfEmpty(entity, attribute, values); return self(); }
	
	public Q likeIfNotNull 		(Entity<?> entity, String attribute, String value)        	{ super.likeIfNotNull (entity, attribute, value);  return self(); }
	public Q likeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeStartsWithIfNotNull(entity, attribute, value);	return self(); }
	public Q likeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeEndsWithIfNotNull(entity, attribute, value);		return self(); }
	public Q likeContainsIfNotNull(Entity<?> entity, String attribute, String value)		{ super.likeContainsIfNotNull(entity, attribute, value);		return self(); }

	public Q likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { super.likeInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeStartsWithInIfNotEmpty (Entity<?> entity, String attribute, Collection<String> values) { super.likeStartsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeEndsWithInIfNotEmpty(entity, attribute, values); return self(); }
	public Q likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { super.likeContainsInIfNotEmpty(entity, attribute, values); return self(); }

	
	public Q notLikeIfNotNull 	(Entity<?> entity, String attribute, String value)        	{ super.notLikeIfNotNull (entity, attribute, value);  return self(); }
	public Q notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeStartsWithIfNotNull(entity, attribute, value);return self(); }
	public Q notLikeEndsWithIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeEndsWithIfNotNull(entity, attribute, value);	return self(); }
	public Q notLikeContainsIfNotNull(Entity<?> entity, String attribute, String value)	{ super.notLikeContainsIfNotNull(entity, attribute, value);	return self(); }
	
	public Q isNotNull			(Entity<?> entity, String attribute) {super.isNotNull(entity, attribute); return self();}
	public Q isNull				(Entity<?> entity, String attribute) {super.isNull(entity, attribute); return self();}
	public Q eqOrNull			(Entity<?> entity, String attribute, Object value) { super.eqOrNull(entity, attribute, value); return self();	}
	public Q ltOrNull			(Entity<?> entity, String attribute, Object value) { super.ltOrNull(entity, attribute, value); return self();	}
	public Q gtOrNull			(Entity<?> entity, String attribute, Object value) { super.gtOrNull(entity, attribute, value); return self();	}
	public Q lteOrNull			(Entity<?> entity, String attribute, Object value) { super.lteOrNull(entity, attribute, value); return self();}
	public Q gteOrNull			(Entity<?> entity, String attribute, Object value) { super.gteOrNull(entity, attribute, value); return self();}
	
	public Q matchAgainst(Entity<?> entity, String attribute, String value) { super.matchAgainst(entity, attribute, value); return self(); }	
	public Q matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.matchAgainstIfNotNull(entity, attribute, value); return self(); }
	public Q notMatchAgainst(Entity<?> entity, String attribute, String value) { super.notMatchAgainst(entity, attribute, value); return self(); }
	public Q notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { super.notMatchAgainstIfNotNull(entity, attribute, value); return self(); }
	
	public Q matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstIn(entity, attribute, values); return self(); }	
	public Q matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.matchAgainstInIfNotEmpty(entity, attribute, values); return self(); }
	public Q notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstIn(entity, attribute, values); return self(); }
	public Q notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(entity, attribute, values); return self(); }


	/* Wrappers for Lambda */
	public <T, P> Q eq   			(AttributeGetter<T, P> function, P value) { super.eq(function, value);  return self(); }
	public <T, P> Q eqIfNotNull   	(AttributeGetter<T, P> function, P value) { super.eqIfNotNull(function, value);  return self(); }
	public <T, P> Q eqNullable		(AttributeGetter<T, P> function, P value) { super.eqNullable(function, value); return self(); }
	public <T, P> Q eqOrNull		(AttributeGetter<T, P> function, P value) { super.eqOrNull(function, value); return self(); }
	public <T, P> Q notEq			(AttributeGetter<T, P> function, P value) { super.notEq(function, value);  return self(); }
	public <T, P> Q notEqIfNotNull	(AttributeGetter<T, P> function, P value) { super.notEqIfNotNull(function, value);  return self(); }
	public <T, P> Q notEqNullable	(AttributeGetter<T, P> function, P value) { super.notEqNullable(function, value); return self(); }
	public <T, P> Q notEqOrNull		(AttributeGetter<T, P> function, P value) { super.notEqOrNull(function, value); return self(); }

	public <T, P> Q lt   			(AttributeGetter<T, P> function, P value) { super.lt(function, value);  return self(); }
	public <T, P> Q ltIfNotNull		(AttributeGetter<T, P> function, P value) { super.ltIfNotNull(function, value);  return self(); }
	public <T, P> Q ltOrNull		(AttributeGetter<T, P> function, P value) { super.ltOrNull(function, value); return self(); }

	public <T, P> Q lte  		(AttributeGetter<T, P> function, P value) { super.lte(function, value);  return self(); }
	public <T, P> Q lteIfNotNull  	(AttributeGetter<T, P> function, P value) { super.lteIfNotNull(function, value);  return self(); }
	public <T, P> Q lteOrNull		(AttributeGetter<T, P> function, P value) { super.lteOrNull(function, value); return self(); }
	
	public <T, P> Q gt   			(AttributeGetter<T, P> function, P value) { super.gt(function, value);  return self(); }
	public <T, P> Q gtIfNotNull   	(AttributeGetter<T, P> function, P value) { super.gtIfNotNull(function, value);  return self(); }
	public <T, P> Q gtOrNull		(AttributeGetter<T, P> function, P value) { super.gtOrNull(function, value); return self(); }
	public <T, P> Q gte  			(AttributeGetter<T, P> function, P value) { super.gte(function, value);  return self(); }
	public <T, P> Q gteIfNotNull  	(AttributeGetter<T, P> function, P value) { super.gteIfNotNull(function, value);  return self(); }
	public <T, P> Q gteOrNull		(AttributeGetter<T, P> function, P value) { super.gteOrNull(function, value); return self(); }
	
	public <T, P> Q between			(AttributeGetter<T, P> function, P min, P max) { super.between(function, min, max); return self(); }
	public <T, P> Q betweenIfNotNull(AttributeGetter<T, P> function, P min, P max) { super.betweenIfNotNull(function, min, max); return self(); }

	public <T, P> Q in   					(AttributeGetter<T, P> function, Collection<P> values) { super.in(function, values); return self(); }
	public <T, P> Q inIfNotEmpty  			(AttributeGetter<T, P> function, Collection<P> values) { super.inIfNotEmpty(function, values); return self(); }
	public <T, P> Q inIfNotEmptyNullable  	(AttributeGetter<T, P> function, Collection<P> values) { super.inIfNotEmptyNullable(function, values); return self(); }
	public <T, P> Q inOrNull 				(AttributeGetter<T, P> function, Collection<P> values) { super.inOrNull(function, values); return self(); }
	public <T, P> Q inIfNotEmptyOrNull 	(AttributeGetter<T, P> function, Collection<P> values) { super.inIfNotEmptyOrNull(function, values); return self(); }
	public <T, P> Q notIn  				(AttributeGetter<T, P> function, Collection<P> values) { super.notIn(function, values); return self(); }
	public <T, P> Q notInOrNull			(AttributeGetter<T, P> function, Collection<P> values) { super.notInOrNull(function, values); return self(); }
	public <T, P> Q notInIfNotEmptyOrNull	(AttributeGetter<T, P> function, Collection<P> values) { super.notInIfNotEmptyOrNull(function, values); return self(); }


	public <T> Q like 			(AttributeGetter<T, String> function, String value)				{ super.like(function, value);  return self(); }
	public <T> Q likeIn 		(AttributeGetter<T, String> function, Collection<String> value)	{ super.likeIn(function, value);  return self(); }
	public <T> Q notLike		(AttributeGetter<T, String> function, String value)				{ super.notLike(function, value);  return self(); }
	public <T> Q notLikeIn	(AttributeGetter<T, String> function, Collection<String> value)	{ super.notLikeIn(function, value);  return self(); }
	
	
	public <T> Q likeStartsWith 	(AttributeGetter<T, String> function, String value)	{ super.likeStartsWith(function, value);  return self(); }
	public <T> Q likeStartsWithIn 	(AttributeGetter<T, String> function, Collection<String> value)	{ super.likeStartsWithIn(function, value);  return self(); }
	public <T> Q notLikeStartsWith 	(AttributeGetter<T, String> function, String value)	{ super.notLikeStartsWith(function, value);  return self(); }
	
	public <T> Q likeEndsWith 		(AttributeGetter<T, String> function, String value)	{ super.likeEndsWith(function, value);  return self(); }
	public <T> Q notLikeEndsWith 	(AttributeGetter<T, String> function, String value)	{ super.notLikeEndsWith(function, value);  return self(); }
	
	public <T> Q likeContains 		(AttributeGetter<T, String> function, String value)	{ super.likeContains(function, value);  return self(); }
	public <T> Q notLikeContains 	(AttributeGetter<T, String> function, String value)	{ super.notLikeContains(function, value);  return self(); }

	public <T> Q likeEndsWithIn 	(AttributeGetter<T, String> function, Collection<String> value)	{ super.likeEndsWithIn(function, value);  return self(); }
	public <T> Q likeContainsIn 	(AttributeGetter<T, String> function, Collection<String> value)	{ super.likeContainsIn(function, value);  return self(); }
	public <T> Q notLikeStartsWithIn(AttributeGetter<T, String> function, Collection<String> value)	{ super.notLikeStartsWithIn(function, value);  return self(); }
	public <T> Q notLikeEndsWithIn 	(AttributeGetter<T, String> function, Collection<String> value)	{ super.notLikeEndsWithIn(function, value);  return self(); }
	public <T> Q notLikeContainsIn 	(AttributeGetter<T, String> function, Collection<String> value)	{ super.notLikeContainsIn(function, value);  return self(); }

	public <T> Q nativeCriterion 	(AttributeGetter<T, String> function, String criterion) { super.nativeCriterion(function, criterion); return self(); }
	
	public <T> Q in			(AttributeGetter<T, ?> function, SelectQuery<?> subQuery){ super.in(function, subQuery); return self(); }
	public <T> Q notIn		(AttributeGetter<T, ?> function, SelectQuery<?> subQuery){ super.notIn(function, subQuery); return self(); }
	
	
	public <T, P> Q notInIfNotEmpty   	(AttributeGetter<T, P> function, Collection<P> values) { super.notInIfNotEmpty(function, values); return self(); }
	public <T, P> Q inOrFalseIfEmpty	(AttributeGetter<T, P> function, Collection<P> values) { super.inOrFalseIfEmpty(function, values); return self(); }
	public <T, P> Q notInOrTrueIfEmpty	(AttributeGetter<T, P> function, Collection<P> values) { super.notInOrTrueIfEmpty(function, values); return self(); }
	
	
	public <T> Q likeIfNotNull 				(AttributeGetter<T, String> function, String value)        	{ super.likeIfNotNull (function, value);  return self(); }
	public <T> Q likeStartsWithIfNotNull 	(AttributeGetter<T, String> function, String value)        	{ super.likeStartsWithIfNotNull (function, value);  return self(); }
	public <T> Q likeEndsWithIfNotNull 		(AttributeGetter<T, String> function, String value)        	{ super.likeEndsWithIfNotNull (function, value);  return self(); }
	public <T> Q likeContainsIfNotNull 		(AttributeGetter<T, String> function, String value)        	{ super.likeContainsIfNotNull (function, value);  return self(); }

	public <T> Q likeInIfNotEmpty 			(AttributeGetter<T, String> function, Collection<String> values) { super.likeInIfNotEmpty(function, values); return self(); }
	public <T> Q likeStartsWithInIfNotEmpty (AttributeGetter<T, String> function, Collection<String> values) { super.likeStartsWithInIfNotEmpty(function, values); return self(); }
	public <T> Q likeEndsWithInIfNotEmpty	(AttributeGetter<T, String> function, Collection<String> values) { super.likeEndsWithInIfNotEmpty(function, values); return self(); }
	public <T> Q likeContainsInIfNotEmpty	(AttributeGetter<T, String> function, Collection<String> values) { super.likeContainsInIfNotEmpty(function, values); return self(); }
	
	public <T> Q notLikeIfNotNull 			(AttributeGetter<T, String> function, String value)        	{ super.notLikeIfNotNull (function, value);  return self(); }
	public <T> Q notLikeStartsWithIfNotNull (AttributeGetter<T, String> function, String value)        	{ super.notLikeStartsWithIfNotNull (function, value);  return self(); }
	public <T> Q notLikeEndsWithIfNotNull 	(AttributeGetter<T, String> function, String value)        	{ super.notLikeEndsWithIfNotNull (function, value);  return self(); }
	public <T> Q notLikeContainsIfNotNull 	(AttributeGetter<T, String> function, String value)        	{ super.notLikeContainsIfNotNull (function, value);  return self(); }

	public <T> Q matchAgainst(AttributeGetter<T, String> function, String value) { super.matchAgainst(function, value); return self(); }
	public <T> Q matchAgainstIfNotNull(AttributeGetter<T, String> function, String value) { super.matchAgainstIfNotNull(function, value); return self(); }
	public <T> Q notMatchAgainst(AttributeGetter<T, String> function, String value) { super.notMatchAgainst(function, value); return self(); }
	public <T> Q notMatchAgainstIfNotNull(AttributeGetter<T, String> function, String value) { super.notMatchAgainstIfNotNull(function, value); return self(); }


	public <T> Q matchAgainstIn(AttributeGetter<T, String> function, Collection<String> values) { super.matchAgainstIn(function, values); return self(); }
	public <T> Q matchAgainstInIfNotEmpty(AttributeGetter<T, String> function, Collection<String> values) { super.matchAgainstInIfNotEmpty(function, values); return self(); }
	public <T> Q notMatchAgainstIn(AttributeGetter<T, String> function, Collection<String> values) { super.notMatchAgainstIn(function, values); return self(); }
	public <T> Q notMatchAgainstInIfNotEmpty(AttributeGetter<T, String> function, Collection<String> values) { super.notMatchAgainstInIfNotEmpty(function, values); return self(); }
	
	public <T> Q isNotNull	(AttributeGetter<T, ?> function) 				{ super.isNotNull(function); return self();}
	public <T> Q isNull		(AttributeGetter<T, ?> function) 				{ super.isNull(function); return self();}

	
	

	// Misc methods
	
	public Q importCriterionFromQueryMapping(Object object) {
		if (object == null) {
			return self();
		}
		
		Class<?> objectClass = object.getClass();
		Field[] fields = objectClass.getDeclaredFields();
		
		for (Field field : fields) {
			field.setAccessible(true);
			
			try {
				Object value = field.get(object);
				processFieldAnnotations(field, value);
			} catch (IllegalAccessException e) {
				// Skip fields that cannot be accessed
				continue;
			}
		}
		
		return self();
	}
	
	private Q processFieldAnnotations(Field field, Object value) {
		// Handle @Eq annotation
		QueryMapping.Eq eq = field.getAnnotation(QueryMapping.Eq.class);
		if (eq != null) {
			String attribute = (eq.attribute() != null) ? eq.attribute() : field.getName();
			return this.eq(eq.entity() == void.class ? this.entity.entity : eq.entity(), attribute, value);
		}
		
		// Handle @EqIfNotNull annotation
		QueryMapping.EqIfNotNull eqIfNotNull = field.getAnnotation(QueryMapping.EqIfNotNull.class);
		if (eqIfNotNull != null) {
			String attribute = (eqIfNotNull.attribute() != null) ? eqIfNotNull.attribute() : field.getName();
			return this.eqIfNotNull(eqIfNotNull.entity() == void.class ? this.entity.entity : eqIfNotNull.entity(), attribute, value);
		}
		
		// Handle @EqNullable annotation
		QueryMapping.EqNullable eqNullable = field.getAnnotation(QueryMapping.EqNullable.class);
		if (eqNullable != null) {
			String attribute = (eqNullable.attribute() != null) ? eqNullable.attribute() : field.getName();
			return this.eqNullable(eqNullable.entity() == void.class ? this.entity.entity : eqNullable.entity(), attribute, value);
		}
		
		// Handle @NotEq annotation
		QueryMapping.NotEq notEq = field.getAnnotation(QueryMapping.NotEq.class);
		if (notEq != null) {
			String attribute = (notEq.attribute() != null) ? notEq.attribute() : field.getName();
			return this.notEq(notEq.entity() == void.class ? this.entity.entity : notEq.entity(), attribute, value);
		}
		
		// Handle @NotEqIfNotNull annotation
		QueryMapping.NotEqIfNotNull notEqIfNotNull = field.getAnnotation(QueryMapping.NotEqIfNotNull.class);
		if (notEqIfNotNull != null) {
			String attribute = (notEqIfNotNull.attribute() != null) ? notEqIfNotNull.attribute() : field.getName();
			return this.notEqIfNotNull(notEqIfNotNull.entity() == void.class ? this.entity.entity : notEqIfNotNull.entity(), attribute, value);
		}
		
		// Handle @NotEqNullable annotation
		QueryMapping.NotEqNullable notEqNullable = field.getAnnotation(QueryMapping.NotEqNullable.class);
		if (notEqNullable != null) {
			String attribute = (notEqNullable.attribute() != null) ? notEqNullable.attribute() : field.getName();
			return this.notEqNullable(notEqNullable.entity() == void.class ? this.entity.entity : notEqNullable.entity(), attribute, value);
		}
		
		// Handle @Lt annotation
		QueryMapping.Lt lt = field.getAnnotation(QueryMapping.Lt.class);
		if (lt != null) {
			String attribute = (lt.attribute() != null) ? lt.attribute() : field.getName();
			return this.lt(lt.entity() == void.class ? this.entity.entity : lt.entity(), attribute, value);
		}
		
		// Handle @LtIfNotNull annotation
		QueryMapping.LtIfNotNull ltIfNotNull = field.getAnnotation(QueryMapping.LtIfNotNull.class);
		if (ltIfNotNull != null) {
			String attribute = (ltIfNotNull.attribute() != null) ? ltIfNotNull.attribute() : field.getName();
			return this.ltIfNotNull(ltIfNotNull.entity() == void.class ? this.entity.entity : ltIfNotNull.entity(), attribute, value);
		}
		
		// Handle @Lte annotation
		QueryMapping.Lte lte = field.getAnnotation(QueryMapping.Lte.class);
		if (lte != null) {
			String attribute = (lte.attribute() != null) ? lte.attribute() : field.getName();
			return this.lte(lte.entity() == void.class ? this.entity.entity : lte.entity(), attribute, value);
		}
		
		// Handle @LteIfNotNull annotation
		QueryMapping.LteIfNotNull lteIfNotNull = field.getAnnotation(QueryMapping.LteIfNotNull.class);
		if (lteIfNotNull != null) {
			String attribute = (lteIfNotNull.attribute() != null) ? lteIfNotNull.attribute() : field.getName();
			return this.lteIfNotNull(lteIfNotNull.entity() == void.class ? this.entity.entity : lteIfNotNull.entity(), attribute, value);
		}
		
		// Handle @Gte annotation
		QueryMapping.Gt gt = field.getAnnotation(QueryMapping.Gt.class);
		if (gt != null) {
			String attribute = (gt.attribute() != null) ? gt.attribute() : field.getName();
			return this.gt(gt.entity() == void.class ? this.entity.entity : gt.entity(), attribute, value);
		}
		
		// Handle @GteIfNotNull annotation
		QueryMapping.GtIfNotNull gtIfNotNull = field.getAnnotation(QueryMapping.GtIfNotNull.class);
		if (gtIfNotNull != null) {
			String attribute = (gtIfNotNull.attribute() != null) ? gtIfNotNull.attribute() : field.getName();
			return this.gtIfNotNull(gtIfNotNull.entity() == void.class ? this.entity.entity : gtIfNotNull.entity(), attribute, value);
		}
		
		// Handle @Gte annotation
		QueryMapping.Gte gte = field.getAnnotation(QueryMapping.Gte.class);
		if (gte != null) {
			String attribute = (gte.attribute() != null) ? gte.attribute() : field.getName();
			return this.gte(gte.entity() == void.class ? this.entity.entity : gte.entity(), attribute, value);
		}
		
		// Handle @GteIfNotNull annotation
		QueryMapping.GteIfNotNull gteIfNotNull = field.getAnnotation(QueryMapping.GteIfNotNull.class);
		if (gteIfNotNull != null) {
			String attribute = (gteIfNotNull.attribute() != null) ? gteIfNotNull.attribute() : field.getName();
			return this.gteIfNotNull(gteIfNotNull.entity() == void.class ? this.entity.entity : gteIfNotNull.entity(), attribute, value);
		}
	
		// Handle @Like annotation
		QueryMapping.Like like = field.getAnnotation(QueryMapping.Like.class);
		if (like != null && value instanceof String) {
			String attribute = (like.attribute() != null) ? like.attribute() : field.getName();
			return this.like(like.entity() == void.class ? this.entity.entity : like.entity(), attribute, (String) value);
		}
		
		// Handle @LikeIfNotNull annotation
		QueryMapping.LikeIfNotNull likeIfNotNull = field.getAnnotation(QueryMapping.LikeIfNotNull.class);
		if (likeIfNotNull != null && value instanceof String) {
			String attribute = (likeIfNotNull.attribute() != null) ? likeIfNotNull.attribute() : field.getName();
			return this.likeIfNotNull(likeIfNotNull.entity() == void.class ? this.entity.entity : likeIfNotNull.entity(), attribute, (String) value);
		}
		
		// Handle @LikeContains annotation
		QueryMapping.LikeContains likeContains = field.getAnnotation(QueryMapping.LikeContains.class);
		if (likeContains != null && value instanceof String) {
			String attribute = (likeContains.attribute() != null) ? likeContains.attribute() : field.getName();
			return this.likeContains(likeContains.entity() == void.class ? this.entity.entity : likeContains.entity(), attribute, (String) value);
		}
		
		// Handle @LikeContainsIfNotNull annotation
		QueryMapping.LikeContainsIfNotNull likeContainsIfNotNull = field.getAnnotation(QueryMapping.LikeContainsIfNotNull.class);
		if (likeContainsIfNotNull != null && value instanceof String) {
			String attribute = (likeContainsIfNotNull.attribute() != null) ? likeContainsIfNotNull.attribute() : field.getName();
			return this.likeContainsIfNotNull(likeContainsIfNotNull.entity() == void.class ? this.entity.entity : likeContainsIfNotNull.entity(), attribute, (String) value);
		}
		
		// Handle @LikeStartsWith annotation
		QueryMapping.LikeStartsWith likeStartsWith = field.getAnnotation(QueryMapping.LikeStartsWith.class);
		if (likeStartsWith != null && value instanceof String) {
			String attribute = (likeStartsWith.attribute() != null) ? likeStartsWith.attribute() : field.getName();
			return this.likeStartsWith(likeStartsWith.entity() == void.class ? this.entity.entity : likeStartsWith.entity(), attribute, (String) value);
		}
		
		// Handle @LikeStartsWithIfNotNull annotation
		QueryMapping.LikeStartsWithIfNotNull likeStartsWithIfNotNull = field.getAnnotation(QueryMapping.LikeStartsWithIfNotNull.class);
		if (likeStartsWithIfNotNull != null && value instanceof String) {
			String attribute = (likeStartsWithIfNotNull.attribute() != null) ? likeStartsWithIfNotNull.attribute() : field.getName();
			return this.likeStartsWithIfNotNull(likeStartsWithIfNotNull.entity() == void.class ? this.entity.entity : likeStartsWithIfNotNull.entity(), attribute, (String) value);
		}
	
		// Handle @In annotation
		QueryMapping.In in = field.getAnnotation(QueryMapping.In.class);
		if (in != null && value instanceof Collection) {
			String attribute = (in.attribute() != null) ? in.attribute() : field.getName();
			return this.in(in.entity() == void.class ? this.entity.entity : in.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @InIfNotEmpty annotation
		QueryMapping.InIfNotEmpty inIfNotEmpty = field.getAnnotation(QueryMapping.InIfNotEmpty.class);
		if (inIfNotEmpty != null && value instanceof Collection) {
			String attribute = (inIfNotEmpty.attribute() != null) ? inIfNotEmpty.attribute() : field.getName();
			return this.inIfNotEmpty(inIfNotEmpty.entity() == void.class ? this.entity.entity : inIfNotEmpty.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @InOrFalseIfEmpty annotation
		QueryMapping.InOrFalseIfEmpty inOrFalseIfEmpty = field.getAnnotation(QueryMapping.InOrFalseIfEmpty.class);
		if (inOrFalseIfEmpty != null && value instanceof Collection) {
			String attribute = (inOrFalseIfEmpty.attribute() != null) ? inOrFalseIfEmpty.attribute() : field.getName();
			return this.inOrFalseIfEmpty(inOrFalseIfEmpty.entity() == void.class ? this.entity.entity : inOrFalseIfEmpty.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @InOrNull annotation
		QueryMapping.InOrNull inOrNull = field.getAnnotation(QueryMapping.InOrNull.class);
		if (inOrNull != null && value instanceof Collection) {
			String attribute = (inOrNull.attribute() != null) ? inOrNull.attribute() : field.getName();
			return this.inOrNull(inOrNull.entity() == void.class ? this.entity.entity : inOrNull.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @NotIn annotation
		QueryMapping.NotIn notIn = field.getAnnotation(QueryMapping.NotIn.class);
		if (notIn != null && value instanceof Collection) {
			String attribute = (notIn.attribute() != null) ? notIn.attribute() : field.getName();
			return this.notIn(notIn.entity() == void.class ? this.entity.entity : notIn.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @NotInIfNotEmpty annotation
		QueryMapping.NotInIfNotEmpty notInIfNotEmpty = field.getAnnotation(QueryMapping.NotInIfNotEmpty.class);
		if (notInIfNotEmpty != null && value instanceof Collection) {
			String attribute = (notInIfNotEmpty.attribute() != null) ? notInIfNotEmpty.attribute() : field.getName();
			return this.notInIfNotEmpty(notInIfNotEmpty.entity() == void.class ? this.entity.entity : notInIfNotEmpty.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @NotInOrTrueIfEmpty annotation
		QueryMapping.NotInOrTrueIfEmpty notInOrTrueIfEmpty = field.getAnnotation(QueryMapping.NotInOrTrueIfEmpty.class);
		if (notInOrTrueIfEmpty != null && value instanceof Collection) {
			String attribute = (notInOrTrueIfEmpty.attribute() != null) ? notInOrTrueIfEmpty.attribute() : field.getName();
			return this.notInOrTrueIfEmpty(notInOrTrueIfEmpty.entity() == void.class ? this.entity.entity : notInOrTrueIfEmpty.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @NotInOrNull annotation
		QueryMapping.NotInOrNull notInOrNull = field.getAnnotation(QueryMapping.NotInOrNull.class);
		if (notInOrNull != null && value instanceof Collection) {
			String attribute = (notInOrNull.attribute() != null) ? notInOrNull.attribute() : field.getName();
			return this.notInOrNull(notInOrNull.entity() == void.class ? this.entity.entity : notInOrNull.entity(), attribute, (Collection<?>) value);
		}
		
		// Handle @LikeInContains annotation
		QueryMapping.LikeInContains likeInContains = field.getAnnotation(QueryMapping.LikeInContains.class);
		if (likeInContains != null && value instanceof Collection) {
			String attribute = (likeInContains.attribute() != null) ? likeInContains.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeContainsIn(likeInContains.entity() == void.class ? this.entity.entity : likeInContains.entity(), attribute, stringValues);
		}
		
		// Handle @LikeInStartsWith annotation
		QueryMapping.LikeInStartsWith likeInStartsWith = field.getAnnotation(QueryMapping.LikeInStartsWith.class);
		if (likeInStartsWith != null && value instanceof Collection) {
			String attribute = (likeInStartsWith.attribute() != null) ? likeInStartsWith.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeStartsWithIn(likeInStartsWith.entity() == void.class ? this.entity.entity : likeInStartsWith.entity(), attribute, stringValues);
		}
		
		// Handle @LikeInEndsWith annotation
		QueryMapping.LikeInEndsWith likeInEndsWith = field.getAnnotation(QueryMapping.LikeInEndsWith.class);
		if (likeInEndsWith != null && value instanceof Collection) {
			String attribute = (likeInEndsWith.attribute() != null) ? likeInEndsWith.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeEndsWithIn(likeInEndsWith.entity() == void.class ? this.entity.entity : likeInEndsWith.entity(), attribute, stringValues);
		}
		
		// Handle @LikeInContains annotation
		QueryMapping.LikeInContainsIfNotEmpty likeInContainsIfNotEmpty = field.getAnnotation(QueryMapping.LikeInContainsIfNotEmpty.class);
		if (likeInContainsIfNotEmpty != null && value instanceof Collection) {
			String attribute = (likeInContainsIfNotEmpty.attribute() != null) ? likeInContainsIfNotEmpty.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeContainsInIfNotEmpty(likeInContainsIfNotEmpty.entity() == void.class ? this.entity.entity : likeInContainsIfNotEmpty.entity(), attribute, stringValues);
		}
		
		// Handle @LikeInStartsWith annotation
		QueryMapping.LikeInStartsWithIfNotEmpty likeInStartsWithIfNotEmpty = field.getAnnotation(QueryMapping.LikeInStartsWithIfNotEmpty.class);
		if (likeInStartsWithIfNotEmpty != null && value instanceof Collection) {
			String attribute = (likeInStartsWithIfNotEmpty.attribute() != null) ? likeInStartsWithIfNotEmpty.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeStartsWithIn(likeInStartsWithIfNotEmpty.entity() == void.class ? this.entity.entity : likeInStartsWithIfNotEmpty.entity(), attribute, stringValues);
		}
		
		// Handle @LikeInEndsWith annotation
		QueryMapping.LikeInEndsWithIfNotEmpty likeInEndsWithIfNotEmpty = field.getAnnotation(QueryMapping.LikeInEndsWithIfNotEmpty.class);
		if (likeInEndsWithIfNotEmpty != null && value instanceof Collection) {
			String attribute = (likeInEndsWithIfNotEmpty.attribute() != null) ? likeInEndsWithIfNotEmpty.attribute() : field.getName();
			Collection<String> stringValues = (Collection<String>) value;
			return this.likeEndsWithIn(likeInEndsWithIfNotEmpty.entity() == void.class ? this.entity.entity : likeInEndsWithIfNotEmpty.entity(), attribute, stringValues);
		}
		
		return self();
	}
	
	/* Order */

	@SuperBuilder
	@lombok.Data
	@lombok.AllArgsConstructor
	public static class Order extends Attribute{
		public Direction direction;
		public enum Direction{
			Asc,
			Desc;
		}
	}
	
	
	public Q orderBy(Order order) { orders.add(order); return self(); }
	public Q orderBy(Entity<?> entity, String attribute, Function function, Order.Direction direction) {
		orders.add(Order.builder().entity(entity).attribute(attribute).direction(direction).function(function).build()); 
		return self(); 
	}

	public Q orderBy(Class<?> entity, String attribute, Function function, Order.Direction direction) 	{ return orderBy(new Entity<>(entity), attribute, function, direction); }
	public Q orderBy(String attribute, Function function, Order.Direction direction) 					{ return orderBy(this.entity, attribute, function, direction); }
	public Q orderBy(Entity<?> entity, String attribute, Order.Direction direction) 					{ return orderBy(entity, attribute, null, direction); }
	public Q orderBy(Class<?> entity, String attribute, Order.Direction direction) 						{ return orderBy(new Entity<>(entity), attribute, null, direction); }
	public Q orderBy(String attribute, Order.Direction direction) 										{ return orderBy(this.entity, attribute, null, direction); }
	public Q orderBy(Attribute attribute, Order.Direction direction) 									{ return orderBy(attribute.entity, attribute.attribute, attribute.function, direction); }
	public Q orderBy(Attribute attribute, Function function, Order.Direction direction) 				{ return orderBy(attribute.entity, attribute.attribute, function, direction); }
	public <T, P> Q orderBy(AttributeGetter<T, P> getter, Order.Direction direction) 							{ return orderBy(Estivate.attribute(getter), direction); }
	public <T, P> Q orderBy(AttributeGetter<T, P> getter, Function function, Order.Direction direction) 		{ return orderBy(Estivate.attribute(getter), function, direction); }
	public Q orderByAlias(String alias, Order.Direction direction) 										{ orders.add(Order.builder().attribute(alias).direction(direction).build()); return self(); }

	public Q orderByAsc(Entity<?> entity, String attribute) 					{ return orderBy(entity, attribute, Order.Direction.Asc); }
	public Q orderByAsc(Entity<?> entity, String attribute, Function function) 	{ return orderBy(entity, attribute, function, Order.Direction.Asc); }
	public Q orderByAsc(Class<?> entity, String attribute) 						{ return orderBy(entity, attribute, Order.Direction.Asc); }
	public Q orderByAsc(Class<?> entity, String attribute, Function function) 	{ return orderBy(entity, attribute, function, Order.Direction.Asc); }
	public Q orderByAsc(String attribute) 										{ return orderBy(this.entity, attribute, Order.Direction.Asc); }
	public Q orderByAsc(String attribute, Function function) 					{ return orderBy(this.entity, attribute, function, Order.Direction.Asc); }
	public Q orderByAsc(Attribute attribute) 									{ return orderBy(attribute.entity, attribute.attribute, attribute.function, Order.Direction.Asc); }
	public <T, P> Q orderByAsc(AttributeGetter<T, P> getter) 					{ return orderBy(Estivate.attribute(getter), Order.Direction.Asc); }
	public <T, P> Q orderByAsc(AttributeGetter<T, P> getter, Function function) { return orderBy(Estivate.attribute(getter), function, Order.Direction.Asc); }
	public Q orderByAscAlias(String alias)										{ return orderByAlias(alias, Order.Direction.Asc); }
	
	public Q orderByDesc(Entity<?> entity, String attribute) 					{ return orderBy(entity, attribute, Order.Direction.Desc); }
	public Q orderByDesc(Entity<?> entity, String attribute, Function function)	{ return orderBy(entity, attribute, function, Order.Direction.Desc); }
	public Q orderByDesc(Class<?> entity, String attribute) 					{ return orderBy(entity, attribute, Order.Direction.Desc); }
	public Q orderByDesc(Class<?> entity, String attribute, Function function) 	{ return orderBy(entity, attribute, function, Order.Direction.Desc); }
	public Q orderByDesc(String attribute) 										{ return orderBy(this.entity, attribute, Order.Direction.Desc); }
	public Q orderByDesc(String attribute, Function function) 					{ return orderBy(this.entity, attribute, function, Order.Direction.Desc); }
	public Q orderByDesc(Attribute attribute) 									{ return orderBy(attribute.entity, attribute.attribute, attribute.function, Order.Direction.Desc); }
	public <T, P> Q orderByDesc(AttributeGetter<T, P> getter) 					{ return orderBy(Estivate.attribute(getter), Order.Direction.Desc); }
	public <T, P> Q orderByDesc(AttributeGetter<T, P> getter, Function function){ return orderBy(Estivate.attribute(getter), function, Order.Direction.Desc); }
	public Q orderByDescAlias(String alias)										{ return orderByAlias(alias, Order.Direction.Desc); }
	
	
	public Q clearOrderBys(){
		orders.clear();
		return self();
	}


	/* Limit & Offset */
	
	public Q limit(Integer limit) { this.limit = limit; return self(); }
	public Q limitIfNotNull(Integer limit) { if(limit != null) { this.limit = limit; } return self(); }
	public Q limitIfNotNullOr(Integer limit, Integer fallbackLimit) { if(limit != null) { this.limit = limit; } else { this.limit = fallbackLimit; } return self(); }
	public Q offset(Integer offset) { this.offset = offset; return self();}
	public Q offsetIfNotNull(Integer offset) { if(offset != null) { this.offset = offset; } return self(); }
	public Q offsetIfNotNullOr(Integer offset, Integer fallbackOffset) { if(offset != null) { this.offset = offset; } else { this.offset = fallbackOffset; } return self(); }

	

	
	
	public void pruneUnusedJoins(){

        List<Attribute> attributes = listNodeAttributes(this);
        
        if(this instanceof SelectQuery) {
        	attributes.addAll(listNodeAttributes(((SelectQuery<?>) this).getHaving()));
        }
            

        List<Join> joinsToRemove = new ArrayList<>();
        do {
        	
        	joinsToRemove = new ArrayList<>();
        	
	        for(Join join : getJoins()){

	        	// if any field of joined entity in where, it is used
	            if(attributes.stream().anyMatch(attribute -> attribute.getEntity().equals(join.rightEntity))){
	                continue;
	            }
	            
	            // if any field of joined entity in order by, it is used
	            if(getOrders().stream().anyMatch(order -> order.entity.equals(join.rightEntity))){
	                continue;
	            }

	            // if any other join is using this join, it is used
				if(getJoins().stream().anyMatch(otherJoin -> otherJoin.leftEntity == join.rightEntity)){
					continue;
				}
			
	            // if any field of joined entity in select, it is used
	        	if(this instanceof SelectQuery) {
		            if(((SelectQuery<?>) this).getSelects().stream().anyMatch(select -> select.getEntity().equals(join.rightEntity))){
		                continue;
		            }
		            // if any field of joined entity in group by, it is used
		            if(((SelectQuery<?>) this).getGroupBys().stream().anyMatch(groupBy -> groupBy.entity.equals(join.rightEntity))){
		                continue;
		            }
	        	}

	            // collect join to remove
	            joinsToRemove.add(join);
	        }
	        
	        // remove collected joins outside the iteration
	        if(!joinsToRemove.isEmpty()) {
	        	getJoins().removeAll(joinsToRemove);
	        }
	        
        }
		while(!joinsToRemove.isEmpty());

 
    }

    public static List<Attribute> listNodeAttributes(EstivateNode node){

        List<Attribute> attributes = new ArrayList<>();

        if(node instanceof Aggregator) {
            for(EstivateNode criterion : ((Aggregator)node).getCriterions()) {
                attributes.addAll(listNodeAttributes(criterion));
        
            }
        }
        else if(node instanceof Criterion) {
            attributes.add(((Criterion)node).attribute);
        }

        return attributes;
    
    }
	

}
