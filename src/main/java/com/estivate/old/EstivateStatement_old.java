//package com.estivate.old;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//
//@NoArgsConstructor
//@AllArgsConstructor
//public class EstivateStatement_old {
//	
//	public String query = "";
//	
//	public List<Object> parameters = new ArrayList<>();
//
//	public static EstivateStatement_old mergeInOne(List<EstivateStatement_old> criterionsStatements, String start, String end, String separator) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(start);
//		sb.append(criterionsStatements.stream().map(x -> x.query).collect(Collectors.joining(separator)));
//		sb.append(end);
//		
//		return new EstivateStatement_old(sb.toString(), criterionsStatements.stream().flatMap(x -> x.parameters.stream()).collect(Collectors.toList()));
//	}
//
//
//	
//	
//
//}
