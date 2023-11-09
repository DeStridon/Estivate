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
		
		if(method == SelectMethod.Count && entity != null && attribute != null) {
			return "COUNT(distinct "+entity.getName() + "." + Query.nameMapper.mapAttribute(attribute)+")";
		}
		
		return entity.getName() + "." + Query.nameMapper.mapAttribute(attribute)+" as `"+entity.getName()+"."+attribute+"`";
		
		
	}
	
	
	public static enum SelectMethod{
		Count,
		Max,
		Min,
		Sum
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
		else if(StringUtils.compare(this.entity.getName(), select.entity.getName()) != 0) {
			return StringUtils.compare(this.entity.getName(), select.entity.getName());
		}
		return StringUtils.compare(this.attribute, select.attribute);
		
		
	}



}
