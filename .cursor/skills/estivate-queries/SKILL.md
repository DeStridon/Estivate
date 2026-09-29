---
name: estivate-queries
description: >-
  Prefer Estivate extract helpers over verbose select + fetch + map
  chains when writing or reviewing Estivate SelectQuery code. Use when building
  queries, counts, distinct counts, single-row extracts (extractSingle not
  extractOptional.orElse(null); extractSingle not extractOptional.orElse(null)),
  single-column projections, formatting fluent query chains (one method per
  line), preferring AttributeGetter (Entity::getX) on joined queries instead of
  Entity.class + Fields.x, optional filters (eqIfNotBlank not if-isBlank-then-eq),
  or optimizing selectCountDistinct/extractCountDistinct patterns in Estivate or
  Sapience.
---

# Estivate queries

Prefer the dedicated `extract*` helpers on `SelectQuery` / `Context`. Avoid building a select clause then manually unpacking a `ResultTable` when a helper already returns the typed value.

## Single entity / nullable row

`extractSingle` maps the **first** result row (or `null` if none). Do **not** unwrap `Optional` with `.orElse(null)` — that is exactly what `extractSingle` returns.

| Need | Prefer | Avoid |
|------|--------|--------|
| Nullable entity | `extractSingle(context)` | `.extractOptional(context).orElse(null)` (with or without `.limit(1)`) |
| `Optional` entity | `extractOptional(context)` | `.limit(1).extractOptional(context)` |
| Typed / projected nullable | `extractSingle(context, X.class)` | `.extractOptional(context, X.class).orElse(null)` |

```java
// Prefer
CourseEntity course = Estivate.selectQuery(CourseEntity.class)
    .eq(BaseEntity.Fields.id, link.getCourseId())
    .extractSingle(context);

// Avoid — same result, needless Optional unwrap
CourseEntity course = Estivate.selectQuery(CourseEntity.class)
    .eq(BaseEntity.Fields.id, link.getCourseId())
    .extractOptional(context)
    .orElse(null);
```

Use `extractOptional` / `extractOptional` only when you actually need Optional API (`orElseThrow`, `isPresent`, `map`, …) — still without a redundant `.limit(1)`.

## Counts

| Need | Prefer | Avoid |
|------|--------|--------|
| Row count | `extractCountAll(context)` | `selectCountAll(...).fetch(context).asSingleLong()` |
| Distinct count | `extractCountDistinct(context, Entity.class, Fields.x)` or `extractCountDistinct(context, Entity::getX)` | `selectCountDistinct(...).fetch(context).asSingleLong()` |

Example — prefer:

```java
long memberCount = Estivate.selectQuery(UserCourseEntity.class)
    .eq(UserCourseEntity.Fields.courseId, courseId)
    .isNotNull(UserCourseEntity.Fields.userApprovalDate)
    .isNotNull(UserCourseEntity.Fields.courseApprovalDate)
    .extractCountDistinct(context, UserCourseEntity.class, UserCourseEntity.Fields.userId);
```

Not:

```java
long memberCount = Estivate.selectQuery(UserCourseEntity.class)
    .eq(...)
    .selectCountDistinct(UserCourseEntity.class, UserCourseEntity.Fields.userId)
    .fetch(context)
    .asSingleLong();
```

Optional variants: `extractOptionalCountAll`, `extractOptionalCountDistinct`.

## Single column / collections

| Need | Prefer |
|------|--------|
| One scalar | `extractOptional` / `extractSingle` with attribute or getter |
| List of values | `extractList(context, Entity::getX)` |
| Distinct list | `extractListDistinct(...)` |
| Unique values | `extractSet(context, Entity::getX)` |

Do not `extractList` full entities then map/count/distinct in Java when only one column or a count is needed.

## Filters & empty collections

- Optional string filters: `eqIfNotBlank(attribute, value)` — do **not** wrap `.eq(...)` in `if (value != null && !value.isBlank())` / `if (StringUtils.isNotBlank(value))`.
- Optional non-string filters: `eqIfNotNull`, `inIfNotEmpty`, etc.
- `IN` that must stay valid when empty: `inOrFalseIfEmpty` / `inOrNull` — never pass an empty collection to a raw `in`.

