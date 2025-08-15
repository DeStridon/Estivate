// package com.estivate.test.entities;

// import java.util.Date;

// import javax.persistence.Convert;
// import javax.persistence.EnumType;
// import javax.persistence.Enumerated;

// import com.estivate.Entity.InsertDate;
// import com.estivate.Entity.UpdateDate;
// import com.estivate.Entity.VirtualForeignKey;
// import com.estivate.index.Annotations.IndexColumn;
// import com.estivate.index.Annotations.IndexType;
// import com.estivate.index.Annotations.TableIndex;
// import com.estivate.index.Annotations.TableIndexes;

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
// @TableIndexes({
//     @TableIndex(name = "idx_user_email", columns = {@IndexColumn(User.Fields.email)}, type = IndexType.UNIQUE),
//     @TableIndex(name = "idx_user_username", columns = {@IndexColumn(User.Fields.username)}, type = IndexType.UNIQUE),
//     @TableIndex(name = "idx_user_created", columns = {@IndexColumn(User.Fields.createdAt)}),
//     @TableIndex(name = "idx_user_status", columns = {@IndexColumn(User.Fields.status)}),
//     @TableIndex(name = "idx_user_role", columns = {@IndexColumn(User.Fields.roleId)})
// })
// public class User extends AbstractEntity {
    
//     @VirtualForeignKey(entity = Role.class)
//     private Long roleId;
    
//     private String username;
//     private String email;
//     private String firstName;
//     private String lastName;
//     private String phoneNumber;
    
//     @Enumerated(EnumType.STRING)
//     private UserStatus status;
    
//     @InsertDate
//     private Date createdAt;
    
//     @UpdateDate
//     private Date updatedAt;
    
//     private Date lastLoginAt;
//     private boolean active;
//     private String avatarUrl;
    
//     public enum UserStatus {
//         ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
//     }
// } 