---
name: estivate-table-indexes
description: >-
  Declare database indexes with Estivate @TableIndexes / @TableIndex / @IndexColumn
  instead of Jakarta @Table(indexes = @Index(...)) or uniqueConstraints. Use when
  creating or editing JPA entities, adding indexes, unique constraints via indexes,
  or reviewing @Table annotations in Sapience or Estivate model classes.
---

# Estivate table indexes

Do **not** create indexes with Jakarta’s `@Table(indexes = …)` / `@Index`. Use Estivate `@TableIndexes` so reconciliation can create, diff, and apply them.

Do **not** declare the same uniqueness twice. If a unique key is already a `@TableIndex(type = IndexType.UNIQUE, …)`, omit Jakarta `@Table(uniqueConstraints = …)` for those columns.

## Prefer

```java
@Entity
@Table(name = "community_thread_view")
@TableIndexes({
    @TableIndex(
        type = IndexType.UNIQUE,
        columns = {
            @IndexColumn(CommunityThreadViewEntity.Fields.threadId),
            @IndexColumn(CommunityThreadViewEntity.Fields.userId)
        }),
    @TableIndex(columns = {
        @IndexColumn(CommunityThreadViewEntity.Fields.courseId),
        @IndexColumn(CommunityThreadViewEntity.Fields.threadId),
        @IndexColumn(CommunityThreadViewEntity.Fields.viewedAt)
    })
})
public class CommunityThreadViewEntity extends BaseEntity { ... }
```

Unique keys look like this — Estivate unique index only, no `uniqueConstraints` on `@Table`:

```java
@Entity
@Table(name = "course_user_note_comment")
@TableIndexes({
    @TableIndex(name = "idx_course_user_note_comment_date", columns = {
        @IndexColumn(CourseUserNoteCommentEntity.Fields.noteId),
        @IndexColumn(CourseUserNoteCommentEntity.Fields.createdAt),
        @IndexColumn(BaseEntity.Fields.id)
    }),
    @TableIndex(
        name = "uk_course_user_note_comment_uuid",
        type = IndexType.UNIQUE,
        columns = {
            @IndexColumn(CourseUserNoteCommentEntity.Fields.noteId),
            @IndexColumn(NanoIdEntity.Fields.uuid)
        })
})
public class CourseUserNoteCommentEntity extends NanoIdEntity { ... }
```

Imports:

```java
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.Annotations.TableIndexes;
```

Use `Fields.*` (from `@FieldNameConstants`) for column names on `@IndexColumn`, including inherited ones like `BaseEntity.Fields.id`.

`IndexType`: `DEFAULT`, `UNIQUE`, `PRIMARY`, `FULLTEXT` (default is `DEFAULT`).

`@Table` may still set `name` (and non-index / non-unique-constraint concerns). Keep indexes and uniqueness only on `@TableIndexes`.

## Avoid

```java
@Table(
    name = "chat_message",
    indexes = @Index(
        name = "idx_chat_message_channel_id_id",
        columnList = "channel_id,id"
    )
)
```

```java
// ❌ duplicate uniqueness — UNIQUE TableIndex already declares the same key
@Table(
    name = "course_user_note_comment",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_course_user_note_comment_uuid",
        columnNames = {"note_id", "uuid"}
    )
)
@TableIndexes({
    @TableIndex(
        name = "uk_course_user_note_comment_uuid",
        type = IndexType.UNIQUE,
        columns = {
            @IndexColumn(CourseUserNoteCommentEntity.Fields.noteId),
            @IndexColumn(NanoIdEntity.Fields.uuid)
        })
})
```

Do not duplicate the same index or unique key in both Jakarta `@Table` (`indexes` / `uniqueConstraints`) and `@TableIndexes`.

## Checklist

- [ ] No `indexes = @Index(...)` (or `indexes = { ... }`) on `@Table`
- [ ] No `uniqueConstraints = …` on `@Table` when the same columns are already a `@TableIndex(type = IndexType.UNIQUE, …)`
- [ ] Indexes declared with `@TableIndexes({ @TableIndex(...) })`
- [ ] Columns reference `Entity.Fields.x`, not raw SQL column strings when Fields exist
- [ ] Unique indexes use `type = IndexType.UNIQUE`
