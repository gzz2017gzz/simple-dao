# SimpleDAO API Cheat Sheet

## BaseCondition

| Category | Method | Purpose | Example |
|----------|--------|---------|---------|
| Common (main/related tables) | `add(String sql)` | Fixed SQL fragment | Case03 - Condition class |
| Common (main/related tables) | `add(String sql, boolean condition)` | Conditional control, dynamically added | Case03 - Condition class |
| Common (main/related tables) | `add(String sql, Object value)` | Single parameter condition | Case01 - Condition class |
| Common (main/related tables) | `add(String sql, Object[] values)` | IN condition, array expands automatically | Case02 - Condition class |
| Common (main/related tables) | `add(String sql, String value, int site)` | Fuzzy query, site=1 left/2 right/3 both sides | Case01 - Condition class |
| Main table syntactic sugar | `and(String field, Object value)` | Main table equality condition, automatically adds `AND t. ?` | Case01 - Condition class |
| Main table syntactic sugar | `and(String field, String value, int site)` | Main table fuzzy query | Case01 - Condition class |
| Main table syntactic sugar | `in(String field, Object[] ids)` | Main table IN condition | Case01 - Condition class |
| Main table syntactic sugar | `notIn(String field, Object[] ids)` | Main table NOT IN condition | Case01 - Condition class |
| Condition finalizer | `where()` | Outputs complete WHERE clause | Case05 - DAO class |
| Condition finalizer | `and()` | Outputs fragment that retains AND | Case06 - DAO class |
| Condition finalizer | `array()` | Returns parameter list array | Case02 - DAO class |
| Condition finalizer | `mergeParams(BaseCondition...)` | Static method, merges parameters from multiple conditions | Case06 - DAO class |

---

**General Notes**:
- Fuzzy query `site`: 1 = left%, 2 = right%, 3 = %both%.
- For related table conditions, use `add("AND u.xxx = ?", value)` directly; do not use the `and` syntactic sugar.
- `mergeParams` passes condition classes in the order of `?` appearances in the SQL.

---

## BaseDao

| Category | Method | Purpose | Log overload | Example |
|----------|--------|---------|--------------|---------|
| Insert | `save(T)` | Single insert, automatic audit fields | ✅ Yes (`save(boolean, T)`) | Case01 - DAO class |
| Insert | `saveBatch(List<T>)` | Batch insert | ✅ Yes (`saveBatch(boolean, List<T>)`) | Case01 - DAO class |
| Insert | `replace(T)` | Single replace | ✅ Yes (`replace(boolean, T)`) | Use directly |
| Insert | `replaceBatch(List<T>)` | Batch replace | ✅ Yes (`replaceBatch(boolean, List<T>)`) | Use directly |
| Update | `update(T)` | Update by primary key, null fields are ignored | ✅ Yes (`update(boolean, T)`) | Case01 - DAO class |
| Update | `updateNull(T)` | Update by primary key, null fields are included | ✅ Yes (`updateNull(boolean, T)`) | Use directly |
| Update | `update(T, cond)` | Conditional update, null fields ignored | ✅ Yes (`update(boolean, T, C)`) | Use directly |
| Update | `updateNull(T, cond)` | Conditional update, null fields included | ✅ Yes (`updateNull(boolean, T, C)`) | Use directly |
| Delete | `delete(id...)` | Delete by primary key, automatic logical delete | ✅ Yes (`delete(boolean, Object...)`) | Case01 - DAO class |
| Delete | `delete(cond)` | Conditional delete | ✅ Yes (`delete(boolean, C)`) | Use directly |
| Single query | `findById(id)` | Find by primary key | ✅ Yes (`findById(boolean, id)`) | Case01 - DAO class |
| Single query | `findById(id, lock)` | Find by primary key + row lock | ✅ Yes (`findById(boolean, id, lock)`) | Use directly |
| Single query | `findOne(cond)` | Find one by condition, throws exception if more than one | ✅ Yes (`findOne(boolean, C)`) | Case01 - DAO class |
| List query | `list(cond)` | List by condition | ✅ Yes (`list(boolean, C)`) | Case05 - DAO class |
| List query | `page(cond)` | Pagination by condition | ✅ Yes (`page(boolean, C)`) | Case01 - DAO class |
| Helper | `count(cond)` | Count by condition | ✅ Yes (`count(boolean, C)`) | Case01 - DAO class |
| Helper | `exists(cond)` | Check existence | ✅ Yes (`exists(boolean, C)`) | Use directly |

---

**General Notes**:
- Audit fields (createTime, createBy, updateTime, updateBy) are auto-filled, no manual setting required.
- Logical delete field defaults to `dr`, can be changed by `simple-dao.logic-delete.field`.
- `update(T, cond)` does not update `null` fields; `updateNull(T, cond)` does.
- All `delete` methods automatically perform a logical delete (if the table has a `dr` field).

---

## BaseSql

| Category | Method | Purpose | Log overload | Parameter form | Example |
|----------|--------|---------|--------------|----------------|---------|
| Query form | `list(sql, cond, clazz)` | Multiple rows / multiple columns (4 overloads) | ✅ Yes | Cond / Array | Case05 - Most common arbitrary list |
| Query form | `row(sql, cond, clazz)` | Single row / multiple columns (4 overloads) | ✅ Yes | Cond / Array | Not common |
| Query form | `columns(sql, cond, clazz)` | Multiple rows / single column (4 overloads) | ✅ Yes | Cond / Array | Not common |
| Query form | `field(sql, cond, clazz)` | Single row / single column (4 overloads) | ✅ Yes | Cond / Array | Common |
| Pagination | `page(sql, cond, clazz)` | Auto pagination, COUNT replacement method | ✅ Yes | Cond only | Case02 - Most common arbitrary paginated list |
| Pagination | `page0(sql, cond, clazz)` | Fallback pagination, subquery method | ✅ Yes | Cond only | Extreme scenarios (when SQL contains UNION) |
| Write operation | `update(sql, params...)` | Executes UPDATE/DELETE, enforces WHERE check | ✅ Yes | Array only | Common |
| Batch | `batchOperate(list, sql)` | Batch operation | ✅ Yes | List + named parameters only | Not common |

---

**General Notes**:
- SQL for `update` and `delete` operations must contain the `WHERE` keyword, otherwise it will be blocked.
- All methods have a `boolean logSql` overload to control printing of parameterized SQL.
- `page` and `page0` automatically read `page` and `size` from `cond` and calculate LIMIT.
- Named parameter SQL uses `:fieldName`, and the parameter object is a POJO.