package com.estivate.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexColumn {
    private String columnName;
    private Integer length;

    public static IndexColumn of(com.estivate.index.Annotations.IndexColumn column) {
        return new IndexColumn(column.value(), column.length());
    }
}