Prefer:

```java
.eqIfNotBlank(CourseEntity::getUuid, courseUuid);
```

Avoid:

```java
if (courseUuid != null && !courseUuid.isBlank()) query.eq(CourseEntity::getUuid, courseUuid);
```

## Style

- Prefer `AttributeGetter` (`Entity::getField`) or `Fields.x` over bare string column names.
- Keep predicates on the query; let `extractCount*` clone and clear selects / group by / order by / limit / offset.
- **Fluent chains: one method per line.** Never pack `.join*` / `.eq` / `.orderBy*` / `.extract*` / `.fetch` on a single long line. Put each piped call on its own indented line (including trailing `.map` / `.orElse` on the result).
- **Joins: prefer `AttributeGetter`.** After `.join*` / when the query has more than one entity, prefer `Entity::getField` over `.eq(Entity.class, Entity.Fields.x, …)` / `.orderBy*(Entity.class, Entity.Fields.x)` / `.select(Entity.class, …)`. Method references bind the owning class, so criteria and selects are less likely to target the wrong table. Same for `isNull` / `isNotNull` / `in*` / `lte` / `orderBy*` / projections on joined queries.

Prefer (joined query):

```java
UserEntity student = ChannelAccessService.requireAccepted(Estivate.selectQuery(UserEntity.class)
        .joinInner(UserEntity.class, UserCourseEntity.class)
        .eq(UserEntity::getUuid, input.getStudentUuid())
        .eq(UserCourseEntity::getCourseId, course.getId())
        .eq(UserCourseEntity::getType, UserCourseEntity.Role.STUDENT))
    .extractOptional(context)
    .orElseThrow(() -> new SapienceUnauthorizedException(ResourceEnum.User, input.getStudentUuid()));
```

```java
return Estivate.selectQuery(InvitationLinkUsageEntity.class)
    .joinInner(InvitationLinkUsageEntity.class, InvitationLinkEntity.class, InvitationLinkUsageEntity.Fields.invitationLinkId, BaseEntity.Fields.id)
    .eq(InvitationLinkUsageEntity::getUserId, userId)
    .isNotNull(InvitationLinkUsageEntity::getUsedAt)
    .eq(InvitationLinkEntity::getCourseId, courseId)
    .eq(InvitationLinkEntity::getType, InvitationLinkType.TARGETED)
    .orderByDesc(InvitationLinkUsageEntity::getUsedAt)
    .orderByDesc(InvitationLinkUsageEntity::getId)
    .extractOptional(context, ConsumedInvitationDraftRow.class)
    .map(ConsumedInvitationDraftRow::getTimelineDraft)
    .orElse(null);
```

Avoid (joined query — easy to omit / mismatch the class argument):

```java
.eq(UserEntity.class, NanoIdEntity.Fields.uuid, input.getStudentUuid())
.eq(UserCourseEntity.class, UserCourseEntity.Fields.courseId, course.getId())
```

On **single-entity** queries (no join), `Fields.x` or `Entity::getField` are both fine.

## Checklist

- [ ] Nullable single row? → `extractSingle` (not `.extractOptional(...).orElse(null)`, with or without `.limit(1)`)
- [ ] Typed nullable single? → `extractSingle` (not `.extractOptional(...).orElse(null)`)
- [ ] Count? → `extractCountAll` / `extractCountDistinct`
- [ ] One column? → `extractList` / `extractSet` / `extractOptional`
- [ ] No full-entity extractList for aggregate-only work
- [ ] Empty `IN` lists handled with `*OrFalseIfEmpty` / `*IfNotEmpty`
- [ ] Optional string filter? → `eqIfNotBlank` (not `if (…isBlank()) query.eq(...)`)
- [ ] Fluent query chains broken one method per line (not a single packed line)
- [ ] Joined queries use `Entity::getField` for criteria / order / select (not `Entity.class, Fields.x`)
