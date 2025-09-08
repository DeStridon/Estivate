package com.estivate.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;

import lombok.Getter;

@Getter
public class Aggregator implements EstivateNode {
	
	final GroupType groupType;
	
	List<EstivateNode> criterions = new ArrayList<>();
	
	public static enum GroupType{AND, OR}
	
	public Aggregator(GroupType groupType) {
		this.groupType = groupType;
	}
	
	public Aggregator(GroupType groupType, List<EstivateNode> criterions) {
		this.groupType = groupType;
		this.criterions = new ArrayList<>(criterions.stream().filter(x -> x != null).collect(Collectors.toList()));
	}
	
	

	public Aggregator eq    		(Attribute attribute, Object value) { add(Estivate.eq(attribute, value)); return this; }
	public Aggregator eqIfNotNull   (Attribute attribute, Object value) { add(Estivate.eqIfNotNull(attribute, value)); return this; }
	public Aggregator eqNullable    (Attribute attribute, Object value) { add(Estivate.eqNullable(attribute, value)); return this; }
	public Aggregator eqOrNull		(Attribute attribute, Object value) { add(Estivate.eqOrNull(attribute, value)); return this; }
	
	public Aggregator notEq 		(Attribute attribute, Object value) { add(Estivate.notEq(attribute, value)); return this; }
	public Aggregator notEqIfNotNull(Attribute attribute, Object value) { add(Estivate.notEqIfNotNull(attribute, value)); return this; }
	public Aggregator notEqNullable (Attribute attribute, Object value) { add(Estivate.notEqNullable(attribute, value)); return this; }
	public Aggregator notEqOrNull	(Attribute attribute, Object value) { add(Estivate.notEqOrNull(attribute, value)); return this; }
	
	public Aggregator lt    	(Attribute attribute, Object value)     		{ add(Estivate.lt(attribute, value)); return this; }
	public Aggregator ltIfNotNull   	(Attribute attribute, Object value) 	{ add(Estivate.ltIfNotNull(attribute, value)); return this; }
	public Aggregator ltOrNull(Attribute attribute, Object value) 			{ add(Estivate.ltOrNull(attribute, value)); return this; }

	public Aggregator lte   	(Attribute attribute, Object value)     		{ add(Estivate.lte(attribute, value)); return this; }
	public Aggregator lteIfNotNull  	(Attribute attribute, Object value) 	{ add(Estivate.lteIfNotNull(attribute, value)); return this; }
	public Aggregator lteOrNull(Attribute attribute, Object value) 			{ add(Estivate.lteOrNull(attribute, value)); return this; }
	
	public Aggregator gt    	(Attribute attribute, Object value)     		{ add(Estivate.gt(attribute, value)); return this; }
	public Aggregator gtIfNotNull   	(Attribute attribute, Object value) 	{ add(Estivate.gtIfNotNull(attribute, value)); return this; }
	public Aggregator gtOrNull(Attribute attribute, Object value) 			{ add(Estivate.gtOrNull(attribute, value)); return this; }
	
	public Aggregator gte   	(Attribute attribute, Object value)     		{ add(Estivate.gte(attribute, value)); return this; }
	public Aggregator gteIfNotNull  	(Attribute attribute, Object value) 	{ add(Estivate.gteIfNotNull(attribute, value)); return this; }
	public Aggregator gteOrNull(Attribute attribute, Object value) 			{ add(Estivate.gteOrNull(attribute, value)); return this; }

	public Aggregator between 	(Attribute attribute, Object min, Object max)		{ add(Estivate.between(attribute, min, max)); return this; }
	public Aggregator betweenIfNotNull	(Attribute attribute, Object min, Object max){ add(Estivate.betweenIfNotNull(attribute, min, max)); return this; }
	public Aggregator betweenOrNull	(Attribute attribute, Object min, Object max) 	{ add(Estivate.betweenOrNull(attribute, min, max)); return this; }
	
	
	public Aggregator in    	(Attribute attribute, Collection<?> values) 		 	{ add(Estivate.in(attribute, values)); return this; }
	public Aggregator inIfNotEmptyNullable(Attribute attribute, Collection<?> values)	{ add(Estivate.inIfNotEmptyNullable(attribute, values)); return this; }
	public Aggregator inOrNull(Attribute attribute, Collection<?> values)			 	{ add(Estivate.inOrNull(attribute, values)); return this; }
	public Aggregator inIfNotEmptyOrNull(Attribute attribute, Collection<?> values)	 	{ add(Estivate.inIfNotEmptyOrNull(attribute, values)); return this; }
	public Aggregator notIn    	(Attribute attribute, Collection<?> values) 		 	{ add(Estivate.notIn(attribute, values)); return this; }
	public Aggregator notInOrNull (Attribute attribute, Collection<?> values) 		 	{ add(Estivate.notInOrNull(attribute, values)); return this; }
	public Aggregator notInIfNotEmptyOrNull (Attribute attribute, Collection<?> values) { add(Estivate.notInIfNotEmptyOrNull(attribute, values)); return this; }

