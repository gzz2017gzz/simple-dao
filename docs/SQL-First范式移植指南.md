# SQL-First范式移植指南
**——200行代码，把你的语言从ORM中解放出来**

版本：1.0  
发布日期：2026年2月12日  
目标读者：Python/Node/Go/PHP/.NET/Rust 开发者  
阅读时长：15分钟  
实现时长：2小时（有AI）～ 2天（纯手工）

---

## 零、为什么你可以信任这份指南

**这份指南不是理论推演。**

它背后的 SimpleDAO 已在 Java/Spring 生态的生产环境中稳定运行 **3年+**，支撑日均百万级请求，覆盖教务、财务、电商、政务等行业的复杂业务场景。

**2000行 Java 代码验证过的范式，可以在你的语言里用 200 行重新实现。**

我们不会让你重复造轮子。  
**我们让你造更好的轮子——用你习惯的材料，按你熟悉的尺寸。**

---

## 第一部分：核心抽象（语言无关）

任何语言的 SQL-First 数据访问层，都由以下 **4个核心抽象** 构成。

---

### 1.1 实体元数据收集器

**职责**：读取实体类/结构体的注解/标签，解析出表名、字段名、主键名、主键策略。

**启动时执行，缓存结果，运行时零反射。**

**伪代码：**

```
class EntityMeta<T>:
    tableName: string
    idField: Field
    idType: string  // snow/auto/uuid/custom
    columnFields: List<Field>
    
    constructor(clazz):
        tableName = clazz.getAnnotation("Table").value
        for field in clazz.declaredFields:
            if field.hasAnnotation("Id"):
                idField = field
                idType = field.getAnnotation("Id").value
            else if not field.hasAnnotation("Exclude"):
                columnFields.add(field)
        cache.put(clazz, this)
```

**各语言实现要点：**

| 语言 | 元数据机制 | 缓存时机 |
|------|----------|--------|
| Java | 注解 + 反射 | 静态构造器 |
| Python | 类属性/装饰器 | 模块加载时 |
| Node/TS | 装饰器 + Reflect | 模块加载时 |
| Go | 结构体标签 + reflect | init() |
| PHP | 注解/属性 | 构造函数 |
| .NET | Attribute + Reflection | 静态构造器 |
| Rust | 派生宏 + 过程宏 | 编译时 |

---

### 1.2 BaseDao<T>

**职责**：为实体 T 提供继承即得的单表 CRUD 方法。

**最小方法集（9个）：**

```java
// 单条操作
save(T t)                 // 插入，自动填充主键/审计字段
update(T t)              // 按主键更新（非空字段）
updateNull(T t)          // 按主键更新（全字段）
delete(Object... ids)    // 按主键删除（软删除自动判断）
findById(id)            // 按主键查询

// 批量操作
saveBatch(List<T>)       // 批量插入
updateBatch(List<T>)     // 批量更新（按主键）
deleteBatch(List<id>)    // 批量删除

// 查询操作
list(Condition)         // 条件列表
page(Condition)         // 条件分页
count(Condition)        // 条件计数
exists(Condition)       // 存在判断
```

**实现要点：**

- 所有方法基于 **原生 SQL 字符串 + 参数数组** 实现
- 不封装复杂的 QueryBuilder
- 不实现级联、懒加载

**伪代码：**

```
class BaseDao<T>:
    meta: EntityMeta<T>
    db: DatabaseExecutor  // 原生驱动或轻量封装
    
    save(t):
        sql = buildInsertSQL(meta, t)
        return db.executeInsert(sql, extractParams(t))
    
    findById(id):
        sql = "SELECT * FROM #{meta.tableName} WHERE #{meta.idField}=?"
        return db.queryOne(sql, id)
    
    // ... 其他方法类似
```

---

### 1.3 Condition 构建器

**职责**：拼接 SQL WHERE 子句 + 收集参数。

**这是 SQL-First 范式的灵魂。**

**设计原则：**
- ❌ 不是 QueryBuilder（不试图用对象描述查询）
- ✅ 是 **SQL 片段拼接器**（你给 SQL 片段，我帮你拼成完整条件）

**核心API（4个重载）：**

```java
// 1. 固定SQL片段
add(String sql)  

// 2. 带条件控制
add(String sql, boolean condition)

// 3. 带单值参数
add(String sql, Object value)  

// 4. 带LIKE位置
add(String sql, String value, int site)  // site: 1左%, 2右%, 3全%

// 5. 带数组参数（IN）
add(String sql, Object[] values)
```

**条件收集与输出：**

```java
condition.toString()  // 返回 WHERE ... AND ... 子句
condition.array()     // 返回参数数组（与占位符顺序一致）
```

