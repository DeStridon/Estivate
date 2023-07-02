package com.estivate;

import java.lang.reflect.Field;

import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstivateJoin {

	EstivateQuery.Entity joinerEntity;

	EstivateQuery.Entity joinedEntity;

	String joinerAttribute;

	String joinedAttribute;
	
	JoinType joinType = JoinType.INNER;

	public String toString() {
		
		StringPipe sb = new StringPipe().separator(" ")
				.append  (joinType.toString())
				.append  ("JOIN")
				.append  (EstivateQuery.nameMapper.mapEntity(joinedEntity.entity))
				.appendIf(joinedEntity.alias != null, "AS "+joinedEntity.alias)
				.append  ("ON")
				.append  (joinedEntity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(joinedAttribute))
				.append  ("=")
				.append  (joinerEntity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(joinerAttribute));

		return sb.toString();
		
	}

	public EstivateJoin(EstivateQuery.Entity internalEntity, EstivateQuery.Entity externalEntity){
		this.joinerEntity = internalEntity;
		this.joinedEntity = externalEntity;
	}
	
	public EstivateJoin(EstivateQuery.Entity joinerEntity, Class joinedClass){
		this(joinerEntity, new EstivateQuery.Entity(joinedClass));
	}
	
	public EstivateJoin(Class joinerClass, EstivateQuery.Entity joinedEntity){
		this(new EstivateQuery.Entity(joinerClass), joinedEntity);
	}

	public EstivateJoin(Class internalEntity, Class externalEntity) {
		this(new EstivateQuery.Entity(internalEntity), new EstivateQuery.Entity(externalEntity));
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
			cj.joinerAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();
			cj.joinedAttribute = externalField.getName();

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
			cj.joinerAttribute = internalField.getName();
			cj.joinedAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();

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

	public EstivateJoin joinerAttribute(String id) {
		this.joinerAttribute = id; 
		return this;
	}
	
	public EstivateJoin joinedAttribute(String attributeName) {
		this.joinedAttribute = attributeName;
		return this;
	}
	
	public EstivateJoin joinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}


}
