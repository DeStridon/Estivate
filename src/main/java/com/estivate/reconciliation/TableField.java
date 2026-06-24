package com.estivate.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableField {

    /** Field/column name (uses entity field name, not database column name) */
    public String name;

    /** SQL type (VARCHAR, INT, BIGINT, etc.) */
    public String type;

    /** Whether the field is nullable */
    public boolean nullable;

    /** Whether this field is auto-increment */
    public boolean autoIncrement;

    /** Default value if any */
    public String defaultValue;

    /** Column length/size (e.g. VARCHAR(255) -> 255) */
    public Integer length;

    public boolean equalsIgnoringNullable(TableField other) {
        if (other == null) return false;
        if (!name.equalsIgnoreCase(other.name)) return false;
        if (!typeMatches(other.type)) return false;
        return true;
    }

    public boolean typeMatches(String otherType) {
        if (type == null && otherType == null) return true;
        if (type == null || otherType == null) return false;
        
        // Normalize type comparison (INT vs INTEGER, etc.)
        String normalizedThis = normalizeType(type);
        String normalizedOther = normalizeType(otherType);
        
        return normalizedThis.equalsIgnoreCase(normalizedOther);
    }

    private String normalizeType(String sqlType) {
        if (sqlType == null) return "";
        String upper = sqlType.toUpperCase().trim();
        
        // Remove length specification for comparison
        if (!upper.startsWith("ENUM") && upper.contains("(")) {
            upper = upper.substring(0, upper.indexOf("("));
        }
        
        // Normalize common type aliases
        switch (upper) {
            case "INTEGER": return "INT";
            case "BOOL": return "BOOLEAN";
            case "TINYINT": return "BOOLEAN"; // MySQL stores booleans as TINYINT(1)
            case "BIT": return "BOOLEAN";     // MySQL/MariaDB store booleans as BIT(1)
            default: return upper;
        }
    }

}