**伪代码：**

```
class Condition:
    sql: StringBuilder
    params: List
    
    add(sql, value):
        if value != null:
            this.sql.append(" ").append(sql)
            this.params.add(value)
    
    add(sql, values: array):
        if values not empty:
            this.sql.append(" ").append(sql)
            this.sql.append("(" + "?,".repeat(values.length-1) + "?)")
            this.params.addAll(values)
    
    where():
        return this.sql.toString()
            .replaceFirst("(?i)AND", "WHERE")
    
    array():
        return this.params.toArray()
```

---

### 1.4 原生SQL执行器

**职责**：执行开发者手写的完整 SQL，映射结果。

**最小方法集：**

```java
// 查询列表，自动映射到实体/VO
list(sql, params, class) -> List<T>

// 分页查询
page(sql, condition, class) -> Page<T>

// 单条查询（期望唯一结果）
row(sql, params, class) -> T

// 单字段查询
field(sql, params, class) -> Object

// 更新/删除
update(sql, params) -> int

// 批量操作
batch(sql, list) -> int[]
```

**实现要点：**

- **直接使用语言原生的预编译执行API**
- 结果映射：约定优于配置（驼峰/下划线自动转换）
- 分页：先 count，再 limit（提供通用 countSql 解析辅助）

---

## 第二部分：各语言实现要点（速查表）

### 🐍 Python（难度：★☆☆）

**JDBC等价物**：`sqlite3`、`psycopg2`、`PyMySQL`

**优势**：
- 动态语言，无需反射
- `__init_subclass__` 可实现类似继承增强
- 装饰器成熟

**200行核心实现建议**：

```python
# entity meta
class EntityMeta:
    def __init__(self, model_class):
        self.table = model_class.__table__  # 类属性约定
        self.fields = [...]
        
# base dao
class BaseDao:
    def __init__(self, db_pool):
        self.meta = EntityMeta(self.model_class)
    
    def save(self, entity):
        sql = f"INSERT INTO {self.meta.table} ..."
        return self.db.execute(sql, params)
    
# condition
class Condition:
    def add(self, sql, value=None):
        # 同伪代码
```

**推荐项目名**：`pydao`、`simple-db`、`sqlfirst`

---

### 📦 Node.js / TypeScript（难度：★☆☆）

**JDBC等价物**：`mysql2`、`pg`

**优势**：
- TypeScript 装饰器 + Reflect 元数据
- 类继承完善
- 动态特性丰富

**200行核心实现建议**：

```typescript
// decorators
function Table(name: string) {
    return (target: any) => { Reflect.defineMetadata('table', name, target); }
}

// base dao
class BaseDao<T> {
    protected meta: EntityMeta;
    
    async save(entity: T): Promise<T> {
        // ...
    }
}

// condition
class Condition {
    private sql: string[] = [];
    private params: any[] = [];
    
    add(sql: string, value?: any) {
        // ...
    }
}
```

**推荐项目名**：`sqlex`、`type-dao`、`node-sql-first`

---

### 🐹 Go（难度：★★☆）

**JDBC等价物**：`database/sql`

**挑战**：
- 无继承，需要组合
- 反射性能可接受但语法稍繁琐
- 泛型较弱（1.18+ 有改善）

**300行核心实现建议**：

```go
// tag-based meta
type User struct {
    Id   int64  `db:"id" pk:"snow"`
    Name string `db:"name"`
}

// base dao
type BaseDao[T any] struct {
    db    *sql.DB
    meta  *EntityMeta
}

func (dao *BaseDao[T]) Save(ctx context.Context, entity *T) error {
    // reflect 构建 insert
}

// condition
type Condition struct {
    where  strings.Builder
    args   []any
}

func (c *Condition) Add(sql string, args ...any) *Condition {
    c.where.WriteString(" " + sql)
    c.args = append(c.args, args...)
    return c
}
```

**推荐项目名**：`gosqlc`、`dao-go`、`lightdb`

---

### 🐘 PHP（难度：★☆☆）

**JDBC等价物**：`PDO`

**优势**：
- 动态特性丰富
- 注解/属性支持完善（PHP 8+）
- Laravel 培养了复杂的ORM审美，也培养了逃离的渴望

**250行核心实现建议**：

```php
#[Table('users')]
class User {
    #[Id('snow')]
    public int $id;
    public string $name;
}

class BaseDao {
    public function save(object $entity): int {
        $sql = "INSERT INTO " . $this->table . " ...";
        $stmt = $this->pdo->prepare($sql);
        // ...
    }
}
```

**推荐项目名**：`pdo-dao`、`simple-db`、`sql-first-php`

