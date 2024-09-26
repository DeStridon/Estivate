package com.estivate.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StackLog {
	
	public static List<String> create() {
		
		return Arrays.asList(Thread.currentThread().getStackTrace()).stream()
				.filter(x -> !x.getClassName().startsWith("com.estivate"))
				.map(x -> x.toString())
				.collect(Collectors.toList());
		
	}
	
	public static void main(String... args) {

		System.out.println(StackLog.create());
	
	}

}
