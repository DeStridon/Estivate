# Estivate Framework

[![Java](https://img.shields.io/badge/Java-8+-blue.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-orange.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](MIT-License.txt)

**Estivate** is a powerful, lightweight Java framework that complements Hibernate by providing a fluent, type-safe SQL query builder with advanced features for database operations, entity management, and performance optimization.

Estivate is a framework complementary to Hibernate, helping developers to create SQL queries that would 


### 0. Features

- **Fluent Query Builder**: Chainable, readable SQL query construction
- **Centralized Catalog**: Exhaustive feature listing from one class
- **Type-Safe Operations**: Compile-time safety with generic types 
- **Advanced Joins**: Support for INNER, LEFT, RIGHT, and OUTER joins
- **Subquery Support**: Complex nested queries with aliasing
- **Performance Optimization**: Built-in caching, index hints
- **Flexible Entity Mapping**: JPA annotations with custom extensions
- **Database Agnostic**: Works with any JDBC-compliant database


### 1. Setup context

### 2. Query execution

#### 2.1 Basic select query

#### 2.2. Getting result

#### 2.3. Criterion Library

##### 2.3.1. Native SQL Criteria

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


##### 2.3.2. Extended Criteria
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


##### 2.3.3. Optional Criteria
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

### 3. Join
Estivate aims to offer simplicity and control over query joins. 

#### 3.1. Manual Join

- Query::joinInner
- Query::joinLeft
- Query::joinRight
- Query::joinOuter
- Query::join
- Estivate::join 


#### 3.2. Automated Join

- Query::joinInner
- Query::joinLeft
- Query::joinRight
- Query::joinOuter

- @VirtualForeignKey



#### 3.3. Extended join

### 4. Performances

#### 4.1. Differential Update


### 5. Misc

#### 5.1. @InsertDate & @UpdateDate
Field with @InsertDate annotation will be filled with current date when inserted.
Field with @UpdateDate annotation will be filled with current date when updated.

#### 5.2. Legacy annotations management
Following legacy annotations are handled by Estivate 
- PrePersist  
- PreUpdate
- PostPersist
- PostUpdate 

### 6. Advanced configuration


### 7. License
The source code is licensed under the MIT license, which you can find in the MIT-LICENSE.txt file.
	