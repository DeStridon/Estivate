package com.estivate.result;

import java.util.List;
import java.util.stream.Collectors;

import com.estivate.query.SelectQuery;

public class ResultTable <U> {

    SelectQuery<U> query;

    List<ResultRow<U>> rows;


    public List<U> get(){ return rows.stream().map(x -> x.get()).collect(Collectors.toList()); }




}
