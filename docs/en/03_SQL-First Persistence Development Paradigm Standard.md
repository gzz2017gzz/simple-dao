# 03 SQL-First Persistence Development Paradigm Standard

SimpleDAO proposes and implements a brand-new persistence development paradigm: with **SQL-First** as its core philosophy, native database access interfaces as its foundation, and characterized by **extremely low intrusion, ultra-thin encapsulation, and high business code density**, it unifies data operation paradigms across all languages.  
It does not build an ecosystem, does not reinvent the wheel, does not rely on upper-layer containers, and only provides a set of **replicable, portable, and implementable** engineering methodologies.

---

## I. Core Principles (Pre-requisite Consensus)

1. **SQL Sovereignty Principle**: Developers have full sovereignty to see, modify, and optimize the final executed SQL. The framework does not hide, distort, or fragment SQL.
2. **No-Added-Nuisance Principle**: The framework only eliminates boilerplate code and passes through native errors. It does not create any framework-specific errors, invent private syntax, or introduce complexity without business value.
3. **Minimal Encapsulation Principle**: Encapsulation only aims to "reduce repetitive work" without over-abstraction. **Framework presence < 5%, business code density ≥ 95%**.
4. **Convention Over Configuration Principle**: Core capabilities are implemented with zero configuration through conventions; special scenarios are handled by lightweight annotations.
5. **White-Box Extensibility Principle**: Any extension (data permissions, pagination, data masking, etc.) should be implemented at the business layer using the language's native approach, rather than delving into the framework's internals to hack interceptors.

---

## II. MUST Implement (Establish)

1. **Zero-Code Single-Table CRUD**  
   - By inheriting/composing `BaseDao`, an empty class gains full single-table capabilities (`save`/`saveBatch`/`update`/`updateNull`/`delete`/`findById`/`list`/`page`/`count`/`exists`).  
   - Only annotations (`@Table`/`@Id`) are needed to specify the table name and primary key; everything else is automatically adapted.

2. **Unified and Safe Condition Construction**  
   - Provide a standardized `Condition` builder supporting `AND`/`OR`/`LIKE`/`IN`/`NULL` checks / time ranges, with parameters strictly separated from SQL (prepared statement placeholders).  
   - Do not rely on framework-private syntax (e.g., OGNL/XML tags); use the language's native logic (`if`/`else`) to assemble conditions.  
   - Use the same condition tool for both single-table and join queries, eliminating mental fragmentation.

3. **Free Native SQL Execution**  
   - Support writing complete native SQL (`JOIN`/subqueries/`UNION`/aggregations) and executing it directly, with automatic mapping to entities/VOs.  
   - SQL can be copied directly to a database client for execution; support IDE syntax highlighting/formatting/completion; during debugging, the complete SQL with parameters can be viewed directly.

4. **Standardized Pagination Logic**  
   - Provide a unified `page` method that automatically handles "total count query + paginated query" with intelligent `COUNT SQL` parsing.  
   - No reliance on third-party plugins; pagination parameters are uniformly managed; the returned result includes the data list and pagination metadata.

5. **Automated Auditing and Soft Delete**  
   - Automatically populate audit fields (`create_time`/`create_by`/`update_time`/`update_by`) during insert/update/delete, and automatically recognize the soft delete field (`dr`).  
   - Delete operations default to soft delete (`dr=1`); if the `dr` field is absent, physical deletion is performed.

6. **Automated Field Mapping**  
   - Automatically map between **entity camelCase names and database underscore names**; support overriding special field mappings with lightweight annotations (`@Column`).  
   - No need for manual result mapping; results from multi-table joins can be automatically mapped to VOs.

7. **Transparent Error Propagation**  
   - Do not encapsulate, transform, or hide native database errors; all errors are either SQL syntax errors, parameter errors, or business errors.  
   - Error messages point directly to the root cause; troubleshooting does not require parsing framework internals.

---

## III. MUST NOT Implement (Break)

- Do NOT implement second-level caching (use Redis instead)  
- Do NOT implement lazy loading (use explicit precise queries instead)  
- Do NOT implement cascading operations (use explicit `JOIN`s instead)  
- Do NOT implement complex object mapping (write SQL manually instead)  
- Do NOT bind a distributed ID generator (use an independent ID service instead)

---

## IV. ALLOW Extensions (Complement)

- **Data Permissions**: Support non-intrusive permission control via annotations + AOP, without modifying native SQL and without relying on interceptors.  
- **Multiple Data Sources**: Compatible with the language's native multi-data-source solutions; no additional framework adaptation required.  
- **Database Compatibility**: Support adapting to special database syntax differences through extensions, transparent to the business layer.  
- **Code Generation**: Support generating standard code (entities, conditions, DAOs, VOs) that is white-box modifiable, compatible with team conventions, and not tied to a low-code platform.

---

## V. Implementation Example (Language-Agnostic Pseudocode)

```java
// Entity
@Table("user")
class User {
    @Id("snow")
    Long id;
    String name;
    Integer age;
}

// DAO
class UserDao extends BaseDao<User> {}

// Usage
UserDao dao = new UserDao();
dao.save(user);
User u = dao.findById(1L);
List<User> list = dao.list(cond);
Page<User> page = dao.page(cond);

// Condition construction
class UserCond extends Condition {
    String name;
    Integer ageMin;
    @Override
    protected void addCondition() {
        and("name LIKE ?", name, 3);
        and("age > ?", ageMin);
    }
}

// Join query
String sql = "SELECT u.*, d.name dept_name FROM user u LEFT JOIN dept d ON u.dept_id = d.id";
List<UserVO> list = dao.list(sql, cond, UserVO.class);
```

---

**End of Paradigm Standard.**

> **Related Documents**  
> - To understand the origin of SQL-First thinking, see **[01 The SQL-First Manifesto](01_The%20SQL-First%20Manifesto.md)**  
> - For a comprehensive comparison between SQL-First and ORM, see **[02 Full-Scenario Comparison Matrix](02_Full-Scenario%20Comparison%20Matrix.md)**  
> - For Java implementation details and real-world cases, see **[04 SimpleDAO Technical Whitepaper](04_SimpleDAO%20Technical%20Whitepaper.md)**  
> - To implement SQL-First in your language, see **[05 SQL-First Paradigm Porting Guide](05_SQL-First%20Paradigm%20Porting%20Guide.md)**