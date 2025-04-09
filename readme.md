# Estivate

Estivate is a framework complementing Hibernate, helping developers to create SQL queries


### 0. Foreword

This framework has been created to ease developers life, it tries to follow basic principles you should keep in mind to take best advantage of it:

** Intuitive **

Don't waist your time looking for all framework capacities, one single entry point : Estivate static class.
`Estivate.query()`, `Estivate.()`, `RegexBuilder.classMatch()`, `RegexBuilder.regexMatcher()` etc.
If it's not here, it just doesn't exist at all !

** Verbose **

Concepts of regex are sometimes hard to get as they are using symbols (*, ?, +, |) you don't want to remember, let's use words instead !

Sample:

```
Query query = Estivate.query(BasicEntity.class)
	.eq(BasicEntity.class, BasicEntity.Fields.parentId, 1)
	.gte(BasicEntity.class, BasicEntity.Fields.created, new Date());

```

> ^Hello World\s*!+

** Fluent interface **

Fluent interface, also known as method chaining or method cascading, is a design pattern making each method return the instance it belongs to, so that you can keep on calling instance methods.

```
Query query = Estivate.query(BasicEntity.class)
	.joinInner(BasicEntity.class, JoinedEntity.class) 
	.eq(BasicEntity.class, BasicEntity.Fields.parentId, 1)
	.gte(BasicEntity.class, BasicEntity.Fields.created, new Date());

```


### 1. Estivate Query

### 1.1 Building select query

### 1.2. Getting result

### 1.3. Criterion Library

#### 1.3.1. Native SQL Criteria

- Estivate::eq : `x = ?`
- Estivate::notEq : `x != ?`

- Estivate::lt : `x < ?`
- Estivate::gt : `x > ?`
- Estivate::lte : `x <= ?`
- Estivate::gte : `x >= ?`
- Estivate::between : `x between ? and ?`

- Estivate::in : `x in (?, ?, ?)`
- Estivate::notIn : `x not in (?, ?, ?)`

- Estivate::like : `x like ?`
- Estivate::notLike : `x not like ?`

- Estivate::matchAgainst : `match(x) against (?)`
- Estivate::notMatchAgainst : `not match(x) against (?)`

- Estivate::isNull : `x is null`
- Estivate::isNotNull : `x is not null`

- Estivate::inSubQuery : check exists in subquery
- Estivate::notInSubQuery : check doesn't exist in subquery

- Estivate::nativeCriterion : allow you to append native sql to query


#### 1.3.2. Extended Criteria
Criterions not supported natively by SQL, but wrapped by Estivate to feel like it does.

- Estivate::eqOrNull : `(x = ? OR x IS NULL)`
- Estivate::ltOrNull : `(x < ? OR y IS NULL)`
- Estivate::gtOrNull : `(x > ? OR y IS NULL)`
- Estivate::lteOrNull : `(x <= ? OR y IS NULL)`
- Estivate::gteOrNull : `(x >= ? OR y IS NULL)`

- Estivate::likeStartsWith : `x like ?` (add % at the beginning of the string)
- Estivate::likeEndsWith : `x like ?` (add % at the end of the string)
- Estivate::likeContains : `x like ?` (add % at the beginning and end of the string)
- Estivate::notLikeStartsWith : `x not like ?` (add % at the beginning of the string)
- Estivate::notLikeEndsWith : `x not like ?` (add % at the end of the string)
- Estivate::notLikeContains : `x not like ?` (add % at the beginning and end of the string)

- Estivate::likeIn : `(x like ?) OR (x like ?) OR (x like ?) ...` (add % at the beginning and end of the string)
- Estivate::notLikeIn : `(x not like ?) AND (x not like ?) AND (x not like ?) ...` (add % at the beginning and end of the string)
- Estivate::likeStartsWithIn : `(x like ?) OR (x like ?) OR (x like ?) ...` (add % at the beginning of the string)
- Estivate::notLikeStartsWithIn : `(x not like ?) AND (x not like ?) AND (x not like ?) ...` (add % at the beginning of the string)
- Estivate::likeEndsWithIn : `(x like ?) OR (x like ?) OR (x like ?) ...` (add % at the end of the string)
- Estivate::notLikeEndsWithIn : `(x not like ?) AND (x not like ?) AND (x not like ?) ...` (add % at the end of the string)
- Estivate::likeContainsIn : `(x like ?) OR (x like ?) OR (x like ?) ...` (add % at the beginning and end of the string)
- Estivate::notLikeContainsIn : `(x not like ?) AND (x not like ?) AND (x not like ?) ...` (add % at the beginning and end of the string)
- Estivate::matchAgainstIn : `(match(x) against (?)) OR (match(x) against (?)) OR (match(x) against (?)) ...`
- Estivate::notMatchAgainstIn : `(not match(x) against (?)) AND (not match(x) against (?)) AND (not match(x) against (?)) ...`


