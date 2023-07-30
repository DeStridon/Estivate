package com.estivate;

import java.sql.PreparedStatement;

import lombok.Getter;

public class EstimateStatement {
	
	StringBuilder query;
	
	@Getter
	PreparedStatement statement;
	
	public EstimateStatement appendQuery(String queryContent) {
		query.append(queryContent);
		
		return this;
	}
	
	public EstimateStatement appendParameter() {
		query.append("?");
		
		return this;
	}
	
	

}
