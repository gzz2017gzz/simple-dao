# 01 The SQL-First Manifesto
## The Unix Philosophy of the Database Access Layer

Version: 1.0  
Release Date: February 12, 2026  
Author: SimpleDAO Thought Collective  
Status: Forever in Progress

---

## Preface: The End of Frameworks is Invisibility

**The best state of technology is when you don't notice its existence.**

A good doorknob doesn't tell your hand how to grip it; it's just there. You push, and the door opens.

A good database access layer should be the same: you think "I want to query users and their departments," your fingers type SQL, you hit Enter, and the data comes back.

No XML to configure, no Mapper to write, no `@OneToMany` to figure out, no N+1 problems to firefight.

**This is the SQL-First paradigm.**

It is not a new framework. It is an answer that has existed for 50 years, yet was forgotten for 20.

---

### The Ultimate Measure of Technology
How do we judge a technology? Not by how "advanced" it is or how many features it has, but by whether it **reduces cognitive load** and **lowers usage costs**. The baseline: it should help you write less code, not force you to write more code for its own sake.

**The higher the business code density, the better. The lower the framework's presence, the better.** A framework should be like air—present but unnoticed; like an assistant—helpful but not intrusive.

**Doing no evil is the greatest good. Adding no unnecessary complexity is the greatest good.** The greatest evil a framework can commit is to create problems that never existed.

---

## Chapter 1: Thirty Years of Detours – How ORM Went from "Solution" to "Problem Itself"

### 1.1 The Forgotten Consensus
In 1974, IBM's Donald Chamberlin and Raymond Boyce invented SQL. Its design goal was crystal clear: **to let humans operate on the relational model in the most natural language possible.**

From that day on, databases had their native tongue. Not Java, not Python, not C#.

It was SQL.

### 1.2 Impedance Mismatch: A Manufactured Problem
In the 1990s, object-oriented programming swept the industry. Developers looked at tables and rows in the database, then at classes and objects in their code, and felt a sense of anxiety: **"They don't look the same."**

This anxiety was named "impedance mismatch." And then a vast industry was born: ORM frameworks, training courses, technical books, consulting services... **But no one stopped to ask: does this gap truly exist?**

### 1.3 The Three Original Sins of ORM

**Sin One: Inventing Dialects**  
SQL is an international standard. ORMs insist on inventing their own dialects: JPQL/HQL, Criteria API, MyBatis dynamic SQL. Each dialect requires dedicated learning, and the knowledge gained is non-transferable to the next project or the next language.

**Sin Two: Distorting Semantics**  
The core of a relational database is **sets**. A SELECT returns a **set of rows**.  
The core of ORM is **object graphs**. Frameworks laboriously disguise sets as objects, then wire objects into graphs.

This distortion leads to:
- N+1 queries: fetching 100 users, then fetching each user's department → **101 queries**
- Cartesian explosions: joining 3 tables, each with 100 rows → **1 million result rows**
- Caching strategies: introducing second-level caches and query caches to solve the above → **complexity transfer**

**Sin Three: Transferring Errors**  
Originally, there were only two kinds of errors: SQL syntax errors and incorrect parameters. ORM introduces a third category: **framework-manufactured errors**.

- MyBatis: Mapper binding failures, ResultMap configuration errors, OGNL expression syntax errors – **22 categories**
- JPA: persistence context state confusion, cascading operations causing accidental deletions – **cold cases without closure**

Developers spend 30% of their time solving "framework problems" instead of business problems.

---

## Chapter 2: The SQL-First Paradigm – Four Laws of Returning to Essence

### 2.1 Law One: Objectify Single-Table Operations, But Stop There

**Inherit and you're done. Zero code.**
```java
@Repository
public class UserDao extends BaseDao<User> {
    // Empty class, all single-table CRUD methods are ready
}
```
**Clear Boundaries**:
- Automatically generates INSERT/UPDATE/DELETE/SELECT BY ID
- Automatically populates audit fields (create_time, create_by, dr...)
- Automatically maps camelCase to underscores
- **Stop right there**

What we **don't** do:
- ❌ No cascading (you should use JOIN)
- ❌ No lazy loading (you should use explicit queries)
- ❌ No automatic flush (you should control transaction boundaries)

**Let automation handle automation, and let manual control remain manual.**

---

### 2.2 Law Two: Express Join Queries in SQL – No Distortion, No Wrapping, No Invented Dialects

**SQL is the only correct expression for join queries.**

```java
private static final String SQL = """
    SELECT u.*, d.name AS dept_name, r.name AS role_name
    FROM user u
    LEFT JOIN dept d ON u.dept_id = d.id
    LEFT JOIN user_role ur ON u.id = ur.user_id
    LEFT JOIN role r ON ur.role_id = r.id
    """;

public Page<UserVO> pageJoin(UserCond cond) {
    return page(SQL, cond, UserVO.class);
}
```

**Features**:
- ✅ IDE syntax highlighting, SQL formatting, table/column name completion
- ✅ Can be copied directly to Navicat/DataGrip for validation
- ✅ Execution plan analysis and index optimization – framework interference: zero
- ✅ 12-table join? Write it clearly in SQL, and it's done.

