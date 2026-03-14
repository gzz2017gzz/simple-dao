
# 05 SQL-First Paradigm Porting Guide
## 200 Lines of Code to Liberate Your Language from ORM

Version: 1.0  
Release Date: February 12, 2026  
Target Audience: Python/Node/Go/PHP/.NET/Rust Developers  
Reading Time: 15 minutes  
Implementation Time: 2 hours (with AI) ~ 2 days (pure manual)

---

## Why You Can Trust This Guide

The SimpleDAO behind this guide has been running stably in production within the Java/Spring ecosystem for **over 3 years**, handling millions of daily requests across complex business scenarios.  
**A paradigm validated by 2000 lines of Java code can be reimplemented in your language with just 200 lines.**

---

### Core Design Goals
When implementing your own SQL-First data access layer, always keep in mind:
- **The higher the business code density, the better**; the less framework code, the better.
- **The lower the framework's presence, the better**; let developers barely notice the framework's existence.
- **Extensions must be white-box**: Any extension functionality should be implemented at the business layer using the language's native approach, not by diving into framework internals.
- **Create no errors, add no unnecessary complexity**: The framework should only eliminate boilerplate code and transparently pass through database errors.

---

## Part I: Core Abstractions (Language-Agnostic)

A SQL-First data access layer in any language consists of the following **4 core abstractions**.

### 1.1 Entity Metadata Collector
**Responsibility**: Reads annotations/tags from entity classes/structs to resolve table name, field names, primary key name, and primary key strategy. **Executed at startup, results cached, zero reflection at runtime.**

**Implementation要点 by Language**:
| Language | Metadata Mechanism | Caching Timing |
|----------|--------------------|----------------|
| Java | Annotations + Reflection | Static constructor |
| Python | Class attributes / decorators | Module load time |
| Node/TS | Decorators + Reflect | Module load time |
| Go | Struct tags + reflect | `init()` |
| PHP | Attributes / Annotations | Constructor |
| .NET | Attributes + Reflection | Static constructor |
| Rust | Derive macros + procedural macros | Compile time |

### 1.2 BaseDao<T>
**Responsibility**: Provides out-of-the-box single-table CRUD methods for entity T via inheritance/composition.

**Minimum Method Set**: `save` / `update` / `updateNull` / `delete` / `findById` / `saveBatch` / `updateBatch` / `deleteBatch` / `list` / `page` / `count` / `exists`

**Implementation要点**: All methods are based on **native SQL strings + parameter arrays**; do not encapsulate complex QueryBuilders.

### 1.3 Condition Builder
**Responsibility**: Assembles SQL WHERE clauses + collects parameters. This is the soul of the SQL-First paradigm.

**Core API**:
- `add(String sql)`
- `add(String sql, boolean condition)`
- `add(String sql, Object value)`
- `add(String sql, String value, int site)`  // site: 1 left `%`, 2 right `%`, 3 both `%`
- `add(String sql, Object[] values)`

**Outputs**: `where()` returns the WHERE clause string; `array()` returns the parameter array.

### 1.4 Native SQL Executor
**Responsibility**: Executes complete SQL written by the developer and maps results.

**Minimum Method Set**: `list` / `page` / `row` / `field` / `update` / `batch`

**Implementation要点**: Directly use the language's native prepared statement execution API; result mapping follows convention over configuration (automatic camelCase/underscore conversion); pagination does `count` first, then `limit`.

---

## Part II: Language-Specific Implementation要点 (Cheat Sheet)

### 🐍 Python (Difficulty: ★☆☆)
- **JDBC Equivalent**: `sqlite3`, `psycopg2`, `PyMySQL`
- **Advantages**: Dynamic language, mature decorators.
- **Recommended Project Names**: `pydao`, `simple-db`, `sqlfirst`

### 📦 Node.js / TypeScript (Difficulty: ★☆☆)
- **JDBC Equivalent**: `mysql2`, `pg`
- **Advantages**: TypeScript decorators + Reflect metadata.
- **Recommended Project Names**: `sqlex`, `type-dao`, `node-sql-first`

### 🐹 Go (Difficulty: ★★☆)
- **JDBC Equivalent**: `database/sql`
- **Challenges**: No inheritance, reflection syntax slightly verbose, generics are weaker (though improved in 1.18+).
- **Recommended Project Names**: `gosqlc`, `dao-go`, `lightdb`

### 🐘 PHP (Difficulty: ★☆☆)
- **JDBC Equivalent**: `PDO`
- **Advantages**: Rich dynamic features, well-supported attributes/annotations in PHP 8+.
- **Recommended Project Names**: `pdo-dao`, `simple-db`, `sql-first-php`

