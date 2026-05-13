
# SimpleDAO - 企业级数据访问的革命

> **SQL-First · 白盒透明 · 非ORM框架**  
> **能力上限 = SQL 表达的上限 = Spring 生态的上限**

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/java-21%2B-orange)](https://www.oracle.com/java/)
[![Production Ready](https://img.shields.io/badge/production-ready-green)](https://github.com/simpledao/simpledao)
[![Performance](https://img.shields.io/badge/performance-%3D%20Spring%20JDBC-brightgreen)]()
[![Multi Language](https://img.shields.io/badge/languages-8%2B-blue)]()

---

## 🚀 快速开始

> 👉 立即实操：[SimpleDAO 完整案例](https://github.com/gzz2017gzz/simple-dao-demo/blob/master/readme.md)
> 👉 API速查：[SimpleDAO API](api.md)

---

## 📋 内容导航

- [核心理念](#-核心理念sql-first-白盒透明)
- [能力边界](#-能力边界没有天花板)
- [我们不一样](#-我们不一样为什么抛弃传统orm)
- [功能亮点](#-功能亮点生产级特性全覆盖)
- [性能数据](#-性能数据--spring-jdbc不接受反驳)
- [生态集成](#-生态集成100-spring原生)
- [与MyBatis共存](#-与mybatis无缝共存零成本迁移)
- [快速开始](#-快速开始)
- [核心价值](#-核心价值总结)
- [常见问题](#-常见问题)
- [深度阅读](#-深度阅读)

---

## ✨ 核心理念：SQL-First，白盒透明

**数据库只认 SQL。** 50 年来，SQL 是关系型数据库唯一且通用的查询语言。任何试图“封装”、“替代”、“生成” SQL 的框架，最终都会变成开发者的额外负担。

SimpleDAO 不制造新的 SQL 方言，不封装 SQL 关键字，不扭曲 SQL 语义，不制造新的错误体系。你手写的 SQL，就是最终在数据库中执行的 SQL——框架不做任何改写、拦截、黑盒处理。

```java
// 单表操作：继承 BaseDao，零代码
@Repository
public class UserDao extends BaseDao<User> {
    // 空类获得所有 CRUD 能力
}

// 联表查询：原生 SQL，直接高效
private static final String JOIN_SQL = """
    SELECT u.*, d.dept_name, r.role_name
    FROM user u
    LEFT JOIN dept d ON u.dept_id = d.id
    LEFT JOIN user_role ur ON u.id = ur.user_id
    LEFT JOIN role r ON ur.role_id = r.id
    """;

// 同样的 API，统一的体验
public Page<UserVO> pageJoin(UserCond cond) {
    return page(JOIN_SQL, cond, UserVO.class);
}
```

---

## 🚀 能力边界：没有天花板

### 1. SQL 能力无上限

SimpleDAO 对复杂场景的支持**没有上限**——它的上限就是 **SQL 表达的上限**。

- 单表 CRUD、多表联查、嵌套子查询、聚合报表、窗口函数、递归 CTE、存储过程调用……**只要数据库 SQL 能写，SimpleDAO 就能直接执行**。
- 不预设“框架支持哪些 SQL 特性”——因为框架根本不解析 SQL。你写什么，数据库就执行什么。
- 不存在“框架能力盲区”，不存在“部分场景兜底”的妥协设计。

### 2. Spring 生态扩展无上限

SimpleDAO 不是对 Spring JDBC 的第三方封装，而是 **Spring JDBC 的原生功能延伸与增强**。

- **事务**：直接用 `@Transactional`，100% 兼容 Spring 事务管理。
- **多数据源**：Spring 多数据源直接使用，无需任何适配。
- **连接池**：HikariCP、Druid 等任意连接池，按 Spring Boot 标准配置。
- **缓存**：Spring Cache 注解在 Service 层，与 DAO 无关。
- **AOP**：数据权限、多租户、日志等，用 Spring AOP 无侵入扩展。
- **监控**：Spring Boot Actuator 监控数据源健康、SQL 执行情况。

**结论：SimpleDAO 的能力上限 = Spring 生态的上限，没有任何人为设置的边界。**

### 3. 性能对齐 Spring JDBC

- **启动时反射，运行时零反射**：所有元数据在启动时解析一次并缓存（双引用缓存设计），运行时无任何反射开销。
- **单表才读元数据，联表完全不读**：联表查询直接用你提供的 SQL，框架不解析任何实体或注解。
- **条件拼接开销可忽略**：字符串拼接 + List.add 的成本，相比数据库网络 I/O 和磁盘 I/O 完全可以忽略。且条件拼接是业务刚需——任何框架都绕不开。
- **最终执行层就是 `JdbcTemplate`**：无中间拦截器、无动态代理、无缓存污染。

**SimpleDAO 的性能 ≈ Spring JDBC 的性能。任何质疑在技术上都不成立。**

---

## 🎯 我们不一样：为什么抛弃传统 ORM？

### 📌简单场景（CRUD、分页、条件）

| 维度 | JPA/Hibernate | MyBatis生态 | SimpleDAO |
|------|:---:|:---:|:---:|
| 单表CRUD | 需配Entity + Repository | 需写SQL或引入MP | **继承空类，零代码** |
| 配置量 | 中（注解+接口） | 高（XML+接口+实体） | **零（两个注解）** |
| 动态条件 | Criteria API，冗长 | XML标签，一行变三行 | **一行add** |
| 条件统一性 | 两套（JPQL+Criteria） | 两套（XML+注解SQL） | **一套通用** |
| 审计字段 | 手写或Listener | 手写或MP插件 | **自动填充** |
| 逻辑删除 | 需@SQLDelete | 需插件或手写 | **自动处理** |
| 分页 | Pageable，SQL不可控 | PageHelper或手写 | **page()一行** |


---

### 📌复杂场景（联表、报表、SQL能力）

| 维度 | JPA/Hibernate | MyBatis生态 | SimpleDAO |
|------|:---:|:---:|:---:|
| 代码量 | 中 | 高 | **1/4 ~ 1/3** |
| SQL能力 | 约1/3能力 | 全能力，标签繁琐 | **全能力，无限制** |
| 报表/子查询 | 几乎不可用 | 极其痛苦 | **原生支持，条件复用** |
| 复杂联表 | 不可维护，退回原生 | 标签地狱，SQL切碎 | **完整SQL直写** |
| 结果映射 | 自动但黑盒 | resultMap，写两遍 | **自动映射VO** |
| 扩展性 | 黑盒难扩展 | 扒拦截器，升级就崩 | **白盒，AOP通吃** |
| SQL优化主权 | 完全丧失 | 半遮半掩 | **完全掌控** |
| 数据权限 | Listener+黑盒 | 拦截器，20行摸不到边 | **AOP，10分钟** |
| 字段脱敏 | 绑实体 | 写TypeHandler | **AOP，绑行为** |
| SQL调试 | 翻代理日志 | 手动替换占位符 | **复制即执行** |


---

### 📌综合能力（执行、扩展、风险、生态）

| 维度 | JPA/Hibernate | MyBatis生态 | SimpleDAO |
|------|:---:|:---:|:---:|
| 学习成本 | 极高 | 高 | **极低（会SQL就会用）** |
| 性能 | 约95% | 95-97% | **99%，天花板** |
| 全数据库（含国产） | 等Dialect，遥遥无期 | 插件逐个适配 | **有驱动就能用，全支持** |
| Spring生态红利 | 可用 | 桥接适配 | **事务/多数据源/缓存…拿来就用** |
| 扩展机制 | Listener/Callback | Interceptor，高门槛 | **AOP，零门槛** |
| 错误来源 | 框架+业务 | **31类自造异常**+业务 | **仅数据库/业务** |
| 社区本质 | 规范讨论组 | **受害者联盟** | **没问题可问** |
| 知识迁移 | 局限JPA生态 | 局限MyBatis生态 | **通用SQL，终身受益** |
| AI友好 | 需学专有API | 需学标签和OGNL | **零私有语法，Token省70%** |

---

**📌SimpleDAO vs JPA：把SQL能力天花板拆了**

**📌SimpleDAO vs MyBatis：把XML枷锁卸了**

**📌SimpleDAO vs Spring JDBC：把手工拼条件苦力自动化了**

**📌补齐了三大主力的全部短板**


---
## ⚙️ 配置项

| 配置项 | 默认值 | 说明 |
| :--- | :--- | :--- |
| `simple-dao.show-sql` | `true` | 是否打印带参 SQL 日志 |
| `simple-dao.logic-delete.field` | `dr` | 逻辑删除字段名 |
| `simple-dao.dialect` | 自动检测 | 数据库方言：`mysql`、`postgresql`、`sqlserver`、`oracle` |
| `simple-dao.worker-id` | `0` | 雪花ID工作节点标识，集群环境为不同服务分配不同值 |
| `simple-dao.data-center-id` | `0` | 雪花ID数据中心标识 |

**方言三级降级策略**：显式配置 > 自动检测 > 兜底 MySQL。

无需配置即可自动适配 MySQL、H2、SQLite、PostgreSQL、SQL Server、Oracle 分页语法。

同时支持全部有JDBC Dirver的国产数据库。

---

## 🚀 功能亮点：生产级特性全覆盖

### 一、开发效率（砍掉冗余）

#### 1. 终结 XML 配置地狱
```java
@Table("sys_user")
public class User {
    @Id("snow")  // 雪花主键
    private Long id;
    private String userName;  // 自动映射为 user_name
}
```
**对比**：无需 XML、ResultMap、association/collection 标签。

#### 2. 统一单表/多表 API
```java
// 单表
userDao.page(userCond);
// 多表（同样的 API）
userDao.page(joinSql, userCond, UserVO.class);
```
**对比**：告别 MyBatis Plus 的 BaseMapper/XML 两套思维割裂。

#### 3. 极致简化的条件拼接
```java
@Override
protected void addCondition() {
    and("name LIKE", name, 3);           // 主表模糊
    and("age >=", ageMin);               // 主表区间
    in("id", ids);                       // IN 子句
    add("AND r.refund_type IN ", types); // 关联表条件
    add("AND (t.start_date = CURDATE() OR t.war_date = CURDATE())", flag); // SQL 片段
}
```
**对比**：告别 MyBatis 的 `<if>` 嵌套和 OGNL 表达式错误。

#### 4. 分页标准化（一行代码）
```java
Page<User> page = userDao.page(cond);
// 自动包含：dataList、rowCount、page 信息
// 智能 COUNT SQL 解析，避免子查询性能问题
```

#### 5. 灵活的更新策略
```java
userDao.update(user);           // 非空字段更新（90% 场景）
userDao.updateNull(user);       // 全字段更新（含 null）
userDao.update(user, condition);// 条件更新
```

#### 6. 高性能批处理
```java
userDao.saveBatch(userList);    // 批量插入
userDao.replaceBatch(userList); // 批量替换（MySQL Upsert）
```

#### 7. 自动审计字段
```java
userDao.save(user);   // 自动设置：id、createTime、createBy、dr=0
userDao.update(user); // 自动设置：updateTime、updateBy
```

### 二、数据安全（从源头构建防线）

#### 8. 根治 SQL 注入
全链路参数化查询 + `BeanPropertyRowMapper` 类型映射，从 Java 到数据库的类型安全通道。

#### 9. 标准化软删除
```java
userDao.delete(1, 2, 3);
// 有 dr 字段 → UPDATE SET dr=1
// 无 dr 字段 → DELETE FROM
```

#### 10. 便捷行锁控制
```java
User user = userDao.findById(1, true); // FOR UPDATE
```

#### 11. 应用层外键约束（微服务专用）
```java
userDao.checkRef = true;  // 开启引用检查
// 删除时自动查询 sys_table_ref 配置，有引用则抛异常
```

#### 12. 防止全表误操作
`update()` 执行前强制检查必须有 WHERE 条件，违规直接熔断。

### 三、生产适配（复杂场景优雅方案）

#### 13. 奇葩数据库兼容
自动识别并适配 OceanBase 等不支持 DELETE 别名的数据库。

#### 14. 分布式主键一体化
```java
@Id("snow")
private Long id;  // 雪花算法，支持反向解析时间戳
```

#### 15. 架构分层强制约束
`BaseDao` 核心方法为 `protected`，从语法层面强制 Service 层作为中间层。

### 四、工程提效（全链路代码生成）

#### 16. 全栈代码生成器
生成内容：Entity + DAO + Service + Controller + Condition + VO + 前端页面（Vue + Element）+ Swagger 注解 + 单元测试模板。**效率提升 80%**。

#### 17. 白盒化低代码
生成的代码是标准 Java，不是黑盒魔法。你可以修改 Freemarker 模板、添加自定义模板、对接任意技术栈、进行 Code Review。

---

## 📊 性能数据：≈ Spring JDBC

| 操作类型 | SimpleDAO | Spring JDBC | MyBatis | JPA |
|---------|-----------|-------------|---------|-----|
| 单表插入 1000 条 | 120 ms | 120 ms | 210 ms (+68%) | 350 ms (+180%) |
| 单表分页查询 | 43 ms | 43 ms | 80 ms (+78%) | 120 ms (+167%) |
| 5 表联表分页 | 109 ms | 108 ms | 180 ms (+64%) | 250 ms (+127%) |
| 复杂报表查询 | 196 ms | 195 ms | 350 ms (+75%) | 500 ms (+150%) |

*SimpleDAO 与 Spring JDBC 性能几乎一致，差异在测量误差范围内。*

---

## 🌱 生态集成：100% Spring 原生

```java
// 事务
@Transactional
public void businessMethod() {
    userDao.save(user);
    orderDao.save(order);
}

// 多数据源
@Bean
public DataSource masterDataSource() { ... }
@Bean
public DataSource slaveDataSource() { ... }

// Spring Cache
@Cacheable("users")
public User getUser(Long id) {
    return userDao.findById(id);
}

// AOP 扩展
@Aspect
public class DataAuthAspect {
    @Around("@annotation(BusinessAuth)")
    public Object authCheck(ProceedingJoinPoint pjp) { ... }
}
```

**结论：SimpleDAO 不是“需要适配 Spring”，而是“本来就是 Spring 的一部分”。**

---

## 🤝 与 MyBatis 无缝共存：零成本迁移

这是 SimpleDAO 独有的核心优势：

- 所有基于 MyBatis 开发的存量项目，**无需修改一行老代码**，仅需引入 SimpleDAO 依赖包，即可直接启用。
- MyBatis 底层基于 Spring JDBC 桥接，SimpleDAO 完全基于 Spring JDBC 原生构建，二者共用同一套数据源与 Spring 底层能力。
- **新老业务完全解耦、互不干扰**：
  - 存量业务继续用 MyBatis
  - 新增业务用 SimpleDAO
  - 随着业务迭代，逐步平滑替换
- 新项目直接基于 SimpleDAO 构建，彻底告别 MyBatis 的补坑生态。

---

## 🏆 核心价值总结

| 维度 | 传统 ORM | SimpleDAO |
|------|----------|-----------|
| **代码量** | 基准 | 减少 **60-80%** |
| **开发时间** | 基准 | 缩短 **50%** |
| **学习成本** | 2-5 天 | **20 分钟** |
| **调试体验** | 占位符 SQL，手动替换参数 | **完整 SQL，复制即用** |
| **能力上限** | 框架限制 | **SQL 的上限 = Spring 的上限** |
| **扩展门槛** | 高（需啃源码） | **零（Spring AOP）** |
| **性能** | 有损耗 | **≈ Spring JDBC** |
| **迁移成本** | 重写 | **零（与 MyBatis 共存）** |

---

## 🤔 常见问题
**Q: 要求 Java 21+，老项目用不了？**  
**A: 故意写的。核心只是字符串拼接，降级到 JDK 8 分分钟——让 AI 帮你改，几分钟的事。**  
（源码里那几处新语法，换个写法就行。你不会连 AI 都懒得用吧？）

**Q: 从 MyBatis 迁移到 SimpleDAO 成本高吗？**  
**A: 零迁移成本。SimpleDAO 可与 MyBatis 无缝共生，你不需要改动任何老代码。**  
- 引入 SimpleDAO 依赖后，原有 MyBatis 的 Mapper、XML、插件照常运行。  
- 新业务直接用 SimpleDAO 开发，老代码碰都不碰。  
- 没有“迁移”这个概念，只有“增量使用”。有精力就换，没精力永远共存。

**Q: SimpleDAO 和 MyBatis Plus 有什么区别？**  
A: MP 是“半 ORM”——单表用对象操作，多表退回 XML。SimpleDAO 是“SQL-First”——单表/多表都用 SQL 思维，API 完全统一。

**Q: 需要学习新语法吗？**  
A: 不需要。你会 SQL 和 Java，就会用 SimpleDAO。没有 XML、OGNL、JPQL 等额外语法。

**Q: 适合微服务架构吗？**  
A: 特别适合。轻量级（核心仅 3 个类）、无外部依赖、与 Spring Cloud 生态完美融合。

**Q: 性能真的和 Spring JDBC 一样吗？**  
A: 是的。启动时反射一次并缓存，运行时零反射；联表查询完全不解析实体；最终执行层就是 `JdbcTemplate`。**不接受反驳。**

**Q: 跨语言支持？**  
A: 已复刻到 Python、PHP、Go、C++、Node.js、Rust、C# 等 8 种主流后端语言。

---

## 📚 深度阅读

- [📄 SimpleDAO 快速开始（跑通完整案例）](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme.md)


---

## 🤝 加入我们

SimpleDAO 正在改变开发者与数据库交互的方式。如果你：
- ✅ 厌倦了复杂框架的折磨
- ✅ 相信简单就是美
- ✅ 重视开发效率和生活质量
- ✅ 愿意分享和贡献

欢迎：⭐ **Star** 项目 | 📚 **阅读** 贡献指南 | 🐛 **报告** Issue | 💬 **加入** 讨论群组

---

## 📄 许可证

Apache License 2.0

---

## 🎯 最后的话

SimpleDAO 不是为了成为又一个流行的框架，而是为了证明一件事：

> **技术可以更简单，开发可以更愉快，程序员可以早下班。**

如果你也受够了复杂框架的折磨，欢迎尝试 SimpleDAO。

我们为那些：
- 想要高效完成工作的人
- 想要早点回家陪家人的人
- 相信简单比复杂更有力量的人

而存在。

---

**SimpleDAO: SQL-First，白盒透明，能力无上限。**  
**把时间留给生活，而不是框架。**

---

*SimpleDAO 已在生产环境稳定运行 3 年+，支撑日均百万级请求，服务十余家企业客户。*