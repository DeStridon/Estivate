package com.estivate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstivateJoin {

	EstivateQuery.Entity joinerEntity;

	EstivateQuery.Entity joinedEntity;

	
	List<Pair<String, String>> joins = new ArrayList<>();
	
	JoinType joinType = JoinType.INNER;

	public String toString() {
		
		StringPipe sb = new StringPipe().separator(" ")
				.append  (joinType.toString())
				.append  ("JOIN")
				.append  (EstivateQuery.nameMapper.mapEntity(joinedEntity.entity))
				.appendIf(joinedEntity.alias != null, joinedEntity.alias)
				.append  ("ON")
				.append  (joins.stream().map(x -> joinedEntity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(x.getLeft()) + " = " + joinerEntity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(x.getRight())).collect(Collectors.joining(", ")));
		return sb.toString();
		
	}

	public EstivateJoin(EstivateQuery.Entity internalEntity, EstivateQuery.Entity externalEntity, String joinerAttribute, String joinedAttribute){
		this.joinerEntity = internalEntity;
		this.joinedEntity = externalEntity;
		joins.add(Pair.of(joinerAttribute, joinedAttribute));
	}
	
	public EstivateJoin(EstivateQuery.Entity joinerEntity, Class joinedClass, String joinerAttribute, String joinedAttribute){
		this(joinerEntity, new EstivateQuery.Entity(joinedClass), joinerAttribute, joinedAttribute);
	}
	
	public EstivateJoin(Class joinerClass, EstivateQuery.Entity joinedEntity, String joinerAttribute, String joinedAttribute){
		this(new EstivateQuery.Entity(joinerClass), joinedEntity, joinerAttribute, joinedAttribute);
	}
	

	public EstivateJoin(Class internalEntity, Class externalEntity, String joinerAttribute, String joinedAttribute) {
		this(new EstivateQuery.Entity(internalEntity), new EstivateQuery.Entity(externalEntity), joinerAttribute, joinedAttribute);
	}



	public static EstivateJoin find(EstivateQuery.Entity internal, EstivateQuery.Entity external) {

		// try doing the join from external class to internal class
		for(Field externalField : external.entity.getDeclaredFields()) {
			VirtualForeignKey reference = externalField.getDeclaredAnnotation(VirtualForeignKey.class);

			// if key not found or not refering to baseClass, skip
			if(reference == null || reference.entity() != internal.entity) {
				continue;
			}

			EstivateJoin cj = new EstivateJoin();
			cj.joinerEntity = internal;
			cj.joinedEntity = external;
			cj.on(reference.attribute().isBlank() ? "id" : reference.attribute(), externalField.getName());

			return cj;
		}

		// try the other way around
		for(Field internalField : internal.entity.getDeclaredFields()) {
			VirtualForeignKey reference = internalField.getDeclaredAnnotation(VirtualForeignKey.class);
			if(reference == null || reference.entity() != external.entity) {
				continue;
			}

			EstivateJoin cj = new EstivateJoin();
			cj.joinerEntity = internal;
			cj.joinedEntity = external;
			cj.on(internalField.getName(), reference.attribute().isBlank() ? "id" : reference.attribute());

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
	
	public EstivateJoin joinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	public EstivateJoin on(String joinerAttribute, String joinedAttribute) {
		joins.add(Pair.of(joinerAttribute, joinedAttribute));
		return this;
	}


}
