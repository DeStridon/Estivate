package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.Entity.VirtualForeignKey;
import com.estivate.Estivate;
import com.estivate.util.FieldUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Join {

	public Entity<?> leftEntity;

	public Entity<?> rightEntity;
	
	
	public Aggregator joiningCriterion = Estivate.and();
	
	public JoinType joinType = JoinType.INNER;
	
	public IndexHint indexHint = null;
	public List<String> indexNames = new ArrayList<>();


	public Join(Entity<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this.leftEntity = leftEntity;
		this.rightEntity = rightEntity;
		this.joinType = joinType;
		this.on(leftAttribute, rightAttribute);
	}

	public Join(Class<?> leftEntity, Entity<?> rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this(new Entity<>(leftEntity), rightEntity, leftAttribute, rightAttribute, joinType);
	}

	public Join(Entity<?> leftEntity, Class<?> rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this(leftEntity, new Entity<>(rightEntity), leftAttribute, rightAttribute, joinType);
	}

	public Join(Class<?> leftEntity, Class<?> rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this(new Entity<>(leftEntity), new Entity<>(rightEntity), leftAttribute, rightAttribute, joinType);
	}

	public Join(Class<?> leftEntity, SubQueryEntity<?> rightEntity, String leftAttribute, String rightAttribute, JoinType joinType){
		this(new Entity<>(leftEntity), rightEntity, leftAttribute, rightAttribute, joinType);
	}


	public static Join find(Entity<?> internal, Entity<?> external, JoinType joinType) {

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
		joiningCriterion.add(Estivate.eq(leftEntity, joinerAttribute, Estivate.attribute(rightEntity, joinedAttribute)));
		return this;
	}
	
	public Join on(EstivateNode node) {
		joiningCriterion.add(node);
		return this;
	}

	public Join setIndexHint(IndexHint indexHint, String mainIndex, String... moreIndex) {
		this.indexHint = indexHint;
		
		this.indexNames = new ArrayList<>(Arrays.asList(mainIndex));
		this.indexNames.addAll(Arrays.asList(moreIndex));
		return this;
	}

	public Join clone() {
		Join join = new Join();
		join.leftEntity = this.leftEntity;
		join.rightEntity = this.rightEntity;
		join.joiningCriterion = this.joiningCriterion.clone();
		join.joinType = this.joinType;
		join.indexHint = this.indexHint;
		join.indexNames = new ArrayList<>(this.indexNames);
		return join;
	}


}
