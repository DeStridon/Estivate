package com.estivate.reconciliation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// TODO : see overlapping with ColumnModel.ColumnFormat
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTypeParts {

    private static final Pattern COLUMN_TYPE_PATTERN = Pattern.compile(
            "([a-zA-Z]+)(?:\\s*\\(([0-9]+)(?:,([0-9]+))?\\))?(?:\\s+(UNSIGNED|SIGNED))?(?:\\s+ZEROFILL)?",
            Pattern.CASE_INSENSITIVE);

    

    /** Base SQL type name, optionally with UNSIGNED/SIGNED (e.g. VARCHAR, DECIMAL, SMALLINT UNSIGNED). */
    String type;

    /** Character length or numeric precision. */
    Integer length;

    /** Numeric scale (DECIMAL/NUMERIC only). */
    Integer scale;

    public static ColumnTypeParts parse(String columnType) {
        if (columnType == null) {
            return null;
        }
        if (columnType.toUpperCase().startsWith("ENUM")) {
            return new ColumnTypeParts(columnType, null, null);
        }
        Matcher matcher = COLUMN_TYPE_PATTERN.matcher(columnType.trim());
        if (matcher.find()) {
            Integer length = matcher.group(2) == null ? null : Integer.parseInt(matcher.group(2));
            Integer scale = matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3));
            String type = matcher.group(1);
            if (matcher.group(4) != null) {
                type = type + " " + matcher.group(4);
            }
            return new ColumnTypeParts(type, length, scale);
        }
        return null;
    }

    // /** Strips UNSIGNED/SIGNED suffix for dialect type lookups. */
    // public static String baseSqlType(String sqlType) {
    //     if (sqlType == null) {
    //         return null;
    //     }
    //     String trimmed = sqlType.trim();
    //     String upper = trimmed.toUpperCase();
    //     if (upper.endsWith(" UNSIGNED")) {
    //         return trimmed.substring(0, trimmed.length() - " UNSIGNED".length()).trim();
    //     }
    //     if (upper.endsWith(" SIGNED")) {
    //         return trimmed.substring(0, trimmed.length() - " SIGNED".length()).trim();
    //     }
    //     return trimmed;
    // }

    // /** Returns UNSIGNED/SIGNED suffix including leading space, or empty string. */
    // public static String signednessSuffix(String sqlType) {
    //     if (sqlType == null) {
    //         return "";
    //     }
    //     String upper = sqlType.trim().toUpperCase();
    //     if (upper.endsWith(" UNSIGNED")) {
    //         return " UNSIGNED";
    //     }
    //     if (upper.endsWith(" SIGNED")) {
    //         return " SIGNED";
    //     }
    //     return "";
    // }

}
