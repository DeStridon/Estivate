package com.estivate.query;

import org.apache.commons.lang3.StringUtils;

import com.estivate.query.Query.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Select implements Comparable {
	public SelectMethod method;
	public Entity entity;
	public String attribute;
	public String alias;
	
	public String toString() {
		
		if(method == SelectMethod.Count) {
			if (entity == null) {
				return "COUNT(*)";
			}
			return "COUNT(distinct "+Query.nameMapper.mapDatabase(entity, attribute)+")";
		}
		else if(method == SelectMethod.Max) {
			return "MAX("+Query.nameMapper.mapDatabase(entity, attribute)+")";
		}
		else if(method == SelectMethod.Min) {
			return "MIN("+Query.nameMapper.mapDatabase(entity, attribute)+")";
		}
		else if(method == SelectMethod.Sum) {
			return "SUM("+Query.nameMapper.mapDatabase(entity, attribute)+")";
		}
		else if(method == SelectMethod.Distinct) {
			return "DISTINCT "+Query.nameMapper.mapDatabase(entity, attribute)+" as `"+Query.nameMapper.mapEntity(entity, attribute)+"`";
		}
		
		return Query.nameMapper.mapDatabase(entity, attribute)+" as `"+Query.nameMapper.mapEntity(entity, attribute)+"`";
		
	}
	
	
	public enum SelectMethod{
		Count,
		Max,
		Min,
		Sum,
		Distinct
	}


	@Override
	public int compareTo(Object o) {
		if(!(o instanceof Select)) {
			return -1;
		}
		Select select = (Select) o;
		
		if(this.method != null && select.method == null) {
			return -1;
		}
		else if(this.method == null && select.method != null) {
			return 1;
		}
		return StringUtils.compare(this.toString(), select.toString());
		
	}



}