**SQL-First is not "allowing SQL"; it's "encouraging SQL and making writing SQL a pleasure."**

---

### 2.3 Law Three: Simplify Condition Construction – Use Your Mother Tongue, Not a Dialect

**BaseCondition: Not a query builder, but a SQL fragment assembler.**

```java
public class UserCond extends BaseCondition {
    @Override
    protected void addCondition() {
        and("name LIKE ?", name, 3);                 // main table field
        and("age > ?", ageMin);                      // main table operator
        add("AND d.dept_name LIKE ?", deptName, 3);  // related table field
        add("AND EXISTS (SELECT 1 FROM order o WHERE o.user_id = t.id)", 
            hasOrder);                               // subquery fragment
    }
}
```

**Comparison with ORM:**

| Framework | Dynamic Condition Syntax | Learning Cost | Debugging Difficulty |
|-----------|--------------------------|----------------|----------------------|
| JPA Criteria | `cb.and(cb.greaterThan(root.get("age"), ageMin))` | High | Very High |
| MyBatis | `<if test="ageMin != null">AND age > #{ageMin}</if>` | Medium | High (need logs to reconstruct SQL) |
| SQL-First | `and("age > ?", ageMin)` | Zero | None (see SQL directly at breakpoint) |

**Because you already know SQL.**

---

### 2.4 Law Four: Share the Same Condition Tool for Both Single-Table and Join Queries – No Fragmentation, No Mental Switching

This is the most subtle and most powerful design of the SQL-First paradigm.

**Single-table:**
```java
UserCond cond = UserCond.builder().name("张").build();
List<User> list = userDao.list(cond);  // SELECT * FROM user t WHERE t.name LIKE ?
```

**Join query:**
```java
UserCond cond = UserCond.builder().deptName("市场").build();
List<UserVO> list = userDao.page(JOIN_SQL, cond, UserVO.class);  // JOIN + WHERE d.dept_name LIKE ?
```

**The same Cond class, the same `addCondition()` method, the same parameter collection mechanism.**

