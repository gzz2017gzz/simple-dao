# SimpleDAO API 速查表

## BaseCondition

| 分类 | 方法 | 用途 | 案例 |
|------|------|------|------|
| 主表/关联表通用 | `add(String sql)` | 固定SQL片段 | 案例03 条件类 |
| 主表/关联表通用 | `add(String sql, boolean condition)` | 条件控制，动态加入 | 案例03 条件类 |
| 主表/关联表通用 | `add(String sql, Object value)` | 单参数条件 | 案例01 条件类 |
| 主表/关联表通用 | `add(String sql, Object[] values)` | IN条件，数组自动展开 | 案例02 条件类 |
| 主表/关联表通用 | `add(String sql, String value, int site)` | 模糊查询，site=1左/2右/3前后 | 案例01 条件类 |
| 主表语法糖 | `and(String field, Object value)` | 主表等值条件，自动带 AND t. ? | 案例01 条件类 |
| 主表语法糖 | `and(String field, String value, int site)` | 主表模糊查询 | 案例01 条件类 |
| 主表语法糖 | `in(String field, Object[] ids)` | 主表IN条件 | 案例01 条件类|
| 主表语法糖 | `notIn(String field, Object[] ids)` | 主表NOT IN条件 | 案例01 条件类  |
| 条件收口 | `where()` | 输出完整WHERE条件 | 案例05 DAO类 |
| 条件收口 | `and()` | 输出保留AND的片段 | 案例06 DAO类 |
| 条件收口 | `array()` | 获取参数列表数组 | 案例02 DAO类 |
| 条件收口 | `mergeParams(BaseCondition...)` | 静态方法，合并多条件参数 | 案例06 DAO类 |

---


**通用注意事项**：
- 模糊查询 `site`：1=左%，2=右%，3=前后%。
- 关联表条件直接用 `add("AND u.xxx = ?", value)`，不用 `and` 语法糖。
- `mergeParams` 按 SQL 中 `?` 出现的顺序传入条件类。




## BaseDao 

| 分类 | 方法 | 用途 | 有无日志重载 | 案例 |
|------|------|------|--------------|------|
| 插入 | `save(T)` | 单条插入，自动审计 | ✅ 有 (`save(boolean, T)`) | 案例01 DAO类 |
| 插入 | `saveBatch(List<T>)` | 批量插入 | ✅ 有 (`saveBatch(boolean, List<T>)`) | 案例01 DAO类 |
| 插入 | `replace(T)` | 单条替换 | ✅ 有 (`replace(boolean, T)`) | --直接用 |
| 插入 | `replaceBatch(List<T>)` | 批量替换 | ✅ 有 (`replaceBatch(boolean, List<T>)`) | --直接用 |
| 更新 | `update(T)` | 按主键更新，null不参与 | ✅ 有 (`update(boolean, T)`) | 案例01 DAO类 |
| 更新 | `updateNull(T)` | 按主键更新，null参与 | ✅ 有 (`updateNull(boolean, T)`) | --直接用 |
| 更新 | `update(T, cond)` | 按条件更新，null不参与 | ✅ 有 (`update(boolean, T, C)`) | --直接用 |
| 更新 | `updateNull(T, cond)` | 按条件更新，null参与 | ✅ 有 (`updateNull(boolean, T, C)`) | --直接用 |
| 删除 | `delete(id...)` | 按主键删除，自动逻辑删除 | ✅ 有 (`delete(boolean, Object...)`) | 案例01 DAO类 |
| 删除 | `delete(cond)` | 按条件删除 | ✅ 有 (`delete(boolean, C)`) | --直接用 |
| 单条查询 | `findById(id)` | 按主键查单条 | ✅ 有 (`findById(boolean, id)`) | 案例01 DAO类 |
| 单条查询 | `findById(id, lock)` | 按主键查单条 + 行锁 | ✅ 有 (`findById(boolean, id, lock)`) | --直接用 |
| 单条查询 | `findOne(cond)` | 按条件查单条，多于1条抛异常 | ✅ 有 (`findOne(boolean, C)`) | 案例01 DAO类 |
| 列表查询 | `list(cond)` | 按条件查列表 | ✅ 有 (`list(boolean, C)`) | 案例05 DAO类 |
| 列表查询 | `page(cond)` | 按条件分页 | ✅ 有 (`page(boolean, C)`) | 案例01 DAO类 |
| 辅助 | `count(cond)` | 按条件统计数量 | ✅ 有 (`count(boolean, C)`) | 案例01 DAO类 |
| 辅助 | `exists(cond)` | 判断是否存在 | ✅ 有 (`exists(boolean, C)`) | --直接用 |

---

**通用注意事项**：
- 审计字段（createTime, createBy, updateTime, updateBy）自动填充，无需手动设置。
- 逻辑删除字段默认 `dr`，可通过 `simple-dao.logic-delete.field` 修改。
- `update(T, cond)` 不会更新 `null` 字段；`updateNull(T, cond)` 会。
- 所有 `delete` 方法自动变为逻辑删除（如果表有 `dr` 字段）。



## BaseSql 

| 分类 | 方法 | 用途 | 有无日志重载 | 参数形式 | 案例 |
|------|------|------|--------------|----------|------|
| 查询形态 | `list(sql, cond, clazz)` | 多行多列 (4重重载) | ✅ 有 | Cond / 数组 | 案例05 最常用任意列表 |
| 查询形态 | `row(sql, cond, clazz)` | 单行多列 (4重重载)| ✅ 有 | Cond / 数组 | 不常用 |
| 查询形态 | `columns(sql, cond, clazz)` | 多行单列 (4重重载)| ✅ 有 | Cond / 数组 | 不常用 |
| 查询形态 | `field(sql, cond, clazz)` | 单行单列 (4重重载)| ✅ 有 | Cond / 数组 | 常用  |
| 分页 | `page(sql, cond, clazz)` | 自动分页，COUNT替换法 | ✅ 有 | 仅 Cond | 案例02 最常用任意分页列表 |
| 分页 | `page0(sql, cond, clazz)` | 兜底分页，子查询法 | ✅ 有 | 仅 Cond | 极端场景 含union 关键字时用 |
| 写操作 | `update(sql, params...)` | 执行UPDATE/DELETE，强制WHERE检查 | ✅ 有 | 仅数组 | 常用 |
| 批量 | `batchOperate(list, sql)` | 批量操作 | ✅ 有 | 仅列表+命名参数 | 不常用 |

---

**通用注意事项**：
- `update` 和 `delete` 操作的 SQL 必须包含 `WHERE` 关键字，否则被拦截。
- 所有方法均有 `boolean logSql` 重载，用于控制是否打印带参 SQL。
- `page` 和 `page0` 自动从 `cond` 中读取 `page`、`size` 并计算 LIMIT。
- 命名参数 SQL 使用 `:fieldName`，参数对象为 POJO。

 