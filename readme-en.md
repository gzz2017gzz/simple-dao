# SimpleDAO - Revolutionizing Enterprise-Grade Data Access
> **SQL-First · Concise & Efficient · Non-ORM Framework**
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/java-21%2B-orange)](https://www.oracle.com/java/)
[![Production Ready](https://img.shields.io/badge/production-ready-green)](https://github.com/simpledao/simpledao)
[![Performance](https://img.shields.io/badge/performance-+60%25-brightgreen)]()

## 🚀 Quick Start
> 👉 Hands-On Now: [SimpleDAO Quick Start (Run the Complete Demo)](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme-en.md)

## 📋 Content Navigation
- [Core Philosophy](#✨-core-philosophy:-simplicity-is-the-ultimate-sophistication)
- [Pain Point Solutions](#🎯-what-makes-us-different:-why-abandon-traditional-orm)
- [Key Features](#🚀-five-dimensional-enterprise-grade-pain-point-solutions)
- [Performance Data](#📊-performance-comparison:-numbers-speak)
- [Quick Start](#🚀-quick-start)
- [Core Value Summary](#🏆-core-value-summary)
- [FAQs](#🤔-frequently-asked-questions)
- [In-Depth Reading](#📚-in-depth-reading)

## 🎯 What Makes Us Different: Why Abandon Traditional ORM?
**If you're fed up with these:**
- ❌ **MyBatis**: XML configuration hell, redundant Mapper interfaces, difficult OGNL expression debugging
- ❌ **JPA/Hibernate**: Black-box SQL, N+1 query problem, helplessness with complex joins  
- ❌ **MyBatis Plus**: Fragmented single-table/multi-table APIs, nightmare of dynamic SQL maintenance
- ❌ All ORMs suffer from **mindset switching** and **performance loss** when moving from single-table to multi-table operations

**Then SimpleDAO is the answer you've been looking for.**

## ✨ Core Philosophy: Simplicity Is the Ultimate Sophistication
```java
// Single-table operations: Inherit BaseDao, zero code required
@Repository
public class UserDao extends BaseDao<User> {
    // Empty class gains full CRUD capabilities
    // 20+ methods including save()/update()/delete()/page()/list()
}

// Multi-table query: Native SQL, direct and efficient
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

## 🚀 Five-Dimensional Enterprise-Grade Pain Point Solutions
### 1. Development Efficiency: Cut Redundancy, Maximize Productivity (9 Points)
#### 🔥 1. **End the XML Configuration Hell**
```java
@Table("sys_user")
public class User {
    @Id("snow")  // Snowflake primary key
    private Long id;
    private String userName;  // Automatically mapped to user_name
    // Need to add a field? Just modify Java code, SQL adapts automatically
}
```
**Vs MyBatis**: No need for XML, ResultMap, or association/collection tags

#### 🔥 2. **Unified Single-Table/Multi-Table API**
```java
// Single-table
userDao.page(userCond);
// Multi-table (same API)
userDao.page(joinSql, userCond, UserVO.class);
```
**Vs MyBatis Plus**: Say goodbye to fragmented thinking between BaseMapper and XML

#### 🔥 3. **Extreme Simplification of Condition (SQL Fragment) Concatenation, Eliminate 98%+ `<if>` Tags**
```java
private Byte[] refundBillTypeIn;
	......

@Override
protected void addCondition() {
	and("signup_code LIKE", signupCode, 3); // Main table fuzzy match
	and("consume_count >", consumeCount); // Main table comparison (greater than/less than)
	and("finish =", finish);// Main table exact match
	
	add("AND r.refund_type IN ", refundBillTypeIn); // Collection match for associated/main table
	add("AND s.student_name LIKE ?", studentName, 3); // Associated table fuzzy match
	
    add("AND (t.war_start_date = DATE(NOW()) OR t.start_date = DATE(NOW()))", YesNo.yes(waitInSchool)); // SQL fragment with boolean expression
	add("AND t.grade_id IN (SELECT distinct grade_id FROM exam_signup WHERE dr=0)"); // SQL fragment

}
```
**Vs MyBatis**: No more nested `<if test="">` or OGNL expression errors

#### 🔥 4. **Standardized Pagination (One Line of Code)**
```java
Page<User> page = userDao.page(cond);
// Automatically includes: dataList, rowCount, pageInfo
// Intelligent COUNT SQL parsing to avoid subquery performance issues
```

#### 🔥 5. **Flexible Update Strategies**
```java
// Update non-null fields (90% of scenarios)
userDao.update(user);
// Full-field update (including null values)
userDao.updateNull(user);
// Conditional update (precise control)
userDao.update(user, condition);
```

#### 🔥 7. **High-Performance Batch Processing**
```java
// Batch insert (one network round-trip)
userDao.saveBatch(userList);
// Batch replace (MySQL Upsert)
userDao.replaceBatch(userList);
// Underlying implementation uses NamedParameterJdbcTemplate.batchUpdate
```

#### 🔥 8. **Automatic Audit Fields**
```java
userDao.save(user);  // Automatically sets: id, createTime, createBy, dr=0
userDao.update(user); // Automatically sets: updateTime, updateBy
// Zero configuration, hard-coded matching for common field names
```

#### 🔥 9. **Unified SQL Style**
```java
// Sql.wash() automatically handles:
// 1. Removes extra spaces/line breaks
// 2. Standardizes comma positions  
// 3. Unifies keyword case
// Clean log output with parameter-filled SQL for straightforward debugging
```

### 2. Data Security: Build Defenses from the Source (6 Points)
#### 🛡️ 10. **Eradicate SQL Injection**
**Full-link parameterized queries** + **BeanPropertyRowMapper type mapping**
A type-safe channel from Java to the database, completely eliminating injection risks.

#### 🛡️ 11. **Standardized Soft Delete**
```java
userDao.delete(1, 2, 3);  // Automatic judgment:
// If dr field exists → UPDATE SET dr=1
// If dr field does not exist → DELETE FROM
// No more handwritten SET dr=1 logic for each table
```

#### 🛡️ 12. **Convenient Row Lock Control**
```java
// No lock (default)
User user = userDao.findById(1);
// Add row lock (FOR UPDATE)  
User user = userDao.findById(1, true);
// Only enabled when needed, no impact on other query performance
```

#### 🛡️ 13. **Application-Level Foreign Key Constraints**
```java
// No physical foreign keys in microservices? We've got you covered!
userDao.checkRef = true;  // Enable reference check
// Automatically queries sys_table_ref configuration on deletion
// If reference records exist → BusinessException("Associated data exists")
// If no reference records exist → Normal deletion
```

#### 🛡️ 14. **Prevent Full-Table Misoperations**
```java
// BaseJdbc.update() enforces checks before execution:
// 1. Must contain WHERE keyword
// 2. Cannot have empty WHERE (e.g., WHERE 1=1)
// Violations throw exceptions directly to protect production data
```

### 3. Production Adaptation: Elegant Solutions for Complex Scenarios (3 Points)
#### 🏭 16. **Compatibility with "Odd" Databases**
**Pain Point**: Alibaba OceanBase does not support DELETE statements with table aliases
**Solution**: `BaseJdbc.update()` automatically identifies and removes `t.` aliases, with zero business awareness required for adaptation.

#### 🏭 17. **Integrated Distributed Primary Keys**
```java
@Id("snow")
private Long id;  // Automatically generates distributed unique ID
// Supports reverse parsing
Long timestamp = SnowflakeId.reverseId(id);
// Troubleshooting: Track generation time by ID
```

#### 🏭 18. **Enforced Architecture Layer Constraints**
```java
public abstract class BaseDao<T> {
    // Core methods are protected
    protected Page<T> page(Condition cond) { ... }
    // Enforces Service layer as the middle tier at the syntax level
    // Prevents anti-pattern of Controller directly calling DAO
}
```

### 4. Native Integration with Spring Ecosystem (5 Points)
#### 🌱 19. **Zero Integration Cost**
```java
// Built on Spring JdbcTemplate
// No need for adaptation layers like mybatis-spring
@Autowired
private JdbcTemplate jdbcTemplate; // Native Spring component
```

#### 🌱 20. **Seamless Transaction Integration**
```java
@Transactional // Direct use of Spring annotations
public void businessMethod() {
    userDao.save(user);
    orderDao.save(order);
    // 100% compatible with Spring transaction management
}
```

#### 🌱 21. **Native Support for Multiple Data Sources**
```java
// Direct use of Spring multiple data sources
@Primary
@Bean
public DataSource masterDataSource() {
    return DataSourceBuilder.create()...;
}
@Bean
public DataSource slaveDataSource() {
    return DataSourceBuilder.create()...;
}
// SimpleDAO adapts automatically, no extra configuration needed
```

#### 🌱 22. **Seamless Integration with Spring Cache**
```java
@Service
public class UserService {
    @Cacheable("users")
    public User getUser(Long id) {
        return userDao.findById(id); // DAO is a standard Spring Bean
    }
    // Enjoy Spring Cache capabilities without modifying DAO layer
}
```

#### 🌱 23. **Non-Intrusive Extension with AOP**
```java
@Aspect
@Component
public class LogAspect {
    @Around("@annotation(com.simpledao.annotation.BusinessAuth)")
    public Object authCheck(ProceedingJoinPoint joinPoint) {
        // Enhance DAO methods non-intrusively with Spring AOP
        return joinPoint.proceed();
    }
}
```

### 5. Engineering Productivity: Full-Link Code Generation (2 Points)
#### ⚡ 24. **Full-Stack Code Generator**
**Generated Content** (Ready to Use):
- ✅ **Backend**: Entity + DAO + Service + Controller + Condition + VO
- ✅ **Frontend**: List page + Form page + Tree component + Popup (Vue + Element UI)
- ✅ **Documentation**: Swagger annotations + API documentation
- ✅ **Testing**: Unit test templates
**80% productivity improvement**, repetitive CRUD code is a thing of the past.

#### ⚡ 25. **White-Box Low-Code (Vs Black-Box Platforms)**
| Dimension | Traditional Low-Code Platforms | SimpleDAO Generator |
|-----------|--------------------------------|---------------------|
| **Controllability** | ❌ Black-box, generated code invisible | ✅ Fully open-source, customizable templates |
| **Extensibility** | ❌ Limited by platform | ✅ Unlimited expansion based on standard Java |
| **Debugging Experience** | ❌ Difficult | ✅ Standard Java debugging |
| **Team Standards** | ❌ Hard to unify | ✅ Templates customized to team standards |
| **Tech Stack Lock-In** | ❌ Strong lock-in | ✅ Supports any tech stack |

```java
// Generates standard Java code, no black-box magic
// You can:
// 1. Modify Freemarker templates to adapt to team standards
// 2. Add custom templates (data permissions/export templates)
// 3. Integrate with any tech stack (Vue/React/Redis/MQ)
// 4. Follow all standard processes: version control, Code Review, etc.
```

## 📊 Performance Comparison: Numbers Speak
| Operation Type | SimpleDAO | MyBatis | JPA | Advantage |
|----------------|-----------|---------|-----|-----------|
| Insert 1000 single-table records | 125 ms | 210 ms (+68%) | 350 ms (+180%) | 🚀 **68-180% faster** |
| Single-table pagination query | 45 ms | 80 ms (+78%) | 120 ms (+167%) | ⚡ **78-167% faster** |
| 5-table join pagination | 110 ms | 180 ms (+64%) | 250 ms (+127%) | 🔥 **64-127% faster** |
| Complex report query | 200 ms | 350 ms (+75%) | 500 ms (+150%) | 💎 **75-150% faster** |
*Test Environment: MySQL 8.0, 1 million data records, Spring Boot application, JDK 21
 

## 🏆 Core Value Summary
Through **25 targeted pain point solutions**, SimpleDAO achieves dimensionality reduction in five key areas:
### 1. **Efficiency Revolution**
- Code volume reduced by **60-80%**
- Development time shortened by **50%**
- Learning curve: **2 hours** vs **2 days** for traditional frameworks

### 2. **Security System**
- **Comprehensive protection** from SQL injection to accidental data deletion
- **Dual safeguards**: compile-time checks + runtime validation

### 3. **Production Readiness**
- Compatible with various "odd" databases
- Full coverage of distributed, large-data-volume, high-concurrency scenarios
- **Zero** additional adaptation code

### 4. **Ecosystem Integration**
- **100% native Spring ecosystem**
- Expansion ceiling = Spring's ceiling
- No need to learn third-party integration

### 5. **Engineering Empowerment**
- Full-link code generation, **80% productivity improvement**
- White-box design balancing efficiency and flexibility
- One-click solidification of team standards

## 📚 Design Philosophy
### 1. **Not an ORM, but a SQL Enhancement Tool**
> We don't aim to replace SQL, but to make writing SQL more enjoyable.  
> No wrapping, no distortion, no hiding of SQL.  
> We believe SQL is the best query language for relational databases, proven over 50 years.

### 2. **Simplicity Requires More Courage Than Complexity**
> Dare to omit "advanced features" (because they are rarely used)  
> Dare to let users write SQL directly (because they already know how)  
> Dare to only solve 90% of common scenarios (handle the remaining 10% natively)

### 3. **Developer Time Is the Most Precious Resource**
> For every new feature added: Ask, "How much developer time does this save?"  
> For every new layer of abstraction added: Ask, "Is this really necessary?"  
> Our goal: **Let developers leave work 1 hour earlier every day**.

## 🤔 Frequently Asked Questions
**Q: What's the difference between SimpleDAO and MyBatis Plus?**  
A: MP is a "semi-ORM" — object operations for single tables, fallback to XML for multi-tables. SimpleDAO is "SQL-First" — unified SQL mindset for both single and multi-tables, with completely consistent APIs.

**Q: Do I need to learn new syntax?**  
A: No. If you know SQL and Java, you know how to use SimpleDAO. No extra syntax like XML, OGNL, or JPQL.

**Q: Is it suitable for microservices architecture?**  
A: Especially suitable. Lightweight (only 3 core classes), no external dependencies, perfect integration with Spring Cloud ecosystem.

**Q: Is the migration cost high from MyBatis?**  
A: Extremely low. Keep your SQL unchanged, just convert Mappers to Daos and move SQL from XML to Java.

## 🤝 Join Us
SimpleDAO is changing how developers interact with databases. If you:
- ✅ Are tired of the torment of complex frameworks
- ✅ Believe simplicity is beauty
- ✅ Value development efficiency and quality of life
- ✅ Are willing to share and contribute

We welcome you to:
- ⭐ **Star** our project
- 📚 **Read** the [Contribution Guide](CONTRIBUTING.md)
- 🐛 **Report** issues or submit PRs
- 💬 **Join** our discussion groups

## 📄 License
SimpleDAO is open source under the Apache License 2.0.

Apache License
Version 2.0, January 2004
http://www.apache.org/licenses/

Under the terms of this license, you are entitled to freely use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and permit persons to whom the Software is furnished to do so, subject to the following condition: the copyright notices and license notices specified in the License must be included in all copies or substantial portions of the Software.

## 🎯 Final Words
SimpleDAO is not meant to be just another popular framework, but to prove one thing:
> **Technology can be simpler, development can be more enjoyable, and programmers can leave work early.**

If you're fed up with complex frameworks, give SimpleDAO a try.  
If you think "it's too simple, not advanced enough" — that's okay — **we weren't designed for you anyway.**

We exist for those who:
- Want to get work done efficiently
- Want to go home to their families early  
- Believe simplicity is more powerful than complexity

---
**SimpleDAO: SQL-First, Concise & Efficient.**  
**Leave time for life, not frameworks.**
---
*SimpleDAO has been running stably in production for 3+ years, supporting millions of daily requests and serving over a dozen enterprise clients.*

## 📚 In-Depth Reading
To learn about SimpleDAO's underlying design logic, comprehensive comparison with ORM frameworks, in-depth refutation of common doubts, and the design philosophy of "doing no harm is already a great good", check out:  
  
[📄 SimpleDAO Quick Start (Run the Complete Demo)](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme-en.md)

> - To understand the origin of SQL-First thinking, see **[01 The SQL-First Manifesto](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/en/01_The%20SQL-First%20Manifesto.md)**  
> - For a comprehensive comparison between SQL-First and ORM, see **[02 Full-Scenario Comparison Matrix](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/en/02_Full-Scenario%20Comparison%20Matrix.md)**  
> - For the concrete implementation standard of the SQL-First paradigm, see **[03 SQL-First Persistence Development Paradigm Standard](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/en/03_SQL-First%20Persistence%20Development%20Paradigm%20Standard.md)**  
> - For Java implementation details and real-world cases, see **[04 SimpleDAO Technical Whitepaper](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/en/04_SimpleDAO%20Technical%20Whitepaper.md)**  
> - To implement SQL-First in your language, see **[05 SQL-First Paradigm Porting Guide](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/en/05_SQL-First%20Paradigm%20Porting%20Guide.md)**

### Translation Notes
1. **Term Consistency**: Key terms are translated consistently (e.g., "软删除" → "Soft Delete", "雪花主键" → "Snowflake primary key", "分页" → "Pagination").
2. **Cultural Adaptation**: Idiomatic expressions adjusted for English readability (e.g., "大道至简" → "Simplicity Is the Ultimate Sophistication", "早下班" → "leave work early").
3. **Technical Accuracy**: Preserved all technical details (code snippets, API names, database terms) to maintain technical integrity.
4. **Tone Consistency**: Maintained the original passionate, developer-centric tone while adapting to English technical writing conventions.
5. **Unchanged Links/File Names**: Kept Chinese-named markdown files (e.g., SQL-First宣言.md) as they are actual file names in the project.