Developers do not need to:
- ❌ Learn two sets of query APIs (MyBatis Plus's QueryWrapper vs XML)
- ❌ Switch mental models (object for single-table, tags for joins)
- ❌ Maintain two copies of condition logic (change in one place, forget the other)

**Uniformity is a higher form of simplicity.**

---

## Chapter 3: SQL Optimization Sovereignty – Who Controls SQL Controls Performance

**SQL Optimization Sovereignty**: The development team's ability to **see, understand, modify, and optimize** the final executed SQL.

### 3.2 Sovereignty Spectrum

| Framework | Sovereignty Level | Manifestation |
|-----------|-------------------|---------------|
| JPA/Hibernate | ❌ Completely lost | SQL generated by framework; developers can only influence indirectly via hints |
| MyBatis | ⚠️ Half-obscured | SQL visible but fragmented by XML tags; optimization requires reconstructing full SQL first |
| SQL-First | ✅ Full control | Complete SQL written directly; can be `EXPLAIN`ed, indexed, rewritten immediately |

### 3.3 A Real-World Case

**Requirement**: 12-table join + 60 dynamic conditions, response time under 1 second.

- **JPA**: Infeasible. The black-box-generated SQL is unpredictable and unoptimizable.
- **MyBatis**: Barely feasible, but with a painful optimization cycle:
  1. Break the 12-table JOIN into multiple `<include>` fragments
  2. Write 60 conditions as 60 `<if>` tags
  3. When performance issues arise, manually reconstruct the full SQL from the fragments
  4. Copy to database tool, `EXPLAIN`, add indexes
  5. Break the optimized SQL back into fragments and put them back into XML
  6. **Repeat steps 3-5 for every optimization**
- **SQL-First**:
  1. Full SQL is written directly in a Java string
  2. When performance issues arise, copy the entire SQL to a database tool
  3. `EXPLAIN`, add indexes, rewrite the SQL
  4. **Paste the optimized SQL back into the code**

**Optimization cycle shortened by 80%, willingness to optimize increased by 200%.**

---

## Chapter 4: Do No Unnecessary Harm – The Ultimate Morality of a Framework

### 4.1 The Moral Philosophy of Frameworks

- **Principle of Non-Maleficence**: A framework should not create errors that did not exist before.
- **Principle of Transparency**: A framework should not hide the core execution path.
- **Principle of Clear Responsibility**: SQL errors belong to SQL, parameter errors belong to parameters, framework errors belong to the framework – but a framework should not have its own errors.

### 4.2 The Three Levels of Good

**Level One: Solve Problems**  
> The framework saves developers from repetitive code.  
> Example: `JdbcTemplate` simplifies JDBC boilerplate.

**Level Two: Do Not Create Problems**  
> The framework solves problem A without introducing problems B, C, or D.  
> Example: Spring JDBC – no XML, no dialects, no self-inflicted errors.

**Level Three: Make Problem-Solvers Feel Respected**  
> The framework not only avoids adding complexity but also treats developers as **adults who know SQL and understand business**.  
> Example: SimpleDAO – returns full SQL optimization sovereignty to developers.

### 4.3 Extensions Must Be White-Box

When extension features are needed (e.g., data permissions, pagination, data masking), the framework should provide a **white-box** approach, allowing developers to implement them directly with business code – not forcing them to dig into the framework's internals to hack interceptors or rewrite execution paths.

MyBatis's ecosystem is bloated precisely because its core execution is a black box. Features like pagination, masking, and permissions must be implemented by brutally modifying SQL via interceptors: 20 lines of code that barely touch business logic, and break with every version upgrade.

Spring JDBC is the complete opposite. It is a **data-oriented minimalist design**, making SQL execution fully white-box. Developers can clearly see every database interaction, and extensions can be implemented directly in the business layer using AOP or native JDBC – no black-box magic. It is precisely this minimalism and transparency of Spring JDBC that laid the foundation for SimpleDAO's birth.

Standing on the shoulders of Spring JDBC, SimpleDAO extends this white-box philosophy from "data access" to "business expression." We pursue a **business-oriented minimalist design**: while retaining Spring JDBC's transparency, we further eliminate boilerplate code, allowing developers to complete data operations with minimal code and maximum clarity. If Spring JDBC makes "accessing data" simple, SimpleDAO makes "expressing business with data" equally simple.

**The overall state of Java persistence frameworks is poor, except for Spring JDBC, which does relatively well** – because it holds the white-box line. SimpleDAO inherits this steadfastness and amplifies it through the SQL-First paradigm.

---

## Final Chapter: Portable Ideas – 200 Lines of Code Can Change the World

### 5.1 SQL-First is Not a Java Privilege

**Core abstractions are language-agnostic:**
```
1. Entity metadata collection (reflection/introspection at startup)
2. BaseDao<T> (inheritance for single-table CRUD)
3. Condition builder (unified condition assembly)
4. Native SQL execution interface (complex queries)
```

**Every language has its "Spring JDBC equivalent":**

| Language | JDBC Equivalent | Implementation Difficulty |
|----------|-----------------|---------------------------|
| Python | sqlite3 / psycopg2 / PyMySQL | ★☆☆ |
| Node.js | mysql2 / pg | ★☆☆ |
| Go | database/sql | ★★☆ |
| PHP | PDO | ★☆☆ |
| .NET | Dapper | ★☆☆ |
| Rust | sqlx | ★★☆ |

**200-300 lines of core code are enough to build a SQL-First data access layer in that language.**

### 5.2 Ideas Don't Need Organizations to Spread

**Linux wasn't finished by Linus. He wrote a seed, and the world completed it.**

The seed of SimpleDAO's ideas is already in this manifesto:
- Four laws
- Four core abstractions
- The promise of 200 lines of code per language

**The next Python developer, tired of SQLAlchemy's complexity, will find their way here.  
The next Node developer, struggling in TypeORM's issue tracker, will find their way here.  
The next Go programmer, wondering why GORM makes simple queries so heavy, will find their way here.**

They will write their own `pydao`, `node-sql-first`, `gosqlc`.

**They don't need to know you. They only need to read this manifesto.**

---

## Appendix: Clarifying Common Misunderstandings

**Q: Is SQL-First against ORM?**  
A: **Yes and no.**  
Yes – it opposes the part of ORM that tries to "replace SQL."  
No – single-table objectification is exactly what ORM does right.  
**SQL-First inherits the correct parts of ORM and corrects the wrong ones.**

---

**Q: Is SQL-First suitable for all projects?**  
A: **Not for projects that need to completely abstract away the database.**  
If your project needs to switch seamlessly between MySQL, Oracle, and PostgreSQL without changing a line of code – JPA might be a better choice.  
If your project needs to hide SQL so that frontend developers are completely unaware of the database – ORM has its historical value.  

**But if you need to control performance, maintain complex business logic, and have developers truly understand what they are operating on –**  
**SQL-First is the most honest choice available today.**

---

**Q: Won't writing SQL manually lead to SQL injection?**  
A: **Not if you use prepared statements with placeholders (? or :name).**  
All ORMs ultimately use prepared statements. SQL-First mandates them, preventing concatenation at the API level.

---

**Q: Isn't this just reinventing the wheel?**  
A: **If you consider "putting SQL back in its rightful place" reinventing the wheel, then yes.**  
But we've also reinvented "the wheel must be round" for five thousand years.

**Some wheels are worth re-confirming that they are indeed round.**

---

**End of Manifesto.**

> **Next Reads**  
> - For a comprehensive comparison between SQL-First and ORM, see **[02 Full-Scenario Comparison Matrix](02_Full-Scenario%20Comparison%20Matrix.md)**  
> - For the concrete implementation standard of the SQL-First paradigm, see **[03 SQL-First Persistence Development Paradigm Standard](03_SQL-First%20Persistence%20Development%20Paradigm%20Standard.md)**  
> - For Java implementation details and real-world cases, see **[04 SimpleDAO Technical Whitepaper](04_SimpleDAO%20Technical%20Whitepaper.md)**  
> - To implement SQL-First in your language, see **[05 SQL-First Paradigm Porting Guide](05_SQL-First%20Paradigm%20Porting%20Guide.md)**