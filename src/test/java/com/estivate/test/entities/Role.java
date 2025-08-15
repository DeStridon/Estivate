// package com.estivate.test.entities;

// import com.estivate.index.Annotations.IndexColumn;
// import com.estivate.index.Annotations.IndexType;
// import com.estivate.index.Annotations.TableIndex;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.FieldNameConstants;
// import lombok.experimental.SuperBuilder;

// @Data
// @EqualsAndHashCode(callSuper = false)
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @FieldNameConstants
// @TableIndex(name = "idx_role_name", columns = {@IndexColumn(Role.Fields.name)}, type = IndexType.UNIQUE)
// public class Role extends AbstractEntity {
    
//     private String name;
//     private String description;
//     private String permissions;
//     private boolean active;
//     private int priority;
// } 