	public Aggregator like				(Attribute attribute, String value)     { add(Estivate.like(attribute, value)); return this; }
	public Aggregator likeStartsWith	(Attribute attribute, String value)     { add(Estivate.likeStartsWith(attribute, value)); return this; }
	public Aggregator likeEndsWith		(Attribute attribute, String value)     { add(Estivate.likeEndsWith(attribute, value)); return this; }
	public Aggregator likeContains		(Attribute attribute, String value)     { add(Estivate.likeContains(attribute, value)); return this; }
	public Aggregator notLike			(Attribute attribute, String value)     { add(Estivate.notLike(attribute, value)); return this; }
	public Aggregator notLikeStartsWith	(Attribute attribute, String value)     { add(Estivate.notLikeStartsWith(attribute, value)); return this; }
	public Aggregator notLikeEndsWith	(Attribute attribute, String value)     { add(Estivate.notLikeEndsWith(attribute, value)); return this; }
	public Aggregator notLikeContains	(Attribute attribute, String value)     { add(Estivate.notLikeContains(attribute, value)); return this; }

	public Aggregator likeIn			(Attribute attribute, Collection<String> value) { add(Estivate.likeIn(attribute, value)); return this; }
	public Aggregator likeStartsWithIn	(Attribute attribute, Collection<String> value) { add(Estivate.likeStartsWithIn(attribute, value)); return this; }
	public Aggregator likeEndsWithIn	(Attribute attribute, Collection<String> value) { add(Estivate.likeEndsWithIn(attribute, value)); return this; }
	public Aggregator likeContainsIn	(Attribute attribute, Collection<String> value) { add(Estivate.likeContainsIn(attribute, value)); return this; }
	public Aggregator notLikeIn			(Attribute attribute, Collection<String> value) { add(Estivate.notLikeIn(attribute, value)); return this; }
	public Aggregator notLikeStartsWithIn(Attribute attribute, Collection<String> value){ add(Estivate.notLikeStartsWithIn(attribute, value)); return this; }
	public Aggregator notLikeEndsWithIn	(Attribute attribute, Collection<String> value) { add(Estivate.notLikeEndsWithIn(attribute, value)); return this; }
	public Aggregator notLikeContainsIn	(Attribute attribute, Collection<String> value) { add(Estivate.notLikeContainsIn(attribute, value)); return this; }

	
	

	public Aggregator inIfNotEmpty   	(Attribute attribute, Collection<?> values) { add(Estivate.inIfNotEmpty(attribute, values)); return this; }
	public Aggregator notInIfNotEmpty	(Attribute attribute, Collection<?> values) { add(Estivate.notInIfNotEmpty(attribute, values)); return this; }
	public Aggregator inOrFalseIfEmpty	(Attribute attribute, Collection<?> values) { add(Estivate.inOrFalseIfEmpty(attribute, values)); return this; }
	public Aggregator notInOrTrueIfEmpty(Attribute attribute, Collection<?> values) { add(Estivate.notInOrTrueIfEmpty(attribute, values)); return this; }
	
	
	public Aggregator likeIfNotNull 			(Attribute attribute, String value) { add(Estivate.likeIfNotNull(attribute, value)); return this; }
	public Aggregator likeStartsWithIfNotNull 	(Attribute attribute, String value) { add(Estivate.likeStartsWithIfNotNull(attribute, value)); return this; }
	public Aggregator likeEndsWithIfNotNull 	(Attribute attribute, String value) { add(Estivate.likeEndsWithIfNotNull(attribute, value)); return this; }
	public Aggregator likeContainsIfNotNull 	(Attribute attribute, String value) { add(Estivate.likeContainsIfNotNull(attribute, value)); return this; }

	
	public Aggregator likeInIfNotEmpty 			(Attribute attribute, Collection<String> values) { add(Estivate.likeInIfNotEmpty(attribute, values)); return this; }
	public Aggregator likeStartsWithInIfNotEmpty(Attribute attribute, Collection<String> values) { add(Estivate.likeStartsWithInIfNotEmpty(attribute, values)); return this; }
	public Aggregator likeEndsWithInIfNotEmpty	(Attribute attribute, Collection<String> values) { add(Estivate.likeEndsWithInIfNotEmpty(attribute, values)); return this; }
	public Aggregator likeContainsInIfNotEmpty	(Attribute attribute, Collection<String> values) { add(Estivate.likeContainsInIfNotEmpty(attribute, values)); return this; }

	
	
