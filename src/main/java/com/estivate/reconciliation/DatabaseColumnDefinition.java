package com.estivate.reconciliation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    Describes the column definition (from database or @Column annotation)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseColumnDefinition {

    private static final Pattern COLUMN_TYPE_PATTERN = Pattern.compile(
            "([a-zA-Z]+)(?:\\s*\\(([0-9]+)(?:,([0-9]+))?\\))?(?:\\s+(UNSIGNED|SIGNED))?(?:\\s+ZEROFILL)?",
            Pattern.CASE_INSENSITIVE);

    

    /** Base SQL type name, optionally with UNSIGNED/SIGNED (e.g. VARCHAR, DECIMAL, SMALLINT UNSIGNED). */
    String type;

    /** Character length or numeric precision. */
    Integer dimension;

    /** Numeric scale (DECIMAL/NUMERIC only). */
    Integer scale;

    public static DatabaseColumnDefinition parse(String columnType) {
        if (columnType == null) {
            return null;
        }
        if (columnType.toUpperCase().startsWith("ENUM")) {
            return new DatabaseColumnDefinition(columnType, null, null);
        }
        Matcher matcher = COLUMN_TYPE_PATTERN.matcher(columnType.trim());
        if (matcher.find()) {
            Integer length = matcher.group(2) == null ? null : Integer.parseInt(matcher.group(2));
            Integer scale = matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3));
            String type = matcher.group(1);
            if (matcher.group(4) != null) {
                type = type + " " + matcher.group(4);
            }
            return new DatabaseColumnDefinition(type, length, scale);
        }
        return null;
    }

}
