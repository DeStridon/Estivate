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

	Query.Entity joinerEntity;

	Query.Entity joinedEntity;

	
	List<Pair<String, String>> joins = new ArrayList<>();
	
	JoinType joinType = JoinType.INNER;
	
	IndexHint indexHint = null;
	List<String> indexNames;

	public String toString() {
		
		StringPipe sb = new StringPipe().separator(" ")
				.append  (joinType.toString())
				.append  ("JOIN")
				//.append  (Query.nameMapper.mapEntityClass(joinedEntity.entity))
				.append  (Query.nameMapper.mapDatabaseClass(joinedEntity.entity))
				.appendIf(joinedEntity.alias != null, joinedEntity.alias);
		if(indexHint != null && indexNames != null && !indexNames.isEmpty()) {
			sb	.append  (indexHint.toString()+ " INDEX ("+indexNames.stream().collect(Collectors.joining(", "))+")");
		}
		sb		.append  ("ON")
				//.append  (joins.stream().map(x -> Query.nameMapper.mapEntity(joinerEntity, x.getLeft()) + " = " + Query.nameMapper.mapEntity(joinedEntity, x.getRight())).collect(Collectors.joining(" and ")));
				.append  (joins.stream().map(x -> Query.nameMapper.mapDatabase(joinerEntity, x.getLeft()) + " = " + Query.nameMapper.mapDatabase(joinedEntity, x.getRight())).collect(Collectors.joining(" and ")));
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
		for(Field externalField : FieldUtils.getEntityFields(external.entity)) {
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
		for(Field internalField : FieldUtils.getEntityFields(internal.entity)) {
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
