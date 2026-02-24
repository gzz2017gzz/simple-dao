# SQL-First宣言
**——数据库访问层的Unix哲学**

版本：1.0  
发布日期：2026年2月12日  
作者：SimpleDAO 思想共同体  
状态：永恒进行时

---

## 序：框架的终点是消失

**技术的最佳状态，是让人感觉不到它的存在。**

好的门把手不会告诉你的手该怎么握，它只是在那里，你一推，门就开了。

好的数据库访问层也应该是这样：你心里想的是“我要查用户和他的部门”，手指敲出来的是 SQL，回车，数据回来。

没有 XML 要配，没有 Mapper 要写，没有 `@OneToMany` 要琢磨，没有 N+1 问题要救火。

**这就是 SQL-First 范式。**

它不是一个新的框架。它是一个持续了50年、却被遗忘了20年的答案。

---

## 第一章：三十年弯路——ORM 如何从“解决方案”变成“问题本身”

### 1.1 那个被遗忘的共识

1974年，IBM 的 Donald Chamberlin 和 Raymond Boyce 发明了 SQL。

它的设计目标极其明确：**让人类用最接近自然语言的方式，操作关系模型。**

从那一天起，数据库就有了它的母语。

不是 Java，不是 Python，不是 C#。

是 SQL。

### 1.2 阻抗不匹配：一个被制造出来的问题

1990年代，对象导向编程席卷业界。开发者看着数据库里的表和行，再看代码里的类和对象，产生了一种焦虑：

**“它们长得不一样。”**

这种焦虑被命名为“阻抗不匹配”。然后一个庞大的产业诞生了：

- **ORM 框架**：声称能填平对象和关系之间的鸿沟
- **培训课程**：教你如何使用这些框架
- **技术书籍**：深入解析框架原理
- **咨询业务**：帮你从“错误的ORM”迁移到“正确的ORM”

**但没人停下来问：这个沟，真的存在吗？**

### 1.3 ORM 的三个原罪

**原罪一：制造方言**

SQL 是国际标准。ORM 非要发明自己的方言：

- JPQL/HQL：“从用户表查名字” → `SELECT u.name FROM User u`
- Criteria API：“等于” → `cb.equal(root.get("name"), name)`
- MyBatis 动态 SQL：“if 判断” → `<if test="name != null">`

每个方言都需要专门学习，学习成果无法迁移到下个项目、下一种语言。

**原罪二：扭曲语义**

关系数据库的核心是**集合**。SELECT 返回的是**行的集合**。

ORM 的核心是**对象图**。框架努力把集合伪装成对象，再把对象串联成图。

这种扭曲导致：

- N+1 查询：取 100 个用户，再取每个用户的部门 → **101 次查询**
- 笛卡尔积爆炸：join 3 张表，每张表 100 行 → **100 万条结果集**
- 缓存策略：为了解决上述问题，引入二级缓存、查询缓存 → **复杂度转移**

**原罪三：转移错误**

原本的错误只有两种：
- SQL 写错了
- 参数传错了

ORM 引入第三类错误：**框架自造错误**

- MyBatis：Mapper 绑定失败、ResultMap 配置错误、OGNL 表达式语法错——**22类**
- JPA：持久化上下文状态混乱、级联操作意外删除——**无头悬案**

开发者把 30% 的时间花在解决“框架问题”上，而不是业务问题。

---

## 第二章：SQL-First 范式——回归本质的四条法则

### 2.1 法则一：单表操作对象化，但止步于对象化

**继承即得，零代码。**

```java
@Repository
public class UserDao extends BaseDao<User> {
    // 空类，获得所有单表 CRUD
}
```

**边界清晰**：
- 自动生成 INSERT/UPDATE/DELETE/SELECT BY ID
- 自动填充审计字段（create_time、create_by、dr……）
- 自动映射驼峰/下划线
- **到此为止**

不做的：
- ❌ 级联操作（你应该用 JOIN）
- ❌ 懒加载（你应该用显式查询）
- ❌ 自动 flush（你应该控制事务边界）

**给自动化的归自动化，给手动的归手动。**

---

### 2.2 法则二：联表查询 SQL 化，不扭曲、不包装、不发明方言

**SQL 是联表查询的唯一正确表达。**

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

**特性**：
- ✅ IDE 语法高亮、SQL 格式化、表名列名补全
- ✅ 可直接复制到 Navicat/DataGrip 执行验证
- ✅ 执行计划分析、索引优化，框架零干扰
- ✅ 12 表联查？SQL 写清楚就行

