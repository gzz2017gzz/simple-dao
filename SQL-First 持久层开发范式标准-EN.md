# SQL-First Paradigm Standard for Persistence Layer Development

SimpleDAO has proposed and implemented a brand-new development paradigm for the persistence layer:
With **SQL First** as the core ideology, native database access interfaces as the foundation, and characterized by **ultra-low intrusion, ultra-thin encapsulation, and ultra-high business concentration**, it unifies the data operation paradigm across all programming languages.
It does not build an ecosystem, reinvent the wheel, or rely on upper-layer containers, but only provides a set of **replicable, migratable, and implementable** engineering methodologies.

---

## 1. SQL-First Paradigm Certification Criteria (Cross-Language Implementation Standards)
### ✅ Mandatory Implementations (Must-Have)
1. **Single-Table CRUD**: Inherit/combine BaseDao, and an empty class can be used out of the box.
2. **Condition Builder**: Support conditions such as AND / OR / LIKE / IN, with parameters securely separated from SQL.
3. **Native SQL Execution**: Support handwritten JOIN / subqueries, and results are automatically mapped to VO (Value Object).
4. **Unified Pagination**: Provide a universal `page` method to automatically handle count and pagination logic.
5. **Automatic Handling of Audit Fields**: Automatic population of audit fields like `create_time`, `update_time`, and `dr` (logical deletion).
6. **No Self-Created Errors**: Do not encapsulate framework-specific exceptions to maintain transparency and ease of troubleshooting.

### ❌ Non-Mandatory Implementations (Not Required)
- Do not implement secondary cache (Redis is recommended).
- Do not implement lazy loading (handwritten precise queries are recommended).
- Do not implement cascade operations (handwritten JOIN is recommended).
- Do not implement complex object mapping (handwritten SQL is recommended).
- Do not bind distributed ID generators (independent ID generation services are recommended).