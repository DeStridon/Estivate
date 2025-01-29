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
	public Entity<?> entity;
	public String attribute;
	public String alias;
	
	
	public enum SelectMethod{
		Count,
		CountDistinct,
		Max,
		Min,
		Sum,
		Distinct,
		GroupConcat
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