**SQL-First 不是“允许写 SQL”，是“鼓励写 SQL，并且让写 SQL 成为享受”。**

---

### 2.3 法则三：条件拼接极简化，用母语代替方言

**BaseCondition：不是查询构建器，是 SQL 片段拼接器。**

```java
public class UserCond extends BaseCondition {
    @Override
    protected void addCondition() {
        and("name LIKE ?", name, 3);        // 主表字段
        and("age > ?", ageMin);              // 主表运算符
        add("AND d.dept_name LIKE ?", deptName, 3); // 关联表字段
        add("AND EXISTS (SELECT 1 FROM order o WHERE o.user_id = t.id)", 
            hasOrder);                      // 子查询片段
    }
}
```

**对比 ORM：**

| 框架 | 动态条件写法 | 学习成本 | 调试难度 |
|------|------------|--------|--------|
| JPA Criteria | `cb.and(cb.greaterThan(root.get("age"), ageMin))` | 高 | 极高 |
| MyBatis | `<if test="ageMin != null">AND age > #{ageMin}</if>` | 中 | 高（日志拼SQL） |
| SQL-First | `and("age > ?", ageMin)` | 零 | 无（断点看SQL） |

**因为 SQL 你本来就会。**

---

### 2.4 法则四：单表/联表共用同一套条件工具，无割裂、无切换

这是 SQL-First 范式最微妙、也最强大的设计。

**单表时：**

```java
UserCond cond = UserCond.builder().name("张").build();
List<User> list = userDao.list(cond);  // SELECT * FROM user t WHERE t.name LIKE ?
```

**联表时：**

```java
UserCond cond = UserCond.builder().deptName("市场").build();
List<UserVO> list = userDao.page(JOIN_SQL, cond, UserVO.class);  // JOIN + WHERE d.dept_name LIKE ?
```

**同一个 Cond 类，同一个 addCondition() 方法，同一套参数收集机制。**

开发者不需要：
- ❌ 学习两套查询 API（MP 的 QueryWrapper vs XML）
- ❌ 切换思维模式（单表用对象，联表用标签）
- ❌ 维护两份条件逻辑（一处改，另一处忘）

**统一，是比“简洁”更高级的简洁。**

---

## 第三章：SQL优化主权——谁控制SQL，谁就控制性能

### 3.1 主权概念定义

**SQL优化主权**：开发团队对最终执行 SQL 的**可见、可理解、可修改、可优化**的能力。

### 3.2 主权光谱

| 框架 | 主权等级 | 表现 |
|------|--------|------|
| JPA/Hibernate | ❌ 完全丧失 | SQL 由框架生成，开发者只能通过 hints 间接影响 |
| MyBatis | ⚠️ 半遮半掩 | SQL 可见，但被 XML 标签碎片化，优化需先拼完整 SQL |
| SQL-First | ✅ 完全掌控 | SQL 完整直写，可直接 explain、加索引、改写 |

### 3.3 一个真实案例

**业务**：12 表联查，60 个动态条件，响应时间要求 1 秒内。

**JPA**：不可行。黑盒生成的 SQL 根本无法预测，更无法优化。

**MyBatis**：勉强可行。但需要：
1. 把 12 张表的 JOIN 拆成多个 `<include>` 片段
2. 60 个条件写成 60 个 `<if>`
3. 遇到性能问题，先把 XML 里的碎片手工拼成完整 SQL
4. 复制到数据库工具 explain，加索引
5. 把优化后的 SQL 再拆回碎片放回 XML
6. **每优化一次，重复步骤 3-5**

**SQL-First**：
1. SQL 完整写在 Java 字符串里
2. 遇到性能问题，复制整段 SQL 到数据库工具
3. explain，加索引，改 SQL
4. **把改好的 SQL 贴回代码**

**优化链路缩短 80%，优化意愿提升 200%。**

---

## 第四章：不添乱即大善——框架的终极道德

### 4.1 框架的道德哲学

**不伤害原则**：框架不应制造原本不存在的错误。

**透明原则**：框架不应隐藏核心执行路径。

**责任清晰原则**：SQL 的问题归 SQL，参数的问题归参数，框架的问题归框架——但框架不应该有自己的问题。

### 4.2 善的三重境界