#### 1.3.3. Optional Criteria
Criterions that are activated only if the value is not null (IfNotNull suffix) or is not empty (IfNotEmpty suffix)

- Estivate::eqIfNotNull : `x = ?` (if value is not null, nothing happens otherwise)
- Estivate::ltIfNotNull : `x < ?` (if value is not null, nothing happens otherwise)
- Estivate::gtIfNotNull : `x > ?` (if value is not null, nothing happens otherwise)
- Estivate::lteIfNotNull : `x <= ?` (if value is not null, nothing happens otherwise)
- Estivate::gteIfNotNull : `x >= ?` (if value is not null, nothing happens otherwise)
- Estivate::betweenIfNotNull : `x between ? and ?` (if no value is null, nothing happens otherwise)

- Estivate::likeIfNotNull : `x like ?` (if value is not null, nothing happens otherwise)
- Estivate::likeStartsWithIfNotNull : `x like ?` (if value is not null, nothing happens otherwise)
- Estivate::likeEndsWithIfNotNull : `x like ?` (if value is not null, nothing happens otherwise)
- Estivate::likeContainsWithIfNotNull : `x like ?` (if value is not null, nothing happens otherwise)

- Estivate::inIfNotEmpty : `x in (?, ?, ?)` (if list is not null and not empty, nothing happens otherwise)
- Estivate::notInIfNotEmpty : `x not in (?, ?, ?)` (if list is not null and not empty, nothing happens otherwise)

- Estivate::likeInIfNotEmpty : `(x like ?) OR (x like ?) OR (x like ?) ...` (if list is not null and not empty, nothing happens otherwise)
- Estivate::likeStartsWithInIfNotEmpty : `(x like ?) OR (x like ?) OR (x like ?) ...` (if list is not null and not empty, nothing happens otherwise)
- Estivate::likeEndsWithInIfNotEmpty : `(x like ?) OR (x like ?) OR (x like ?) ...` (if list is not null and not empty, nothing happens otherwise)
- Estivate::likeContainsInIfNotEmpty : `(x like ?) OR (x like ?) OR (x like ?) ...` (if list is not null and not empty, nothing happens otherwise)
- Estivate::matchAgainstInIfNotEmpty : `(match(x) against (?)) OR (match(x) against (?)) OR (match(x) against (?)) ...` (if list is not null and not empty, nothing happens otherwise)
- Estivate::notMatchAgainstInIfNotEmpty : `(not match(x) against (?)) AND (not match(x) against (?)) AND (not match(x) against (?)) ...` (if list is not null and not empty, nothing happens otherwise)

### 1.4. Join
Estivate aims to offer simplicity and control over query joins. 

#### 1.4.1. Automated Join

- Query::joinInner
- Query::joinLeft
- Query::joinRight
- Query::joinOuter

#### 1.4.2. Manual Join

- Query::joinInner
- Query::joinLeft
- Query::joinRight
- Query::joinOuter
- Query::join
- Estivate::join 

### 1.5. Aggregator mecanisms



### 2. Setup

#### 2.1. Context
You can execute your query in the context, enabling you to get your results


### 3. Empower entities

#### 3.1. VirtualForeignKey
You can use this annotation to explicit link between entities you have in your model.

#### 3.2. CachedEntity

#### 3.3. @InsertDate & @UpdateDate
Field with @InsertDate annotation will be filled with current date when inserted.
Field with @UpdateDate annotation will be filled with current date when updated.

#### 3.4. Legacy annotations management
Following legacy annotations are handled by Estivate 
- PrePersist  
- PreUpdate
- PostPersist
- PostUpdate 


### 4. Advanced
#### 4.1. NameMapper

### 5. License
The source code is licensed under the MIT license, which you can find in the MIT-LICENSE.txt file.
	