	public Aggregator notLikeIfNotNull 			(Attribute attribute, String value) { add(Estivate.notLikeIfNotNull(attribute, value)); return this; }
	public Aggregator notLikeStartsWithIfNotNull(Attribute attribute, String value) { add(Estivate.notLikeStartsWithIfNotNull(attribute, value)); return this; }
	public Aggregator notLikeEndsWithIfNotNull 	(Attribute attribute, String value) { add(Estivate.notLikeEndsWithIfNotNull(attribute, value)); return this; }
	public Aggregator notLikeContainsIfNotNull 	(Attribute attribute, String value) { add(Estivate.notLikeContainsIfNotNull(attribute, value)); return this; }

	public Aggregator isNull(Attribute attribute) 	{ add(Estivate.isNull(attribute)); 	return this; }
	public Aggregator isNotNull(Attribute attribute) { add(Estivate.isNotNull(attribute)); return this; }

	public Aggregator matchAgainst(Attribute attribute, String value) { add(Estivate.matchAgainst(attribute, value)); return this; }
	public Aggregator matchAgainstIfNotNull(Attribute attribute, String value) { add(Estivate.matchAgainstIfNotNull(attribute, value)); return this; }
	public Aggregator notMatchAgainst(Attribute attribute, String value) { add(Estivate.notMatchAgainst(attribute, value)); return this; }
	public Aggregator notMatchAgainstIfNotNull(Attribute attribute, String value) { add(Estivate.notMatchAgainstIfNotNull(attribute, value)); return this; }
	
	public Aggregator matchAgainstIn(Attribute attribute, Collection<String> values) { add(Estivate.matchAgainstIn(attribute, values)); return this; }
	public Aggregator matchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { add(Estivate.matchAgainstInIfNotEmpty(attribute, values)); return this; }
	public Aggregator notMatchAgainstIn(Attribute attribute, Collection<String> values) { add(Estivate.notMatchAgainstIn(attribute, values)); return this; }
	public Aggregator notMatchAgainstInIfNotEmpty(Attribute attribute, Collection<String> values) { add(Estivate.notMatchAgainstInIfNotEmpty(attribute, values)); return this; }
	

	public Aggregator nativeCriterion(Attribute attribute, String criterion) { add(Estivate.nativeCriterion(attribute, criterion)); return this; }


	public Aggregator inSubQuery		(Attribute attribute, SelectQuery<?> subQuery)	{ add(Estivate.inSubQuery(attribute, subQuery)); return this; }
	public Aggregator notInSubQuery		(Attribute attribute, SelectQuery<?> subQuery)	{ add(Estivate.notInSubQuery(attribute, subQuery)); return this; }
	
	
	public Aggregator exists	(SelectQuery<?> subQuery)						{ add(Estivate.existsSubQuery(subQuery)); return this; }
	public Aggregator notExists	(SelectQuery<?> subQuery)						{ add(Estivate.notExistsSubQuery(subQuery)); return this; }
	
	/* Wrapper for Entity */
		
	public Aggregator eq    		(Entity<?> entity, String attribute, Object value) { add(Estivate.eq(entity, attribute, value)); return this; }
	public Aggregator eqIfNotNull   (Entity<?> entity, String attribute, Object value) { add(Estivate.eqIfNotNull(entity, attribute, value)); return this; }
	public Aggregator eqNullable    (Entity<?> entity, String attribute, Object value) { add(Estivate.eqNullable(entity, attribute, value)); return this; }
	public Aggregator eqOrNull		(Entity<?> entity, String attribute, Object value) { add(Estivate.eqOrNull(entity, attribute, value)); return this; }
	
