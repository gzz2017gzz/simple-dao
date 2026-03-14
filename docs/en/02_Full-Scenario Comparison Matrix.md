# 02 Full-Scenario Comparison Matrix
## ORM vs SQL-First: One Chart to End the Debate

Version: 1.0  
Release Date: February 12, 2026  
Comparison Targets: JPA/Hibernate, MyBatis, SQL-First (SimpleDAO Paradigm)  
Guiding Principle: Verifiable, Reproducible, No Double Standards

---

## I. Master Table: 21-Dimension Panoramic Comparison

| Dimension | JPA/Hibernate | MyBatis | **SQL-First Paradigm** | Conclusion |
|-----------|---------------|---------|------------------------|------------|
| **1. Single-Table CRUD Code Volume** | Medium | High | **Very Low (inherit BaseDao, empty class)** | SQL-First ↓80% |
| **2. Single-Table CRUD Configuration Volume** | Medium | High | **Zero (@Table/@Id annotations only)** | SQL-First ↓100% |
| **3. Lightweight Joins (2-3 tables)** | Possible (JPQL) but N+1 risk | Cumbersome | **Extremely Simple (native SQL + auto-mapping)** | SQL-First ★★★★★ |
| **4. Complex Joins (5+ tables)** | ❌ Unmaintainable | ⚠️ Barely Possible | **✅ Clear (complete SQL written directly)** | SQL-First Only Solution |
| **5. Report-Level SQL (UNION/subqueries)** | ❌ Completely Unsupported | ⚠️ Extremely Painful | **✅ Native Support + Parameter Merging** | SQL-First Overwhelming |
| **6. Dynamic SQL** | Criteria API | OGNL + XML Tags | **Native if/else in Java/Python/Go** | SQL-First Zero Learning Cost |
| **7. Condition Construction Uniformity** | ❌ Two sets | ❌ Two sets | **✅ Fully Unified (BaseCondition)** | SQL-First Exclusive |
| **8. Object Mapping** | Automatic but Black-Box | Manual resultMap | **Automatic (camelCase/underscore)** | SQL-First Sufficient & Transparent |
| **9. Data Permissions** | Requires Interceptor | Custom StatementHandler | **Annotation + AOP, One Line** | SQL-First Elegant |
| **10. Batch Operations** | Complex batch API | Requires Third-Party Plugin | **Native batchUpdate Reuse** | SQL-First Zero Dependency |
| **11. Pagination** | PageRequest | PageHelper (Plugin) | **Built-in page(cond) Method** | SQL-First Built-in |
| **12. Transactions** | @Transactional | Requires Extra Configuration | **@Transactional Native Support** | SQL-First Zero Adaptation |
| **13. Multiple Data Sources** | Requires Extra Configuration | Requires Extra Configuration | **Spring Native Support** | SQL-First Zero Cost |
| **14. Distributed Sharding** | ShardingSphere Adaptation | ShardingSphere + XML | **Manually Write Sharding SQL, Clear & Controllable** | SQL-First Sovereignty in My Hands |
| **15. SQL Debugging** | Black-Box, Requires Logs | Requires Parsing BoundSql | **See Full SQL Directly at Breakpoint** | SQL-First Minute-Level Troubleshooting |
| **16. Extensibility White-Box Nature** | ❌ Black-Box, Hard to Extend | ⚠️ Black-Box Execution, Extensions Require Hacking Interceptors | **✅ White-Box Execution, Business Extensions with No Barriers** | SQL-First Zero Dependency Ecosystem |
| **17. Error Sources** | Framework Errors + Business Errors | **31 Categories of Framework-Manufactured Errors** + Business Errors | **Only Database/Business Errors** | SQL-First No Added Nuisance |
| **18. SQL Optimization Sovereignty** | ❌ Completely Lost | ⚠️ Half-Obscured | **✅ Fully Controlled** | SQL-First Essential Advantage |
| **19. Learning Cost** | High | Medium-High | **Extremely Low (know SQL, you're good)** | SQL-First ↓90% |
| **20. Knowledge Transferability** | ❌ JPA Ecosystem Only | ❌ MyBatis Ecosystem Only | **✅ Universal SQL Skills** | SQL-First Lifelong Benefit |
| **21. Community Nature** | Specification Discussion Group | **Victim Alliance** | **No Community (No Problems to Ask)** | SQL-First Healthy State |

---

## II. Deep Dive into Key Dimensions

### 2.1 Single-Table CRUD: From "Writing Code" to "Declaring Requirements"
**JPA**: Inherits a black box; don't know how SQL is generated.  
**MyBatis**: One query, two files, three steps.  
**SQL-First**: Inherit `BaseDao`; empty class gets all single-table methods.

### 2.2 Complex Joins: The Touchstone for Frameworks
**Test Case**: 8-table join + 20 dynamic conditions  
**MyBatis**: ~180 lines of XML, SQL split into 4 fragments, debugging requires reconstructing full SQL.  
**SQL-First**: 70 lines of complete SQL, copy directly to Navicat to execute, optimization cycle 1 minute.

### 2.3 Dynamic Conditions: Mother Tongue vs Dialect
**Requirement**: if name != null, add name like query; if ageMin != null, add age > ageMin  
**JPA Criteria**: Semantics inverted, unreadable.  
**MyBatis**: OGNL syntax, tag closure, string concatenation.  
**SQL-First**: `and("name LIKE ?", name, 3); and("age > ?", ageMin);`

### 2.4 Extensibility White-Box Nature: Why MyBatis Ecosystem Is Bloated
MyBatis's core execution is a black box, causing extensions like pagination, data masking, and data permissions to be impossible to implement directly with business code. They must be implemented by brutally modifying the SQL execution chain via interceptors. Writing a data permission requires 20 lines of code that barely touch business logic – all framework internals – and breaks with every version upgrade.  
**Spring JDBC, in contrast, is white-box**. SQL execution is transparent; developers can easily implement extensions at the service layer using AOP. SimpleDAO inherits this advantage, returning extensions to the business domain.

### 2.5 SQL Optimization Sovereignty: Who Controls SQL, Controls Performance
- **JPA**: Takes away your SQL
- **MyBatis**: Borrows your SQL
- **SQL-First**: Gives SQL back to you

---

## III. Community Nature: Victim Alliance vs No Community

Issue distribution in the MyBatis community:
- XML configuration errors: 25%
- ResultMap mapping failures: 20%
- OGNL expression errors: 15%
- Plugin conflicts: 10%
- Version compatibility issues: 10%
- **Real SQL/business problems**: 20%

**80% of community discussions are about solving problems created by the framework itself.**  
This is the "Victim Alliance": framework creates problems → developers step into pitfalls → community provides solutions → solutions create new problems → more pitfalls.

Issues in SQL-First:
- This SQL query is slow → database/SQL problem
- Transaction not rolling back → Spring transaction problem
- Parameter passed incorrectly → business code problem

**Not a single problem is a "framework problem."** Tools don't need communities. If you hit your hand with a hammer, you don't post in a hammer community.

---

## IV. Clarifying Misunderstandings
- **Misunderstanding 1: SQL-First = Going back to the JDBC stone age**  
  Clarification: Have you ever seen a "stone age" that automatically generates INSERT statements, auto-fills primary keys, and auto-paginates?
- **Misunderstanding 2: Writing SQL manually leads to SQL injection**  
  Clarification: Injection comes from **string concatenation**, not from writing SQL manually. SQL-First **mandates prepared statements**.
- **Misunderstanding 3: Complex mapping requires resultMap**  
  Clarification: 80% of complex mapping arises from trying to make ORM do things the database shouldn't do. For the remaining 20%, hand-written code is clearer than configuring resultMap.

---

## V. Summary: A Picture Is Worth a Thousand Words

```
                      SQL Optimization Sovereignty
                           ▲
                           │
                  ● SQL-First (Fully Controlled)
                           │
                           │
        MyBatis (Half-Obscured) ●──┬──● JPA (Completely Lost)
                           │
                           │
                           │
                 Framework-Manufactured Errors
```

**Quadrant I: Sovereignty with Me, Errors Real → SQL-First**  
**Quadrant II: Sovereignty Lost, Errors Mixed → JPA**  
**Quadrant III: Sovereignty Half-Lost, Errors Manufactured → MyBatis**

---

## Appendix: How to Use This Matrix to Convince Your Team
- **Technology Selection Review**: Use rows 4, 6, and 17 to counter arguments like "JPA is fast for development" or "MyBatis is flexible."
- **Legacy System Refactoring**: Show row 4 (complex joins) and propose using SQL-First for new features.
- **Onboarding New Developers**: Show rows 18 and 19 (learning cost and knowledge transferability) and suggest they learn SQL first, then pick up the tool.

---

**End of Comparison Matrix.**

> **Related Documents**  
> - To understand the origin of SQL-First thinking, see **[01 The SQL-First Manifesto](01_The%20SQL-First%20Manifesto.md)**  
> - For a comprehensive comparison between SQL-First and ORM, see **[02 Full-Scenario Comparison Matrix](02_Full-Scenario%20Comparison%20Matrix.md)**  
> - For the concrete implementation standard of the SQL-First paradigm, see **[03 SQL-First Persistence Development Paradigm Standard](03_SQL-First%20Persistence%20Development%20Paradigm%20Standard.md)**  
> - For Java implementation details and real-world cases, see **[04 SimpleDAO Technical Whitepaper](04_SimpleDAO%20Technical%20Whitepaper.md)**  
> - To implement SQL-First in your language, see **[05 SQL-First Paradigm Porting Guide](05_SQL-First%20Paradigm%20Porting%20Guide.md)**