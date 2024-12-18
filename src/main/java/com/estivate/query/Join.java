package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.estivate.entity.VirtualForeignKey;
import com.estivate.util.FieldUtils;
import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Join {

	public Query.Entity leftEntity;

	public Query.Entity rightEntity;

	
	public List<Pair<String, String>> joins = new ArrayList<>();
	
	public JoinType joinType = JoinType.INNER;
	
	public IndexHint indexHint = null;
	public List<String> indexNames;



	public Join(Query.Entity leftEntity, Query.Entity rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this.leftEntity = leftEntity;
		this.rightEntity = rightEntity;
		this.joinType = joinType;
		joins.add(Pair.of(leftAttribute, rightAttribute));
	}

	public static Join Inner(Query.Entity leftEntity, 	Query.Entity rightEntity)	{ return find(leftEntity, rightEntity, JoinType.INNER); }
	public static Join Inner(Query.Entity leftEntity, 	Class rightClass)			{ return Join.Inner(leftEntity, new Query.Entity(rightClass)); }
	public static Join Inner(Class leftClass, 			Query.Entity rightEntity)	{ return Join.Inner(new Query.Entity(leftClass), rightEntity); }
	public static Join Inner(Class joinerEntity, 		Class joinedEntity)			{ return Join.Inner(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity)); }

	public static Join Outer(Query.Entity leftEntity, 	Query.Entity rightEntity)	{ return find(leftEntity, rightEntity, JoinType.OUTER); }
	public static Join Outer(Query.Entity leftEntity, 	Class rightClass)			{ return Join.Outer(leftEntity, new Query.Entity(rightClass)); }
	public static Join Outer(Class leftClass, 			Query.Entity rightEntity)	{ return Join.Outer(new Query.Entity(leftClass), rightEntity); }
	public static Join Outer(Class joinerEntity, 		Class joinedEntity)			{ return Join.Outer(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity)); }

	public static Join Left	(Query.Entity leftEntity, 	Query.Entity rightEntity)	{ return find(leftEntity, rightEntity, JoinType.LEFT); }
	public static Join Left	(Query.Entity leftEntity, 	Class rightClass)			{ return Join.Left(leftEntity, new Query.Entity(rightClass)); }
	public static Join Left	(Class leftClass, 			Query.Entity rightEntity)	{ return Join.Left(new Query.Entity(leftClass), rightEntity); }
	public static Join Left	(Class joinerEntity, 		Class joinedEntity)			{ return Join.Left(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity)); }

	public static Join Right(Query.Entity leftEntity, 	Query.Entity rightEntity)	{ return find(leftEntity, rightEntity, JoinType.RIGHT); }
	public static Join Right(Query.Entity leftEntity, 	Class rightClass)			{ return Join.Right(leftEntity, new Query.Entity(rightClass)); }
	public static Join Right(Class leftClass, 			Query.Entity rightEntity)	{ return Join.Right(new Query.Entity(leftClass), rightEntity); }
	public static Join Right(Class joinerEntity, 		Class joinedEntity)			{ return Join.Right(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity)); }


	public static Join Inner(Query.Entity leftEntity, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join Inner(Query.Entity leftEntity, Class rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity(rightClass), leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join Inner(Class leftClass, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.INNER); }
	public static Join Inner(Class joinerEntity, Class joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity), joinerAttribute, joinedAttribute, JoinType.INNER); }

	public static Join Outer(Query.Entity leftEntity, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join Outer(Query.Entity leftEntity, Class rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity(rightClass), leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join Outer(Class leftClass, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.OUTER); }
	public static Join Outer(Class joinerEntity, Class joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity), joinerAttribute, joinedAttribute, JoinType.OUTER); }

	public static Join Left(Query.Entity leftEntity, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join Left(Query.Entity leftEntity, Class rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity(rightClass), leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join Left(Class leftClass, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.LEFT); }
	public static Join Left(Class joinerEntity, Class joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity), joinerAttribute, joinedAttribute, JoinType.LEFT); }

	public static Join Right(Query.Entity leftEntity, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(leftEntity, rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join Right(Query.Entity leftEntity, Class rightClass, String leftAttribute, String rightAttribute){ return new Join(leftEntity, new Query.Entity(rightClass), leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join Right(Class leftClass, Query.Entity rightEntity, String leftAttribute, String rightAttribute){ return new Join(new Query.Entity(leftClass), rightEntity, leftAttribute, rightAttribute, JoinType.RIGHT); }
	public static Join Right(Class joinerEntity, Class joinedEntity, String joinerAttribute, String joinedAttribute){ return new Join(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity), joinerAttribute, joinedAttribute, JoinType.RIGHT); }



	private static Join find(Query.Entity internal, Query.Entity external, JoinType joinType) {

		// try doing the join from external class to internal class
		for(Field externalField : FieldUtils.getEntityFields(external.entity)) {
			VirtualForeignKey reference = externalField.getDeclaredAnnotation(VirtualForeignKey.class);

			// if key not found or not refering to baseClass, skip
			if(reference == null || reference.entity() != internal.entity) {
				continue;
			}

			Join cj = new Join();
			cj.leftEntity = internal;
			cj.rightEntity = external;
			cj.on(StringUtils.isBlank(reference.attribute()) ? "id" : reference.attribute(), externalField.getName());
			cj.joinType = joinType;

			return cj;
		}

		// try the other way around
		for(Field internalField : FieldUtils.getEntityFields(internal.entity)) {
			VirtualForeignKey reference = internalField.getDeclaredAnnotation(VirtualForeignKey.class);
			if(reference == null || reference.entity() != external.entity) {
				continue;
			}

			Join cj = new Join();
			cj.leftEntity = internal;
			cj.rightEntity = external;
			cj.on(internalField.getName(), StringUtils.isBlank(reference.attribute()) ? "id" : reference.attribute());
			cj.joinType = joinType;

			return cj;
		}


		return null;
	}
	
	public enum JoinType{
		LEFT,
		RIGHT,
		INNER,
		OUTER
	}
	
	public Join setJoinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	public Join on(String joinerAttribute, String joinedAttribute) {
		joins.add(Pair.of(joinerAttribute, joinedAttribute));
		return this;
	}

	public Join setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		
		this.indexNames = new ArrayList<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}


}