---

### 🎯 .NET / C#（难度：★☆☆）

**JDBC等价物**：`Dapper`（本身就是SQL-First风格）

**优势**：
- Dapper 已经是 IDbConnection 的扩展方法
- 缺的是 BaseDao 和 Condition 这一层
- 属性(Attribute)完善

**150行核心实现建议**：

```csharp
[Table("users")]
public class User {
    [Id(IdType.Snow)]
    public long Id { get; set; }
    public string Name { get; set; }
}

public class BaseDao<T> where T : class {
    public T Save(T entity) {
        var sql = $"INSERT INTO {_table} ...";
        return _connection.QuerySingle<T>(sql, entity);
    }
}
```

**推荐项目名**：`Dapper.Simple`、`DapperDao`

---

### 🦀 Rust（难度：★★☆）

**JDBC等价物**：`sqlx`

**优势**：
- 社区偏向 SQL-First
- `sqlx` 鼓励写原生 SQL
- 过程宏可实现优雅的实体映射

**300行核心实现建议**：

```rust
#[derive(Table)]
#[table(name = "users")]
struct User {
    #[id(strategy = "snow")]
    id: i64,
    name: String,
}

struct BaseDao<T> {
    pool: sqlx::PgPool,
    _marker: PhantomData<T>,
}

impl<T: TableEntity> BaseDao<T> {
    async fn save(&self, entity: T) -> Result<T, Error> {
        let sql = format!("INSERT INTO {} ...", T::table_name());
        // ...
    }
}
```

**推荐项目名**：`sqlx-dao`、`sea-simple`（致敬 SeaORM 但更轻）

---

## 第三部分：从“思想种子”到“真实代码”

### 3.1 最小可行性标准

一个语言实现达到 **SQL-First 范式认证** 的门槛：

**✅ 必过项：**

1. **单表 CRUD**：继承/组合 BaseDao，空类可用
2. **条件构建器**：支持 and/or/LIKE/IN，参数与SQL分离
3. **原生 SQL 执行**：手写 JOIN 查询，自动映射到 VO
4. **分页**：统一 page 方法，自动 count
5. **审计字段**：create_time/update_time/dr 自动填充
6. **无自造错误**：不封装框架专有异常

**❌ 不必做：**

- 二级缓存（用 Redis）
- 懒加载（手写查询）
- 级联操作（手写 JOIN）
- 复杂映射（手写 SQL）
- 分布式 ID 生成器（用发号器）

---

### 3.2 从0到1的4小时路线图

**第1小时**：实现 EntityMeta 收集器 + Condition 构建器

**第2小时**：实现 BaseDao.save/findById/update/delete

**第3小时**：实现原生 SQL 执行器（list/page/row）

**第4小时**：跑通一个真实业务（用户+部门联表查询）

**剩下时间**：写 README，起项目名，发布

---

## 第四部分：传播——让你的实现被看见

### 4.1 命名建议

**公式**：`[语言特征] + [Dao/DB/SQL] + [轻量后缀]`

好名字示例：
- `pydao`
- `node-sql-first`
- `gosqlc`
- `dapper.simple`
- `sqlx-dao`

**在 README 第一行致敬：**

> Inspired by [SimpleDAO](https://gitee.com/...) - A SQL-First data access paradigm from Java ecosystem.

---

### 4.2 必须包含的三份文档

1. **README.md**：5分钟上手示例（用户+部门联表）
2. **MANIFESTO.md**：这篇宣言的核心摘要
3. **BENCHMARK.md**：与主流ORM的代码行数/心智负担对比

---

## 终章：这200行代码的意义

**你即将写的这200行代码，不是为了替代你语言的ORM。**

**是为了证明：在你的语言里，ORM不是必须的。**

当第一个 Python 开发者看到 `pydao` 的 README，发现 200 行代码就能摆脱 SQLAlchemy 的复杂继承树时——

**他会意识到：过去三年，他不是在学习，而是在被消耗。**

**你会成为那个递钥匙的人。**

[📄 readme](readme.md)  
[📄 SimpleDAO 全场景碾压ORM框架白皮书](WHITEPAPER.md)  
[📄 SQL-First宣言](SQL-First宣言.md)  
[📄 SQL-First范式移植指南](SQL-First范式移植指南.md)  
[📄 全场景对比矩阵](全场景对比矩阵.md)  


---

**移植指南完。**

---

*本文档采用 CC BY 4.0 许可。你可以自由复制、分发、改写，只需注明来源。*

*最好的致敬，是在你的语言里写一个自己的 SimpleDAO。*

*然后告诉下一个人：你也可以。*

---