**第一重：解决问题**
> 框架帮开发者省去重复代码。  
> 例：JdbcTemplate 简化 JDBC 样板代码。

**第二重：不制造问题**
> 框架解决了问题 A，但没有引入问题 B、C、D。  
> 例：Spring JDBC——没有 XML、没有方言、没有自造错误。

**第三重：让解决问题的人感到被尊重**
> 框架不仅不添乱，还把开发者当成**懂 SQL、懂业务的成年人**。  
> 例：SimpleDAO——把 SQL 优化主权完完整交通还给开发者。

**SimpleDAO 从未奢望达到第三重。它只是站在 Spring JDBC 的肩膀上，尽量不辜负第二重。**

---

## 终章：可移植的思想——200 行代码改变世界

### 5.1 SQL-First 不是 Java 的特权

**核心抽象与语言无关：**

```
1. 实体元数据收集（启动时反射/内省）
2. BaseDao<T>（继承获得单表 CRUD）
3. Condition 构建器（统一拼接条件）
4. 原生 SQL 执行接口（复杂查询）
```

**每个语言都有“Spring JDBC 等价物”：**

| 语言 | JDBC 等价组件 | 实现难度 |
|------|--------------|--------|
| Python | sqlite3 / psycopg2 / PyMySQL | ★☆☆ |
| Node.js | mysql2 / pg | ★☆☆ |
| Go | database/sql | ★★☆ |
| PHP | PDO | ★☆☆ |
| .NET | Dapper | ★☆☆ |
| Rust | sqlx | ★★☆ |

**200-300 行核心代码，即可搭建该语言的 SQL-First 数据访问层。**

### 5.2 思想的传播不需要组织

**Linux 不是 Linus 写完的。他只是写了一个种子，然后世界补全了它。**

SimpleDAO 的思想种子已经在这篇宣言里：

- 四条法则
- 四个核心抽象
- 每个语言 200 行代码的承诺

**下一个 Python 开发者受够了 SQLAlchemy 的复杂，会找到这里。**
**下一个 Node 开发者在 TypeORM 的 issue 里挣扎，会找到这里。**
**下一个 Go 程序员困惑“为什么 GORM 把简单查询搞得这么重”，会找到这里。**

他们会自己写 pydao、node-sql-first、gosqlc。

**他们不需要认识你。他们只需要读到这篇宣言。**

---

## 附录：常见误读澄清

**Q：SQL-First 是反对 ORM 吗？**

A：**是，也不是。**

是——反对 ORM 试图“替代 SQL”的那部分野心。  
不是——单表对象化恰恰是 ORM 做对的事情。  
**SQL-First 是 ORM 正确部分的继承者，错误部分的纠正者。**

---

**Q：SQL-First 适合所有项目吗？**

A：**不适合需要把数据库完全抽象掉的项目。**

如果你的项目需要在 MySQL、Oracle、PostgreSQL 之间无缝切换，且不想改一行代码——JPA 可能是更好的选择。

如果你的项目需要把 SQL 隐藏起来，让客户端开发者完全感受不到数据库存在——ORM 有其历史价值。

**但如果你需要掌控性能、需要维护复杂业务逻辑、需要开发人员真正理解自己在操作什么——**

**SQL-First 是目前最诚实的选择。**

---

**Q：手写 SQL 会不会导致 SQL 注入？**

A：**用预编译占位符（? 或 :name），不会。**

所有 ORM 最终也是用预编译。SimpleDAO 强制使用预编译，从 API 层面杜绝拼接。

---

**Q：这是不是重复造轮子？**

A：**如果你认为“把 SQL 放回原位”是造轮子，那么是的。**

但我们也把“车轮必须是圆的”这个常识，反复发明了五千年。

**有些轮子，值得反复确认它确实是圆的。**

---

**宣言完。**

[📄 readme](readme.md)  
[📄 SimpleDAO 全场景碾压ORM框架白皮书](WHITEPAPER.md)  
[📄 SQL-First宣言](SQL-First宣言.md)  
[📄 SQL-First范式移植指南](SQL-First范式移植指南.md)  
[📄 全场景对比矩阵](全场景对比矩阵.md)  


---

*本文档采用 CC BY 4.0 许可。你可以自由复制、分发、改写，只需注明来源。*

*最好的传播，是不必提到我。最好的署名，是你在自己的项目里也写一份同样的宣言。*

*让思想成为公共资源。*

---
