package com.estivate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JoinQueryPreparedStatement {
	
	public String query = "";
	
	public List<Object> parameters = new ArrayList<>();

	public static JoinQueryPreparedStatement mergeInOne(List<JoinQueryPreparedStatement> criterionsStatements, String start, String end, String separator) {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append(criterionsStatements.stream().map(x -> x.query).collect(Collectors.joining(separator)));
		sb.append(end);
		
		return new JoinQueryPreparedStatement(sb.toString(), criterionsStatements.stream().flatMap(x -> x.parameters.stream()).collect(Collectors.toList()));
	}
	
	

}
