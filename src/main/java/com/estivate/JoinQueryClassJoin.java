package com.estivate;

import java.lang.reflect.Field;

import com.wezen.framework.StringPipe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JoinQueryClassJoin {

	JoinQuery.Entity internalClass;

	JoinQuery.Entity externalClass;

	// TODO : join type ? LEFT, RIGHT, INNER

	String internalAttribute;

	String externalAttribute;

	public String toString() {

		//return "INNER JOIN "+JoinQuery.nameMapper.mapEntity(externalClass.entity)+" AS "+externalClass.alias+" ON "+JoinQuery.nameMapper.mapEntityAttribute(externalClass, externalAttribute) + " = "+ JoinQuery.nameMapper.mapEntityAttribute(internalClass, internalAttribute);

		StringPipe sb = new StringPipe().separator(" ")
				.append  ("INNER JOIN")
				.append  (JoinQuery.nameMapper.mapEntity(externalClass.entity))
				.appendIf(externalClass.alias != null, "AS "+externalClass.alias)
				.append  ("ON")
				.append  (externalClass.getName()+"."+JoinQuery.nameMapper.mapAttribute(externalAttribute))
				.append  ("=")
				.append  (internalClass.getName()+"."+JoinQuery.nameMapper.mapAttribute(internalAttribute));

		return sb.toString();
	}

	public JoinQueryClassJoin(JoinQuery.Entity internalEntity, Class externalEntity, String internalAttribute, String externalAttribute) {
		this(internalEntity, new JoinQuery.Entity(externalEntity), internalAttribute, externalAttribute);
	}

	public JoinQueryClassJoin(Class internalEntity, Class externalEntity, String internalAttribute, String externalAttribute) {
		this(new JoinQuery.Entity(internalEntity), new JoinQuery.Entity(externalEntity), internalAttribute, externalAttribute);
	}



	public static JoinQueryClassJoin find(JoinQuery.Entity internal, JoinQuery.Entity external) {

		// try doing the join from external class to internal class
		for(Field externalField : external.entity.getDeclaredFields()) {
			VirtualForeignKey reference = externalField.getDeclaredAnnotation(VirtualForeignKey.class);

			// if key not found or not refering to baseClass, skip
			if(reference == null || reference.entity() != internal.entity) {
				continue;
			}

			JoinQueryClassJoin cj = new JoinQueryClassJoin();
			cj.internalClass = internal;
			cj.externalClass = external;
			cj.internalAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();
			cj.externalAttribute = externalField.getName();

			return cj;
		}

		// try the other way around
		for(Field internalField : internal.entity.getDeclaredFields()) {
			VirtualForeignKey reference = internalField.getDeclaredAnnotation(VirtualForeignKey.class);
			if(reference == null || reference.entity() != external.entity) {
				continue;
			}

			JoinQueryClassJoin cj = new JoinQueryClassJoin();
			cj.internalClass = internal;
			cj.externalClass = external;
			cj.internalAttribute = internalField.getName();
			cj.externalAttribute = reference.attribute().isBlank() ? "id" : reference.attribute();

			return cj;
		}


		return null;
	}


}
