package com.estivate;

import java.lang.reflect.Field;

import com.estivate.util.StringPipe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EstivateJoin {

	EstivateQuery.Entity internalClass;

	EstivateQuery.Entity externalClass;

	// TODO : join type ? LEFT, RIGHT, INNER

	String internalAttribute;

	String externalAttribute;

	public String toString() {

		//return "INNER JOIN "+JoinQuery.nameMapper.mapEntity(externalClass.entity)+" AS "+externalClass.alias+" ON "+JoinQuery.nameMapper.mapEntityAttribute(externalClass, externalAttribute) + " = "+ JoinQuery.nameMapper.mapEntityAttribute(internalClass, internalAttribute);

		StringPipe sb = new StringPipe().separator(" ")
				.append  ("INNER JOIN")
				.append  (EstivateQuery.nameMapper.mapEntity(externalClass.entity))
				.appendIf(externalClass.alias != null, "AS "+externalClass.alias)
				.append  ("ON")
				.append  (externalClass.getName()+"."+EstivateQuery.nameMapper.mapAttribute(externalAttribute))
				.append  ("=")
				.append  (internalClass.getName()+"."+EstivateQuery.nameMapper.mapAttribute(internalAttribute));

		return sb.toString();
	}

	public EstivateJoin(EstivateQuery.Entity internalEntity, Class externalEntity, String internalAttribute, String externalAttribute) {
		this(internalEntity, new EstivateQuery.Entity(externalEntity), internalAttribute, externalAttribute);
	}

	public EstivateJoin(Class internalEntity, Class externalEntity, String internalAttribute, String externalAttribute) {
		this(new EstivateQuery.Entity(internalEntity), new EstivateQuery.Entity(externalEntity), internalAttribute, externalAttribute);
	}



	public static EstivateJoin find(EstivateQuery.Entity internal, EstivateQuery.Entity external) {

		// try doing the join from external class to internal class
		for(Field externalField : external.entity.getDeclaredFields()) {
			EstivateVirtualForeignKey reference = externalField.getDeclaredAnnotation(EstivateVirtualForeignKey.class);

			// if key not found or not refering to baseClass, skip
			if(reference == null || reference.entity() != internal.entity) {
				continue;
			}

			EstivateJoin cj = new EstivateJoin();
			cj.internalClass = internal;
			cj.externalClass = external;
			cj.internalAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();
			cj.externalAttribute = externalField.getName();

			return cj;
		}

		// try the other way around
		for(Field internalField : internal.entity.getDeclaredFields()) {
			EstivateVirtualForeignKey reference = internalField.getDeclaredAnnotation(EstivateVirtualForeignKey.class);
			if(reference == null || reference.entity() != external.entity) {
				continue;
			}

			EstivateJoin cj = new EstivateJoin();
			cj.internalClass = internal;
			cj.externalClass = external;
			cj.internalAttribute = internalField.getName();
			cj.externalAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();

			return cj;
		}


		return null;
	}


}
