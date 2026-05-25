package com.estivate.reconciliation;

import java.lang.reflect.Field;

import com.estivate.context.Context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ColumnModel {



    @Data
    @AllArgsConstructor
    public static class ColumnFormat{
        public ColumnFormat(String type){ this.type = type; this.length = null; }
        String type;
        Integer length;
    }

    @Data
    public static class EntityColumn{
        //Field field;
        String name;
        Class<?> type;
        String designedName;
        String designedType;
        Integer designedLength;
        String defaultValue;
        String charset;
        String collation;
        boolean isNullable = true;
        boolean isAutoIncrement = false;
        boolean isPrimaryKey = false;
        
    }


}
