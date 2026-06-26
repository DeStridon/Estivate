package com.estivate.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ColumnModel {



    @Data
    @AllArgsConstructor
    public static class ColumnFormat{
        public ColumnFormat(String type){ this.type = type; this.length = null; this.noLength = false; }
        String type;
        Integer length;
        boolean noLength;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
        @Builder.Default
        boolean isNullable = true;
        @Builder.Default
        boolean isAutoIncrement = false;
        @Builder.Default
        boolean isPrimaryKey = false;
        
    }


}
