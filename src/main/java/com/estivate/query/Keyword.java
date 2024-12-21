package com.estivate.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class Keyword implements EstivateNode{
	
	KeywordValue value;
	
	public static enum KeywordValue{
		TRUE,
		FALSE,
	}
	
	public Keyword clone() {
		return new Keyword(value);
	}

}
