
# SimpleDAO - A Revolution in Enterprise Data Access

> **SQL-First · White-box Transparency · Non-ORM Framework**  
> **Capability limit = SQL expressiveness limit = Spring ecosystem limit**

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/java-21%2B-orange)](https://www.oracle.com/java/)
[![Production Ready](https://img.shields.io/badge/production-ready-green)](https://github.com/simpledao/simpledao)
[![Performance](https://img.shields.io/badge/performance-%3D%20Spring%20JDBC-brightgreen)]()
[![Multi Language](https://img.shields.io/badge/languages-8%2B-blue)]()

---

## 🚀 Quick Start

> 👉 Try it now: [SimpleDAO Complete Examples](https://github.com/gzz2017gzz/simple-dao-demo/blob/master/readme-en.md)

> 👉 API Cheatsheet: [SimpleDAO API](api-en.md)

---

## 📋 Table of Contents

- [Core Philosophy](#-core-philosophy-sql-first-white-box-transparency)
- [Capability Boundaries](#-capability-boundaries-no-ceiling)
- [Why We Are Different](#-why-we-are-different-abandoning-traditional-orm)
- [Feature Highlights](#-feature-highlights-production-grade-coverage)
- [Performance Data](#-performance-data--spring-jdbc-no-argument)
- [Ecosystem Integration](#-ecosystem-integration-100-spring-native)
- [Coexistence with MyBatis](#-zero-cost-migration-coexist-with-mybatis)
- [Quick Start](#-quick-start)
- [Core Value Summary](#-core-value-summary)
- [FAQ](#-faq)
- [Further Reading](#-further-reading)

---

## ✨ Core Philosophy: SQL-First, White-box Transparency

**The database only understands SQL.** For 50 years, SQL has been the only universal query language for relational databases. Any framework that attempts to "encapsulate", "replace", or "generate" SQL ultimately becomes an additional burden on developers.

SimpleDAO does not invent new SQL dialects, does not encapsulate SQL keywords, does not distort SQL semantics, and does not invent new error systems. The SQL you write by hand is the exact SQL executed in the database – the framework does not rewrite, intercept, or black-box process it.

```java
// Single-table operations: extend BaseDao, zero code
@Repository
public class UserDao extends BaseDao<User> {
    // Empty class gains full CRUD capabilities
}

// Join queries: native SQL, direct and efficient
private static final String JOIN_SQL = """
    SELECT u.*, d.dept_name, r.role_name
    FROM user u
    LEFT JOIN dept d ON u.dept_id = d.id
    LEFT JOIN user_role ur ON u.id = ur.user_id
    LEFT JOIN role r ON ur.role_id = r.id
    """;

// Same API, consistent experience
public Page<UserVO> pageJoin(UserCond cond) {
    return page(JOIN_SQL, cond, UserVO.class);
}
```

---

## 🚀 Capability Boundaries: No Ceiling

### 1. SQL Capability Unlimited

SimpleDAO has **no upper limit** on complex scenarios – its ceiling is the **expressiveness of SQL** itself.

- Single-table CRUD, multi-table joins, nested subqueries, aggregate reports, window functions, recursive CTEs, stored procedure calls… **if the database SQL can write it, SimpleDAO can execute it directly**.
- No pre‑set “which SQL features the framework supports” – because the framework never parses SQL. Whatever you write, the database executes.
- No “framework blind spots”, no “fallback design” for certain scenarios.

### 2. Spring Ecosystem Extension Unlimited

SimpleDAO is not a third‑party wrapper around Spring JDBC; it is a **native extension and enhancement of Spring JDBC** itself.

- **Transactions**: Use `@Transactional` directly, 100% compatible with Spring transaction management.
- **Multiple Data Sources**: Use Spring’s multi‑data source configuration directly, no adapter needed.
- **Connection Pools**: Any pool (HikariCP, Druid, etc.) works with standard Spring Boot configuration.
- **Caching**: Spring Cache annotations on the service layer, independent of DAO.
- **AOP**: Data permissions, multi‑tenancy, logging – extend non‑invasively with Spring AOP.
- **Monitoring**: Spring Boot Actuator monitors data source health and SQL execution.

**Conclusion: SimpleDAO’s capability ceiling = Spring ecosystem’s ceiling – no artificial limits.**

### 3. Performance Aligned with Spring JDBC

- **Reflection at startup, zero reflection at runtime**: All metadata is parsed once at startup and cached (dual-reference cache). No reflection overhead at runtime.
- **Metadata read only for single‑table operations, never for joins**: For joins, SimpleDAO uses the SQL you provide directly; it does not parse any entities or annotations.
- **Condition‑building overhead negligible**: The cost of string concatenation + `List.add` is negligible compared to database network and disk I/O. Moreover, condition‑building is a business necessity – any framework has to do it.
- **Ultimate execution layer is `JdbcTemplate`**: No interceptors, no dynamic proxies, no cache pollution.

**SimpleDAO performance ≈ Spring JDBC performance. Any doubt is technically unjustified.**

---

## 🎯 Why We Are Different: Abandoning Traditional ORM?

### 📌Simple Scenarios (CRUD, pagination, conditions)

| Dimension | JPA/Hibernate | MyBatis Ecosystem | SimpleDAO |
|-----------|:-------------:|:-----------------:|:---------:|
| Single-table CRUD | Requires Entity + Repository | Requires SQL or MP | **Extend empty class, zero code** |
| Configuration volume | Medium (annotations + interfaces) | High (XML + interfaces + entities) | **Zero (only two annotations)** |
| Dynamic conditions | Criteria API, verbose | XML tags, one line becomes three | **One line of add** |
| Condition uniformity | Two sets (JPQL + Criteria) | Two sets (XML + annotation SQL) | **One universal set** |
| Audit fields | Manual or Listener | Manual or MP plugin | **Auto‑filled** |
| Logical delete | Requires @SQLDelete | Requires plugin or manual | **Auto‑handled** |
| Pagination | Pageable, SQL uncontrollable | PageHelper or manual | **One-line page()** |

---

### 📌Complex Scenarios (joins, reports, SQL capabilities)

| Dimension | JPA/Hibernate | MyBatis Ecosystem | SimpleDAO |
|-----------|:-------------:|:-----------------:|:---------:|
| Lines of code | Medium | High | **1/4 ~ 1/3** |
| SQL capability | ~1/3 of SQL | Full, but verbose tags | **Full, unlimited** |
| Reports / subqueries | Almost unusable | Extremely painful | **Native support, condition reuse** |
| Complex joins | Unmaintainable, fallback to native | Tag hell, SQL fragmented | **Complete SQL written directly** |
| Result mapping | Automatic but black‑box | resultMap, write twice | **Auto‑mapped to VO** |
| Extensibility | Black‑box, hard to extend | Dig into interceptors, break on upgrade | **White‑box, AOP everywhere** |
| SQL optimization sovereignty | Completely lost | Half‑hidden | **Full control** |
| Data permissions | Listener + black‑box | Interceptor, 20 lines not touching business | **AOP, 10 minutes** |
| Field desensitization | Tied to entity | Write TypeHandler | **AOP, bound to behavior** |
| SQL debugging | Dig through proxy logs | Manual placeholder replacement | **Copy and execute** |

---

### 📌Comprehensive Capabilities (execution, extension, risk, ecosystem)

| Dimension | JPA/Hibernate | MyBatis Ecosystem | SimpleDAO |
|-----------|:-------------:|:-----------------:|:---------:|
| Learning curve | Extremely high | High | **Very low (know SQL, you're good)** |
| Performance | ~95% | 95-97% | **99%, ceiling** |
| All databases (including domestic) | Wait for Dialect, endless | Plugins one‑by‑one | **Works with any JDBC driver, full support** |
| Spring ecosystem红利 | Usable | Bridge/adapter needed | **Out‑of‑the‑box: transactions, multi‑data source, cache…** |
| Extension mechanism | Listener/Callback | Interceptor, high barrier | **AOP, zero barrier** |
| Error sources | Framework + business | **31 kinds of self‑inflicted exceptions** + business | **Only database/business** |
| Community nature | Specification discussion group | **Victim alliance** | **No questions to ask** |
| Knowledge transfer | Limited to JPA ecosystem | Limited to MyBatis ecosystem | **Universal SQL, lifelong benefit** |
| AI‑friendly | Need to learn proprietary API | Need to learn tags and OGNL | **Zero private syntax, 70% less tokens** |

---

**📌SimpleDAO vs JPA: Removes SQL capability ceiling**

**📌SimpleDAO vs MyBatis: Removes XML shackles**

**📌SimpleDAO vs Spring JDBC: Automates manual condition‑building drudgery**

**📌Fills all the gaps of the three major players**

---

## ⚙️ Configuration

| Property | Default | Description |
| :--- | :--- | :--- |
| `simple-dao.show-sql` | `true` | Whether to print SQL with parameters |
| `simple-dao.logic-delete.field` | `dr` | Logical delete field name |
| `simple-dao.dialect` | auto‑detect | Database dialect: `mysql`, `postgresql`, `sqlserver`, `oracle` |

**Three‑level dialect fallback strategy**: explicit configuration > auto‑detection > MySQL fallback.

Automatically adapts pagination syntax for MySQL, H2, SQLite, PostgreSQL, SQL Server, Oracle out of the box.

Also supports all domestic databases with a JDBC driver.

---

## 🚀 Feature Highlights: Production‑Grade Coverage

### I. Development Efficiency (Eliminate Redundancy)

#### 1. End XML Configuration Hell
```java
@Table("sys_user")
public class User {
    @Id("snow")  // Snowflake primary key
    private Long id;
    private String userName;  // auto‑mapped to user_name
}
```
**Comparison**: No XML, no ResultMap, no association/collection tags.

#### 2. Unified API for Single‑table / Multi‑table
```java
// Single‑table
userDao.page(userCond);
// Multi‑table (same API)
userDao.page(joinSql, userCond, UserVO.class);
```
**Comparison**: Say goodbye to the two‑way mindset of MyBatis Plus (BaseMapper vs XML).

#### 3. Extremely Simplified Condition Building
```java
@Override
protected void addCondition() {
    and("name LIKE", name, 3);           // main table fuzzy
    and("age >=", ageMin);               // main table range
    in("id", ids);                       // IN clause
    add("AND r.refund_type IN ", types); // related table condition
    add("AND (t.start_date = CURDATE() OR t.war_date = CURDATE())", flag); // SQL snippet
}
```
**Comparison**: Say goodbye to MyBatis `<if>` nesting and OGNL expression errors.

#### 4. Standard Pagination (One Line)
```java
Page<User> page = userDao.page(cond);
// Automatically includes: dataList, rowCount, page info
// Intelligent COUNT SQL parsing to avoid subquery performance issues
```

#### 5. Flexible Update Strategies
```java
userDao.update(user);           // Update non‑null fields (90% of cases)
userDao.updateNull(user);       // Update all fields (including nulls)
userDao.update(user, condition);// Conditional update
```

#### 6. High‑Performance Batch Operations
```java
userDao.saveBatch(userList);    // Batch insert
userDao.replaceBatch(userList); // Batch replace (MySQL Upsert)
```

#### 7. Automatic Audit Fields
```java
userDao.save(user);   // Auto sets: id, createTime, createBy, dr=0
userDao.update(user); // Auto sets: updateTime, updateBy
```

### II. Data Security (Built from the Source)

#### 8. Eradicate SQL Injection
Full‑chain parameterized queries + `BeanPropertyRowMapper` type mapping, a type‑safe channel from Java to the database.

#### 9. Standardized Soft Delete
```java
userDao.delete(1, 2, 3);
// Has dr field → UPDATE SET dr=1
// No dr field → DELETE FROM
```

#### 10. Convenient Row Lock Control
```java
User user = userDao.findById(1, true); // FOR UPDATE
```

#### 11. Application‑Level Foreign Key Constraints (for Microservices)
```java
userDao.checkRef = true;  // Enable reference checking
// On delete, automatically query sys_table_ref config; if references exist, throw exception
```

#### 12. Prevent Full‑Table Operations
`update()` enforces a WHERE condition before execution; violation is immediately blocked.

### III. Production Adaptability (Elegant Solutions for Complex Scenarios)

#### 13. Weird Database Compatibility
Automatically detects and adapts to databases like OceanBase that do not support DELETE alias.

#### 14. Integrated Distributed Primary Key
```java
@Id("snow")
private Long id;  // Snowflake algorithm, supports reverse timestamp parsing
```

#### 15. Enforced Layered Architecture
Core methods in `BaseDao` are `protected`, syntactically forcing the Service layer as an intermediary.

### IV. Engineering Efficiency (Full‑Stack Code Generation)

#### 16. Full‑Stack Code Generator
Generated content: Entity + DAO + Service + Controller + Condition + VO + Frontend pages (Vue + Element) + Swagger annotations + unit test templates. **80% productivity increase**.

#### 17. White‑box Low‑Code
Generated code is standard Java, not black‑box magic. You can modify Freemarker templates, add custom templates, integrate with any tech stack, and perform Code Review.

---

## 📊 Performance Data: ≈ Spring JDBC

| Operation Type | SimpleDAO | Spring JDBC | MyBatis | JPA |
|----------------|-----------|-------------|---------|-----|
| Insert 1000 rows | 120 ms | 120 ms | 210 ms (+68%) | 350 ms (+180%) |
| Single‑table pagination | 43 ms | 43 ms | 80 ms (+78%) | 120 ms (+167%) |
| 5‑table join pagination | 109 ms | 108 ms | 180 ms (+64%) | 250 ms (+127%) |
| Complex report query | 196 ms | 195 ms | 350 ms (+75%) | 500 ms (+150%) |

*SimpleDAO performance is nearly identical to Spring JDBC; differences are within measurement error.*

---

## 🌱 Ecosystem Integration: 100% Spring Native

```java
// Transactions
@Transactional
public void businessMethod() {
    userDao.save(user);
    orderDao.save(order);
}

// Multiple Data Sources
@Bean
public DataSource masterDataSource() { ... }
@Bean
public DataSource slaveDataSource() { ... }

// Spring Cache
@Cacheable("users")
public User getUser(Long id) {
    return userDao.findById(id);
}

// AOP Extension
@Aspect
public class DataAuthAspect {
    @Around("@annotation(BusinessAuth)")
    public Object authCheck(ProceedingJoinPoint pjp) { ... }
}
```

**Conclusion: SimpleDAO is not “adapting to Spring” – it is part of Spring.**

---

## 🤝 Zero-Cost Migration: Coexist with MyBatis

This is a unique core advantage of SimpleDAO:

- For any existing project using MyBatis, **you don’t need to change a single line of old code**. Just add the SimpleDAO dependency and start using it immediately.
- MyBatis is bridged on top of Spring JDBC; SimpleDAO is built directly on Spring JDBC. They share the same data source and underlying Spring capabilities.
- **Old and new business logic are fully decoupled and non‑interfering**:
  - Existing business continues to use MyBatis
  - New business uses SimpleDAO
  - Gradually replace as you evolve the codebase
- New projects can be built directly on SimpleDAO, completely moving away from MyBatis’ patch ecosystem.

---

## 🏆 Core Value Summary

| Dimension | Traditional ORM | SimpleDAO |
|-----------|----------------|-----------|
| **Code volume** | Baseline | Reduce **60-80%** |
| **Development time** | Baseline | Shorten **50%** |
| **Learning cost** | 2-5 days | **20 minutes** |
| **Debugging experience** | Placeholder SQL, manual parameter replacement | **Complete SQL, copy‑paste‑run** |
| **Capability limit** | Framework constraint | **SQL’s limit = Spring’s limit** |
| **Extension barrier** | High (need to study source) | **Zero (Spring AOP)** |
| **Performance** | Overhead | **≈ Spring JDBC** |
| **Migration cost** | Rewrite | **Zero (coexists with MyBatis)** |

---

## 🤔 FAQ

**Q: Requires Java 21+, what about legacy projects?**  
**A: We chose Java 21 deliberately. The core is just string concatenation – downgrading to Java 8 takes only minutes. Let AI do it for you.**  
(Just change a few newer syntax constructs – you can even ask an AI to do the conversion.)  

**Q: Is migration from MyBatis to SimpleDAO expensive?**  
**A: Zero migration cost. SimpleDAO coexists seamlessly with MyBatis – you don't need to change any old code.**  
- After adding SimpleDAO dependency, your existing MyBatis mappers, XML, and plugins continue to run unchanged.  
- New business can be written directly with SimpleDAO, leaving old code untouched.  
- There is no “migration” – only “incremental usage”. You can replace at your own pace, or keep them together forever.

**Q: What is the difference between SimpleDAO and MyBatis Plus?**  
A: MyBatis Plus is a “semi‑ORM” – simple tables use object‑style operations, but complex queries fall back to XML. SimpleDAO is “SQL‑First” – both simple and complex use the SQL mindset, with a completely unified API.

**Q: Do I need to learn new syntax?**  
A: No. If you know SQL and Java, you already know SimpleDAO. No XML, OGNL, JPQL, or any other extra syntax.

**Q: Is it suitable for microservices?**  
A: Yes, exceptionally suitable. It is lightweight (only 3 core classes), has no external dependencies, and integrates perfectly with Spring Cloud.

**Q: Is performance really the same as Spring JDBC?**  
A: Yes. Reflection happens once at startup and is cached; zero reflection at runtime. Join queries never parse entities. The ultimate execution layer is `JdbcTemplate`. **No argument accepted.**

**Q: Does it support other languages?**  
A: Implementations exist for 8 major backend languages: Python, PHP, Go, C++, Node.js, Rust, C#, and more.

---

## 📚 Further Reading

- [📄 SimpleDAO Quick Start (run complete examples)](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme.md)

---

## 🤝 Join Us

SimpleDAO is changing the way developers interact with databases. If you:
- ✅ Are tired of complex frameworks
- ✅ Believe simplicity is powerful
- ✅ Value development efficiency and quality of life
- ✅ Are willing to share and contribute

Welcome to: ⭐ **Star** the project | 📚 **Read** the contribution guide | 🐛 **Report** issues | 💬 **Join** the discussion group

---

## 📄 License

Apache License 2.0

---

## 🎯 Final Words

SimpleDAO is not meant to be just another popular framework. It exists to prove one thing:

> **Technology can be simpler, development can be more enjoyable, and programmers can go home early.**

If you are also fed up with the torture of complex frameworks, give SimpleDAO a try.

We exist for those who:
- want to get work done efficiently,
- want to go home early to spend time with family,
- believe that simplicity is stronger than complexity.

---

**SimpleDAO: SQL-First, White‑box Transparency, Capability Unlimited.**  
**Leave time for life, not for the framework.**

---

*SimpleDAO has been running stably in production for over 3 years, handling millions of requests daily, serving more than a dozen enterprise clients.*
