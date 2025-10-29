package com.estivate.result;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.estivate.NameMapper;
import com.estivate.query.Select;
import com.estivate.query.SelectQuery;

public class ResultRow<U> {

    final String[] columnValues;
	final String[] columnNames;
	final NameMapper nameMapper;
    final SelectQuery<U> query;

    public ResultRow(String[] columnValues, String[] columnNames, NameMapper nameMapper, SelectQuery<U> query) {
        this.columnValues = columnValues;
        this.columnNames = columnNames;
        this.nameMapper = nameMapper;
        this.query = query;
    }


    public U get(){
        return null;
    }




}