### 🎯 .NET / C# (Difficulty: ★☆☆)
- **JDBC Equivalent**: `Dapper` (itself SQL-First in style)
- **Advantages**: Dapper already provides extension methods; the missing pieces are the `BaseDao` and `Condition` layer.
- **Recommended Project Names**: `Dapper.Simple`, `DapperDao`

### 🦀 Rust (Difficulty: ★★☆)
- **JDBC Equivalent**: `sqlx`
- **Advantages**: Community leans towards SQL-First; procedural macros enable elegant mapping.
- **Recommended Project Names**: `sqlx-dao`, `sea-simple`

---

## Part III: From "Seed of an Idea" to "Working Code"

### 3.1 Minimum Viable Standard (Must-Haves)
1.  **Single-Table CRUD**: Inherit/compose `BaseDao`; empty class works.
2.  **Condition Builder**: Supports `and`/`or`/`LIKE`/`IN`; parameters strictly separated from SQL.
3.  **Native SQL Execution**: Hand-write `JOIN` queries; results automatically map to VOs.
4.  **Pagination**: Unified `page` method; automatic `count`.
5.  **Audit Fields**: `create_time` / `update_time` / `dr` automatically populated.
6.  **No Self-Inflicted Errors**: Do not encapsulate framework-specific exceptions.
7.  **White-Box Extensions**: Any extension functionality must be implementable at the business layer without relying on framework internals.

### 3.2 4-Hour Roadmap from Zero to One
- **Hour 1**: Implement `EntityMeta` collector + `Condition` builder.
- **Hour 2**: Implement `BaseDao.save` / `findById` / `update` / `delete`.
- **Hour 3**: Implement native SQL executor (`list` / `page` / `row`).
- **Hour 4**: Run a real business case (e.g., user + department join query).

### 3.3 AI Prompt Template (Example for Python)
```
Based on the following Java core code from SimpleDAO, help me implement a minimal SQL-First data access layer in Python.
Requirements:
- Use sqlite3 as the underlying driver.
- Implement an EntityMeta collector, BaseDao, and Condition builder.
- Support single-table CRUD and conditional queries.
- Follow the principles of "high business code density, low framework presence."
- Output the code and a simple usage example.

[Java source code would go here]
```

---

## Part IV: Propagation – Make Your Implementation Seen

### 4.1 Naming Suggestions
**Formula**: `[Language Flavor] + [Dao/DB/SQL] + [Lightweight Suffix]`  
Examples: `pydao`, `node-sql-first`, `gosqlc`, `dapper.simple`, `sqlx-dao`

**Pay homage in the first line of your README**:
> Inspired by [SimpleDAO](https://gitee.com/...) - A SQL-First data access paradigm from the Java ecosystem.

### 4.2 Three Essential Documents to Include
1.  **README.md**: A 5-minute quick-start example (e.g., user + department join).
2.  **MANIFESTO.md**: A core summary of the SQL-First Manifesto.
3.  **BENCHMARK.md**: A comparison with mainstream ORMs in terms of lines of code and cognitive load.

---

## Final Chapter: The Significance of These 200 Lines

**The 200 lines of code you are about to write are not meant to replace your language's ORM. They are meant to prove that, in your language, an ORM is not a necessity.**

When the first Python developer sees `pydao`'s README and discovers that 200 lines of code can free them from SQLAlchemy's complex inheritance tree—**they will realize: for the past three years, they weren't learning; they were being consumed.**

**You will become the one who hands them the key.**

---

**End of Porting Guide.**

> **Related Documents**  
> - To understand the origin of SQL-First thinking, see **[01 The SQL-First Manifesto](01_The%20SQL-First%20Manifesto.md)**  
> - For a comprehensive comparison between SQL-First and ORM, see **[02 Full-Scenario Comparison Matrix](02_Full-Scenario%20Comparison%20Matrix.md)**  
> - For the concrete implementation standard of the SQL-First paradigm, see **[03 SQL-First Persistence Development Paradigm Standard](03_SQL-First%20Persistence%20Development%20Paradigm%20Standard.md)**  
> - For Java implementation details and real-world cases, see **[04 SimpleDAO Technical Whitepaper](04_SimpleDAO%20Technical%20Whitepaper.md)**  
> - To implement SQL-First in your language, see **[05 SQL-First Paradigm Porting Guide](05_SQL-First%20Paradigm%20Porting%20Guide.md)**

---

*This document is licensed under CC BY 4.0. You are free to copy, distribute, and modify it, provided you credit the source.*

*The best tribute is to write your own SimpleDAO in your language. Then tell the next person: you can too.*