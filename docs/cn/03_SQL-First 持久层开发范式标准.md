# 03 SQL-First 持久层开发范式标准

SimpleDAO 提出并落地了一种全新的持久层开发范式：以 **SQL-First** 为核心思想，以原生数据库访问接口为基座，以**极低侵入、极薄封装、极高业务浓度**为特征，统一全语言的数据操作范式。  
它不构建生态、不重造轮子、不依赖上层容器，只提供一套**可复制、可迁移、可落地**的工程方法论。

---

## 一、核心原则（前置共识）

1. **SQL主权原则**：开发者对最终执行 SQL 拥有完全可见、可修改、可优化的主权，框架不隐藏、不扭曲、不碎片化 SQL。
2. **不添乱原则**：框架仅消除样板代码、传递原生错误，不制造任何框架专有错误、不发明私有语法、不引入无业务价值的复杂度。
3. **极简封装原则**：封装仅停留在“减少重复劳动”，不做过度抽象，**框架存在感 < 5%，业务代码浓度 ≥ 95%**。
4. **约定优于配置原则**：核心能力通过约定实现零配置，特殊场景仅需轻量注解兜底。
5. **扩展白盒原则**：任何扩展（数据权限、分页、脱敏等）都应在业务层用语言原生方式实现，而非深入框架底层扒拦截器。

---

## 二、必须实现（立）

1. **单表 CRUD 零代码化**  
   - 通过继承/组合 BaseDao，空类即可获得完整单表能力（save/saveBatch/update/updateNull/delete/findById/list/page/count/exists）。  
   - 仅需注解（@Table/@Id）指定表名/主键，其余自动适配。

2. **条件构建安全统一化**  
   - 提供标准化 Condition 构建器，支持 AND/OR/LIKE/IN/NULL判断/时间范围等，参数与 SQL 强制分离（预编译占位符）。  
   - 不依赖框架私有语法（如 OGNL/XML标签），用语言原生逻辑（if/else）拼接条件。  
   - 单表/联表共用同一套条件工具，无思维割裂。

3. **原生 SQL 执行自由化**  
   - 支持手写完整原生 SQL（JOIN/子查询/UNION/聚合统计），直接执行并自动映射到实体/VO。  
   - SQL 可直接复制到数据库客户端执行，支持 IDE 语法高亮/格式化/补全，调试时可直接查看带参数的完整 SQL。

4. **分页逻辑标准化**  
   - 提供统一 page 方法，自动处理“总数统计+分页查询”，支持智能 COUNT SQL 解析。  
   - 无需依赖第三方插件，分页参数统一收口，返回结果包含数据列表+分页元信息。

5. **审计与软删除自动化**  
   - 增删改时自动填充审计字段（create_time/create_by/update_time/update_by），自动识别软删除字段（dr）。  
   - 删除操作默认执行软删除（dr=1），无 dr 字段则执行物理删除。

6. **字段映射自动化**  
   - 自动实现“实体驼峰命名 ↔ 数据库下划线命名”映射，支持通过轻量注解（@Column）覆盖特殊字段映射。  
   - 无需手动配置结果映射，多表联查结果可自动映射到 VO。

7. **错误传递透明化**  
   - 不封装、不转换、不隐藏数据库原生错误，所有错误均为 SQL 语法错误、参数错误或业务错误。  
   - 报错信息直接指向问题根源，排错无需解析框架内部逻辑。

---

## 三、不必实现（破）

- 不实现二级缓存（推荐使用 Redis）  
- 不实现懒加载（推荐手写精准查询）  
- 不实现级联操作（推荐手写 JOIN）  
- 不实现复杂对象映射（推荐手写 SQL）  
- 不绑定分布式 ID 生成器（推荐使用独立发号服务）

---

## 四、允许扩展（补）

- **数据权限**：支持通过注解+AOP 实现无侵入权限控制，不修改原生 SQL，不依赖拦截器。  
- **多数据源**：兼容语言原生多数据源方案，无需框架额外适配。  
- **数据库兼容**：支持通过扩展适配特殊数据库语法差异，业务层无感。  
- **代码生成**：支持生成标准代码，生成代码为白盒可修改，兼容团队规范，不绑定低代码平台。

---

## 五、实现示例（语言无关伪代码）

```java
// 实体
@Table("user")
class User {
    @Id("snow")
    Long id;
    String name;
    Integer age;
}

// DAO
class UserDao extends BaseDao<User> {}

// 使用
UserDao dao = new UserDao();
dao.save(user);
User u = dao.findById(1L);
List<User> list = dao.list(cond);
Page<User> page = dao.page(cond);

// 条件构建
class UserCond extends Condition {
    String name;
    Integer ageMin;
    @Override
    protected void addCondition() {
        and("name LIKE ?", name, 3);
        and("age > ?", ageMin);
    }
}

// 联表查询
String sql = "SELECT u.*, d.name dept_name FROM user u LEFT JOIN dept d ON u.dept_id = d.id";
List<UserVO> list = dao.list(sql, cond, UserVO.class);
```

---

**范式标准完。**

> **相关文档**  
> - 想了解 SQL-First 的思想原点？请移步 **[01 SQL-First宣言](#)**  
> - 想看到 SQL-First 与 ORM 的全面对比？请移步 **[02 全场景对比矩阵](#)**  
> - 想看到 Java 版的实际落地案例？请移步 **[04 SimpleDAO技术白皮书](#)**  
> - 想在你的语言中实现 SQL-First？请移步 **[05 SQL-First范式移植指南](#)**

---
