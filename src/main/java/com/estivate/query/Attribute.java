package com.estivate.query;

import java.util.List;

import com.estivate.query.Query.Entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    public Entity<?> entity;
	public String attribute;
	public List<String> functions;

}
