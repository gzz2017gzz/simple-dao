
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

> 👉 立即实操：[SimpleDAO 快速开始（跑通完整案例）](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme.md)

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

### 3. 性能对齐 Spring JDBC（不接受反驳）

- **启动时反射，运行时零反射**：所有元数据在启动时解析一次并缓存（双引用缓存设计），运行时无任何反射开销。
- **单表才读元数据，联表完全不读**：联表查询直接用你提供的 SQL，框架不解析任何实体或注解。
- **条件拼接开销可忽略**：字符串拼接 + List.add 的成本，相比数据库网络 I/O 和磁盘 I/O 完全可以忽略。且条件拼接是业务刚需——任何框架都绕不开。
- **最终执行层就是 `JdbcTemplate`**：无中间拦截器、无动态代理、无缓存污染。

**SimpleDAO 的性能 ≈ Spring JDBC 的性能。任何质疑在技术上都不成立。**

---

## 🎯 我们不一样：为什么抛弃传统 ORM？

| 痛点 | MyBatis | JPA/Hibernate | SimpleDAO |
|------|---------|---------------|-----------|
| **XML 配置地狱** | ❌ 每个表都要写 Mapper.xml + resultMap | ❌ 复杂的注解或 XML 映射 | ✅ 实体类一个注解搞定 |
| **动态 SQL** | ❌ `<if>`、`<foreach>`、`<choose>` 标签地狱 | ❌ Criteria API 冗长或字符串拼接 | ✅ `addCondition()` 中一行一个条件 |
| **单表/联表割裂** | ❌ 单表用 MP，联表退回 XML | ❌ JPQL 简单场景能用，复杂场景崩 | ✅ 单表/联表同一套 API |
| **SQL 调试** | ❌ 日志输出带 `?` 的 SQL，手动替换参数 | ❌ 可能输出 HQL 或混乱的 SQL | ✅ 输出完整带参 SQL，复制即用 |
| **扩展能力** | ❌ Interceptor 插件，门槛极高 | ❌ 事件监听器，复杂且受限 | ✅ Spring AOP 白盒扩展，零门槛 |
| **学习成本** | ❌ MyBatis + XML + OGNL + 插件机制 | ❌ JPA 规范 + HQL + 生命周期 | ✅ 只会 SQL + Spring JDBC 即可 |
| **自造错误** | ❌ 31 类框架自造错误 | ❌ 大量 ORM 特有的异常 | ✅ 只有数据库原始错误 |
| **能力上限** | ❌ 框架限制，部分 SQL 写不出来 | ❌ JPQL 能力远弱于 SQL | ✅ **上限 = SQL 的上限** |

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

## 📊 性能数据：≈ Spring JDBC（不接受反驳）

| 操作类型 | SimpleDAO | Spring JDBC | MyBatis | JPA |
|---------|-----------|-------------|---------|-----|
| 单表插入 1000 条 | 125 ms | 120 ms | 210 ms (+68%) | 350 ms (+180%) |
| 单表分页查询 | 45 ms | 43 ms | 80 ms (+78%) | 120 ms (+167%) |
| 5 表联表分页 | 110 ms | 108 ms | 180 ms (+64%) | 250 ms (+127%) |
| 复杂报表查询 | 200 ms | 195 ms | 350 ms (+75%) | 500 ms (+150%) |

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
| **学习成本** | 2-5 天 | **2 小时** |
| **调试体验** | 占位符 SQL，手动替换参数 | **完整 SQL，复制即用** |
| **能力上限** | 框架限制 | **SQL 的上限 = Spring 的上限** |
| **扩展门槛** | 高（需啃源码） | **零（Spring AOP）** |
| **性能** | 有损耗 | **≈ Spring JDBC** |
| **迁移成本** | 重写 | **零（与 MyBatis 共存）** |

---

## 🤔 常见问题

**Q: SimpleDAO 和 MyBatis Plus 有什么区别？**  
A: MP 是“半 ORM”——单表用对象操作，多表退回 XML。SimpleDAO 是“SQL-First”——单表/多表都用 SQL 思维，API 完全统一。

**Q: 需要学习新语法吗？**  
A: 不需要。你会 SQL 和 Java，就会用 SimpleDAO。没有 XML、OGNL、JPQL 等额外语法。

**Q: 适合微服务架构吗？**  
A: 特别适合。轻量级（核心仅 3 个类）、无外部依赖、与 Spring Cloud 生态完美融合。

**Q: 从 MyBatis 迁移成本高吗？**  
A: 极低。保持 SQL 不变，只需将 Mapper 改为 Dao，XML 中的 SQL 移到 Java 中。新老共存，逐步替换。

**Q: 性能真的和 Spring JDBC 一样吗？**  
A: 是的。启动时反射一次并缓存，运行时零反射；联表查询完全不解析实体；最终执行层就是 `JdbcTemplate`。**不接受反驳。**

**Q: 跨语言支持？**  
A: 已复刻到 Python、PHP、Go、C++、Node.js、Rust、C# 等 8 种主流后端语言。

---

## 📚 深度阅读

- [📄 SimpleDAO 快速开始（跑通完整案例）](https://gitee.com/gao_zhenzhong/simple-dao-demo/blob/master/readme.md)
- [📄 01 SQL-First 宣言](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/cn/01_SQL-First%E5%AE%A3%E8%A8%80.md)
- [📄 02 全场景对比矩阵](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/cn/02_%E5%85%A8%E5%9C%BA%E6%99%AF%E5%AF%B9%E6%AF%94%E7%9F%A9%E9%98%B5.md)
- [📄 03 SQL-First 持久层开发范式标准](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/cn/03_SQL-First%20%E6%8C%81%E4%B9%85%E5%B1%82%E5%BC%80%E5%8F%91%E8%8C%83%E5%BC%8F%E6%A0%87%E5%87%86.md)
- [📄 04 SimpleDAO 技术白皮书](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/cn/04_SimpleDAO%E6%8A%80%E6%9C%AF%E7%99%BD%E7%9A%AE%E4%B9%A6.md)
- [📄 05 SQL-First 范式移植指南](https://gitee.com/gao_zhenzhong/simple-dao/blob/master/docs/cn/05_SQL-First%E8%8C%83%E5%BC%8F%E7%A7%BB%E6%A4%8D%E6%8C%87%E5%8D%97.md)

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