	public Aggregator notEq 		(Entity<?> entity, String attribute, Object value) { add(Estivate.notEq(entity, attribute, value)); return this; }
	public Aggregator notEqIfNotNull(Entity<?> entity, String attribute, Object value) { add(Estivate.notEqIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notEqNullable (Entity<?> entity, String attribute, Object value) { add(Estivate.notEqNullable(entity, attribute, value)); return this; }
	public Aggregator notEqOrNull	(Entity<?> entity, String attribute, Object value) { add(Estivate.notEqOrNull(entity, attribute, value)); return this; }
	
	public Aggregator lt    	(Entity<?> entity, String attribute, Object value)     		{ add(Estivate.lt(entity, attribute, value)); return this; }
	public Aggregator ltIfNotNull   	(Entity<?> entity, String attribute, Object value) 	{ add(Estivate.ltIfNotNull(entity, attribute, value)); return this; }
	public Aggregator ltOrNull(Entity<?> entity, String attribute, Object value) 			{ add(Estivate.ltOrNull(entity, attribute, value)); return this; }

	public Aggregator lte   	(Entity<?> entity, String attribute, Object value)     		{ add(Estivate.lte(entity, attribute, value)); return this; }
	public Aggregator lteIfNotNull  	(Entity<?> entity, String attribute, Object value) 	{ add(Estivate.lteIfNotNull(entity, attribute, value)); return this; }
	public Aggregator lteOrNull(Entity<?> entity, String attribute, Object value) 			{ add(Estivate.lteOrNull(entity, attribute, value)); return this; }
	
	public Aggregator gt    	(Entity<?> entity, String attribute, Object value)     		{ add(Estivate.gt(entity, attribute, value)); return this; }
	public Aggregator gtIfNotNull   	(Entity<?> entity, String attribute, Object value) 	{ add(Estivate.gtIfNotNull(entity, attribute, value)); return this; }
	public Aggregator gtOrNull(Entity<?> entity, String attribute, Object value) 			{ add(Estivate.gtOrNull(entity, attribute, value)); return this; }
	
	public Aggregator gte   	(Entity<?> entity, String attribute, Object value)     		{ add(Estivate.gte(entity, attribute, value)); return this; }
	public Aggregator gteIfNotNull  	(Entity<?> entity, String attribute, Object value) 	{ add(Estivate.gteIfNotNull(entity, attribute, value)); return this; }
	public Aggregator gteOrNull(Entity<?> entity, String attribute, Object value) 			{ add(Estivate.gteOrNull(entity, attribute, value)); return this; }

	public Aggregator between 	(Entity<?> entity, String attribute, Object min, Object max)		{ add(Estivate.between(entity, attribute, min, max)); return this; }
	public Aggregator betweenIfNotNull	(Entity<?> entity, String attribute, Object min, Object max){ add(Estivate.betweenIfNotNull(entity, attribute, min, max)); return this; }
	public Aggregator betweenOrNull	(Entity<?> entity, String attribute, Object min, Object max) 	{ add(Estivate.betweenOrNull(entity, attribute, min, max)); return this; }
	
	
	public Aggregator in    	(Entity<?> entity, String attribute, Collection<?> values) 			{ add(Estivate.in(entity, attribute, values)); return this; }
	public Aggregator inIfNotEmptyNullable(Entity<?> entity, String attribute, Collection<?> values){ add(Estivate.inIfNotEmptyNullable(entity, attribute, values)); return this; }
	public Aggregator inOrNull(Entity<?> entity, String attribute, Collection<?> values){ add(Estivate.inOrNull(entity, attribute, values)); return this; }
	public Aggregator inIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values){ add(Estivate.inIfNotEmptyOrNull(entity, attribute, values)); return this; }
	public Aggregator notIn    	(Entity<?> entity, String attribute, Collection<?> values) 			{ add(Estivate.notIn(entity, attribute, values)); return this; }
	public Aggregator notInOrNull(Entity<?> entity, String attribute, Collection<?> values){ add(Estivate.notInOrNull(entity, attribute, values)); return this; }
	public Aggregator notInIfNotEmptyOrNull(Entity<?> entity, String attribute, Collection<?> values){ add(Estivate.notInIfNotEmptyOrNull(entity, attribute, values)); return this; }
	
	public Aggregator like				(Entity<?> entity, String attribute, String value)     { add(Estivate.like(entity, attribute, value)); return this; }
	public Aggregator likeStartsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.likeStartsWith(entity, attribute, value)); return this; }
	public Aggregator likeEndsWith		(Entity<?> entity, String attribute, String value)     { add(Estivate.likeEndsWith(entity, attribute, value)); return this; }
	public Aggregator likeContains		(Entity<?> entity, String attribute, String value)     { add(Estivate.likeContains(entity, attribute, value)); return this; }
	public Aggregator notLike			(Entity<?> entity, String attribute, String value)     { add(Estivate.notLike(entity, attribute, value)); return this; }
	public Aggregator notLikeStartsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeStartsWith(entity, attribute, value)); return this; }
	public Aggregator notLikeEndsWith	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeEndsWith(entity, attribute, value)); return this; }
	public Aggregator notLikeContains	(Entity<?> entity, String attribute, String value)     { add(Estivate.notLikeContains(entity, attribute, value)); return this; }

	public Aggregator likeIn			(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeIn(entity, attribute, value)); return this; }
	public Aggregator likeStartsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeStartsWithIn(entity, attribute, value)); return this; }
	public Aggregator likeEndsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeEndsWithIn(entity, attribute, value)); return this; }
	public Aggregator likeContainsIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.likeContainsIn(entity, attribute, value)); return this; }
	public Aggregator notLikeIn			(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeIn(entity, attribute, value)); return this; }
	public Aggregator notLikeStartsWithIn(Entity<?> entity, String attribute, Collection<String> value){ add(Estivate.notLikeStartsWithIn(entity, attribute, value)); return this; }
	public Aggregator notLikeEndsWithIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeEndsWithIn(entity, attribute, value)); return this; }
	public Aggregator notLikeContainsIn	(Entity<?> entity, String attribute, Collection<String> value) { add(Estivate.notLikeContainsIn(entity, attribute, value)); return this; }

	
	

	public Aggregator inIfNotEmpty   	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.inIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator notInIfNotEmpty	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.notInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator inOrFalseIfEmpty	(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.inOrFalseIfEmpty(entity, attribute, values)); return this; }
	public Aggregator notInOrTrueIfEmpty(Entity<?> entity, String attribute, Collection<?> values) { add(Estivate.notInOrTrueIfEmpty(entity, attribute, values)); return this; }
	
	
	public Aggregator likeIfNotNull 			(Entity<?> entity, String attribute, String value) { add(Estivate.likeIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeStartsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeStartsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeEndsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator likeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.likeContainsIfNotNull(entity, attribute, value)); return this; }

	
	public Aggregator likeInIfNotEmpty 			(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeStartsWithInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeStartsWithInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeEndsWithInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeEndsWithInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator likeContainsInIfNotEmpty	(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.likeContainsInIfNotEmpty(entity, attribute, values)); return this; }

	
	
	public Aggregator notLikeIfNotNull 			(Entity<?> entity, String attribute, String value) { add(Estivate.notLikeIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notLikeStartsWithIfNotNull(Entity<?> entity, String attribute, String value) { add(Estivate.notLikeStartsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notLikeEndsWithIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.notLikeEndsWithIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notLikeContainsIfNotNull 	(Entity<?> entity, String attribute, String value) { add(Estivate.notLikeContainsIfNotNull(entity, attribute, value)); return this; }

	public Aggregator isNull(Entity<?> entity, String attribute) 	{ add(Estivate.isNull(entity, attribute)); 	return this; }
	public Aggregator isNotNull(Entity<?> entity, String attribute) { add(Estivate.isNotNull(entity, attribute)); return this; }

	public Aggregator matchAgainst(Entity<?> entity, String attribute, String value) { add(Estivate.matchAgainst(entity, attribute, value)); return this; }
	public Aggregator matchAgainst(Entity<?> entity, Collection<String> attributes, String value) { add(Estivate.matchAgainst(entity, attributes, value)); return this; }
	public Aggregator matchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { add(Estivate.matchAgainstIfNotNull(entity, attribute, value)); return this; }
	public Aggregator matchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { add(Estivate.matchAgainstIfNotNull(entity, attributes, value)); return this; }
	public Aggregator notMatchAgainst(Entity<?> entity, String attribute, String value) { add(Estivate.notMatchAgainst(entity, attribute, value)); return this; }
	public Aggregator notMatchAgainst(Entity<?> entity, Collection<String> attributes, String value) { add(Estivate.notMatchAgainst(entity, attributes, value)); return this; }
	public Aggregator notMatchAgainstIfNotNull(Entity<?> entity, String attribute, String value) { add(Estivate.notMatchAgainstIfNotNull(entity, attribute, value)); return this; }
	public Aggregator notMatchAgainstIfNotNull(Entity<?> entity, Collection<String> attributes, String value) { add(Estivate.notMatchAgainstIfNotNull(entity, attributes, value)); return this; }

	public Aggregator matchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.matchAgainstIn(entity, attribute, values)); return this; }
	public Aggregator matchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { add(Estivate.matchAgainstIn(entity, attributes, values)); return this; }
	public Aggregator matchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.matchAgainstInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator matchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { add(Estivate.matchAgainstInIfNotEmpty(entity, attributes, values)); return this; }
	public Aggregator notMatchAgainstIn(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.notMatchAgainstIn(entity, attribute, values)); return this; }
	public Aggregator notMatchAgainstIn(Entity<?> entity, Collection<String> attributes, Collection<String> values) { add(Estivate.notMatchAgainstIn(entity, attributes, values)); return this; }
	public Aggregator notMatchAgainstInIfNotEmpty(Entity<?> entity, String attribute, Collection<String> values) { add(Estivate.notMatchAgainstInIfNotEmpty(entity, attribute, values)); return this; }
	public Aggregator notMatchAgainstInIfNotEmpty(Entity<?> entity, Collection<String> attributes, Collection<String> values) { add(Estivate.notMatchAgainstInIfNotEmpty(entity, attributes, values)); return this; }



	public Aggregator nativeCriterion(Entity<?> entity, String attribute, String criterion) { add(Estivate.nativeCriterion(entity, attribute, criterion)); return this; }


	public Aggregator inSubQuery		(Entity<?> entity, String attribute, SelectQuery<?> subQuery)	{ add(Estivate.inSubQuery(entity, attribute, subQuery)); return this; }
	public Aggregator notInSubQuery		(Entity<?> entity, String attribute, SelectQuery<?> subQuery)	{ add(Estivate.notInSubQuery(entity, attribute, subQuery)); return this; }
	
	
	/* Wrappers for Class */
	public Aggregator eq    	(Class<?> entity, String attribute, Object value)        { return eq(new Entity<>(entity), attribute, value); }
	public Aggregator eqIfNotNull    	(Class<?> entity, String attribute, Object value){ return eqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator eqNullable(Class<?> entity, String attribute, Object value)		 { return eqNullable(new Entity<>(entity), attribute, value); }
	public Aggregator eqOrNull(Class<?> entity, String attribute, Object value) 		 { return eqOrNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator notEq 	(Class<?> entity, String attribute, Object value)       { return notEq(new Entity<>(entity), attribute, value); }
	public Aggregator notEqIfNotNull (Class<?> entity, String attribute, Object value)	{ return notEqIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notEqNullable(Class<?> entity, String attribute, Object value)	{ return notEqNullable(new Entity<>(entity), attribute, value); }
	public Aggregator notEqOrNull(Class<?> entity, String attribute, Object value)		{ return notEqOrNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator lt    	(Class<?> entity, String attribute, Object value)        { return lt(new Entity<>(entity), attribute, value); }
	public Aggregator ltIfNotNull    	(Class<?> entity, String attribute, Object value){ return ltIfNotNull(new Entity<>(entity), attribute, value); }
	
	public Aggregator lte   	(Class<?> entity, String attribute, Object value)        { return lte(new Entity<>(entity), attribute, value); }
	
	public Aggregator gt    	(Class<?> entity, String attribute, Object value)        { return gt(new Entity<>(entity), attribute, value); }
	public Aggregator gte   	(Class<?> entity, String attribute, Object value)        { return gte(new Entity<>(entity), attribute, value); }
	
	public Aggregator between	(Class<?> entity, String attribute, Object left, Object right) { return between(new Entity<>(entity), attribute, left, right); }
	
	public Aggregator in    	(Class<?> entity, String attribute, Collection<?> values) 			{ return in(new Entity<>(entity), attribute, values); }
	public Aggregator inIfNotEmptyNullable(Class<?> entity, String attribute, Collection<?> values) { return inIfNotEmptyNullable(new Entity<>(entity), attribute, values); }
	public Aggregator inOrNull(Class<?> entity, String attribute, Collection<?> values) 			{ return inOrNull(new Entity<>(entity), attribute, values); }
	public Aggregator inIfNotEmptyOrNull(Class<?> entity, String attribute, Collection<?> values) 	{ return inIfNotEmptyOrNull(new Entity<>(entity), attribute, values); }
	public Aggregator notIn    	(Class<?> entity, String attribute, Collection<?> values) 			{ return notIn(new Entity<>(entity), attribute, values); }
	public Aggregator notInOrNull    	(Class<?> entity, String attribute, Collection<?> values) 	{ return notInOrNull(new Entity<>(entity), attribute, values); }
	public Aggregator notInIfNotEmptyOrNull(Class<?> entity, String attribute, Collection<?> values){ return notInIfNotEmptyOrNull(new Entity<>(entity), attribute, values); }

	public Aggregator like		(Class<?> entity, String attribute, String value)	{ return like(new Entity<>(entity), attribute, value); }
	public Aggregator likeIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLike	(Class<?> entity, String attribute, String value)	{ return notLike(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeIn(new Entity<>(entity), attribute, value); }
	

	public Aggregator likeStartsWith	(Class<?> entity, String attribute, String value)	{ return likeStartsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWith	(Class<?> entity, String attribute, String value)	{ return notLikeStartsWith(new Entity<>(entity), attribute, value); }
	
	public Aggregator likeEndsWith		(Class<?> entity, String attribute, String value)	{ return likeEndsWith(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWith	(Class<?> entity, String attribute, String value)	{ return notLikeEndsWith(new Entity<>(entity), attribute, value); }
	
	public Aggregator likeContains		(Class<?> entity, String attribute, String value)	{ return likeContains(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContains	(Class<?> entity, String attribute, String value)	{ return notLikeContains(new Entity<>(entity), attribute, value); }

	public Aggregator likeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return likeStartsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWithIn		(Class<?> entity, String attribute, Collection<String> value)	{ return likeEndsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator likeContainsIn		(Class<?> entity, String attribute, Collection<String> value)	{ return likeContainsIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeStartsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWithIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeEndsWithIn(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContainsIn	(Class<?> entity, String attribute, Collection<String> value)	{ return notLikeContainsIn(new Entity<>(entity), attribute, value); }

	
	public Aggregator inSubQuery(Class<?> entity, String attribute, SelectQuery<?> subQuery)	  	{ return inSubQuery(new Entity<>(entity), attribute, subQuery); }
	public Aggregator notInSubQuery(Class<?> entity, String attribute, SelectQuery<?> subQuery)	{ return notInSubQuery(new Entity<>(entity), attribute, subQuery); }

	public Aggregator gtIfNotNull    	(Class<?> entity, String attribute, Object value)        	{ return gtIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return lteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteIfNotNull   	(Class<?> entity, String attribute, Object value)        	{ return gteIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator betweenIfNotNull	(Class<?> entity, String attribute, Object min, Object max)	{ return betweenIfNotNull(new Entity<>(entity), attribute, min, max); }

	public Aggregator inIfNotEmpty    	(Class<?> entity, String attribute, Collection<?> values) 	{ return inIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator notInIfNotEmpty   (Class<?> entity, String attribute, Collection<?> values) 	{ return notInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator inOrFalseIfEmpty	(Class<?> entity, String attribute, Collection<?> values)	{ return inOrFalseIfEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator notInOrTrueIfEmpty(Class<?> entity, String attribute, Collection<?> values)	{ return notInOrTrueIfEmpty(new Entity<>(entity), attribute, values); }
	
	public Aggregator likeIfNotNull				(Class<?> entity, String attribute, String value)	{ return likeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeStartsWithIfNotNull	(Class<?> entity, String attribute, String value)	{ return likeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeEndsWithIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator likeContainsIfNotNull		(Class<?> entity, String attribute, String value)	{ return likeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	public Aggregator likeInIfNotEmpty 			(Class<?> entity, String attribute, Collection<String> values) { return likeInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeStartsWithInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { return likeStartsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeEndsWithInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { return likeEndsWithInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator likeContainsInIfNotEmpty	(Class<?> entity, String attribute, Collection<String> values) { return likeContainsInIfNotEmpty(new Entity<>(entity), attribute, values); }

	
	public Aggregator notLikeIfNotNull			(Class<?> entity, String attribute, String value){ return notLikeIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeStartsWithIfNotNull(Class<?> entity, String attribute, String value){ return notLikeStartsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeEndsWithIfNotNull	(Class<?> entity, String attribute, String value){ return notLikeEndsWithIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notLikeContainsIfNotNull	(Class<?> entity, String attribute, String value){ return notLikeContainsIfNotNull(new Entity<>(entity), attribute, value); }

	
	public Aggregator isNotNull(Class<?> entity, String attribute) { return isNotNull(new Entity<>(entity), attribute); }
	public Aggregator isNull(Class<?> entity, String attribute) { return isNull(new Entity<>(entity), attribute); }

	public Aggregator ltOrNull(Class<?> entity, String attribute, Object value) 	{ return ltOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gtOrNull(Class<?> entity, String attribute, Object value) 	{ return gtOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator lteOrNull(Class<?> entity, String attribute, Object value) 	{ return lteOrNull(new Entity<>(entity), attribute, value); }
	public Aggregator gteOrNull(Class<?> entity, String attribute, Object value) 	{ return gteOrNull(new Entity<>(entity), attribute, value); }

	public Aggregator matchAgainst(Class<?> entity, String attribute, String value) { return matchAgainst(new Entity<>(entity), attribute, value); }
	public Aggregator matchAgainst(Class<?> entity, Collection<String> attributes, String value) { return matchAgainst(new Entity<>(entity), attributes, value); }
	public Aggregator matchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return matchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator matchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { return matchAgainstIfNotNull(new Entity<>(entity), attributes, value); }
	public Aggregator notMatchAgainst(Class<?> entity, String attribute, String value) { return notMatchAgainst(new Entity<>(entity), attribute, value); }
	public Aggregator notMatchAgainst(Class<?> entity, Collection<String> attributes, String value) { return notMatchAgainst(new Entity<>(entity), attributes, value); }
	public Aggregator notMatchAgainstIfNotNull(Class<?> entity, String attribute, String value) { return notMatchAgainstIfNotNull(new Entity<>(entity), attribute, value); }
	public Aggregator notMatchAgainstIfNotNull(Class<?> entity, Collection<String> attributes, String value) { return notMatchAgainstIfNotNull(new Entity<>(entity), attributes, value); }

	public Aggregator matchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { return matchAgainstIn(new Entity<>(entity), attribute, values); }
	public Aggregator matchAgainstIn(Class<?> entity, Collection<String> attributes, Collection<String> values) { return matchAgainstIn(new Entity<>(entity), attributes, values); }
	public Aggregator matchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { return matchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator matchAgainstInIfNotEmpty(Class<?> entity, Collection<String> attributes, Collection<String> values) { return matchAgainstInIfNotEmpty(new Entity<>(entity), attributes, values); }
	public Aggregator notMatchAgainstIn(Class<?> entity, String attribute, Collection<String> values) { return notMatchAgainstIn(new Entity<>(entity), attribute, values); }
	public Aggregator notMatchAgainstIn(Class<?> entity, Collection<String> attributes, Collection<String> values) { return notMatchAgainstIn(new Entity<>(entity), attributes, values); }
	public Aggregator notMatchAgainstInIfNotEmpty(Class<?> entity, String attribute, Collection<String> values) { return notMatchAgainstInIfNotEmpty(new Entity<>(entity), attribute, values); }
	public Aggregator notMatchAgainstInIfNotEmpty(Class<?> entity, Collection<String> attributes, Collection<String> values) { return notMatchAgainstInIfNotEmpty(new Entity<>(entity), attributes, values); }
	

	public Aggregator nativeCriterion(Class<?> entity, String attribute, String criterion) { return nativeCriterion(new Entity<>(entity), attribute, criterion); }
	
	
	public Aggregator add(EstivateNode joinNode) { if(joinNode != null) { criterions.add(joinNode); } return this; }

	public Aggregator addIf(boolean condition, EstivateNode joinNode) { if(condition) { add(joinNode); } return this; }
	
	
	public List<EstivateNode> findCriterionForAttribute(Entity<?> entity, String attribute) {
		List<EstivateNode> found = new ArrayList<>();
		
		for(EstivateNode criterion : criterions) {
			if(criterion instanceof Criterion) {
				Criterion c = (Criterion)criterion;
				if(c.entity.equals(entity) && c.attribute.equals(attribute)) {
					found.add(criterion);
				}
			} else if(criterion instanceof Aggregator) {
				Aggregator agg = (Aggregator)criterion;
				found.addAll(agg.findCriterionForAttribute(entity, attribute));
			}
		}
		
		return found;
	}

	public Aggregator clone() {
		Aggregator joinAggregator = new Aggregator(this.groupType);
		joinAggregator.criterions = criterions.stream().map(x -> x.clone()).collect(Collectors.toList());
		return joinAggregator;
	}

	@Override
	public boolean isEmpty() {
		return criterions.stream().allMatch(x -> x == null || x.isEmpty());
	}





	
	
	

}
