package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.estivate.entity.VirtualForeignKey;
import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Join {

	Query.Entity joinerEntity;

	Query.Entity joinedEntity;

	
	List<Pair<String, String>> joins = new ArrayList<>();
	
	JoinType joinType = JoinType.INNER;

	public String toString() {
		
		StringPipe sb = new StringPipe().separator(" ")
				.append  (joinType.toString())
				.append  ("JOIN")
				.append  (Query.nameMapper.mapEntity(joinedEntity.entity))
				.appendIf(joinedEntity.alias != null, joinedEntity.alias)
				.append  ("ON")
				.append  (joins.stream().map(x -> joinerEntity.getName()+"."+Query.nameMapper.mapAttribute(x.getLeft()) + " = " + joinedEntity.getName()+"."+Query.nameMapper.mapAttribute(x.getRight())).collect(Collectors.joining(" and ")));
		return sb.toString();
		
	}

	public Join(Query.Entity joinerEntity, Query.Entity joinedEntity, String joinerAttribute, String joinedAttribute){
		this.joinerEntity = joinerEntity;
		this.joinedEntity = joinedEntity;
		joins.add(Pair.of(joinerAttribute, joinedAttribute));
	}
	
	public Join(Query.Entity joinerEntity, Class joinedClass, String joinerAttribute, String joinedAttribute){
		this(joinerEntity, new Query.Entity(joinedClass), joinerAttribute, joinedAttribute);
	}
	
	public Join(Class joinerClass, Query.Entity joinedEntity, String joinerAttribute, String joinedAttribute){
		this(new Query.Entity(joinerClass), joinedEntity, joinerAttribute, joinedAttribute);
	}
	

	public Join(Class joinerEntity, Class joinedEntity, String joinerAttribute, String joinedAttribute) {
		this(new Query.Entity(joinerEntity), new Query.Entity(joinedEntity), joinerAttribute, joinedAttribute);
	}



	public static Join find(Query.Entity internal, Query.Entity external) {

		// try doing the join from external class to internal class
		for(Field externalField : external.entity.getDeclaredFields()) {
			VirtualForeignKey reference = externalField.getDeclaredAnnotation(VirtualForeignKey.class);

			// if key not found or not refering to baseClass, skip
			if(reference == null || reference.entity() != internal.entity) {
				continue;
			}

			Join cj = new Join();
			cj.joinerEntity = internal;
			cj.joinedEntity = external;
			cj.on(StringUtils.isBlank(reference.attribute()) ? "id" : reference.attribute(), externalField.getName());

			return cj;
		}

		// try the other way around
		for(Field internalField : internal.entity.getDeclaredFields()) {
			VirtualForeignKey reference = internalField.getDeclaredAnnotation(VirtualForeignKey.class);
			if(reference == null || reference.entity() != external.entity) {
				continue;
			}

			Join cj = new Join();
			cj.joinerEntity = internal;
			cj.joinedEntity = external;
			cj.on(internalField.getName(), StringUtils.isBlank(reference.attribute()) ? "id" : reference.attribute());

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
	
	public Join joinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	public Join on(String joinerAttribute, String joinedAttribute) {
		joins.add(Pair.of(joinerAttribute, joinedAttribute));
		return this;
	}


}
