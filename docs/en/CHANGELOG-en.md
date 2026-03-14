# CHANGELOG-en.md

## [1.2.0] - 2026-02-12
### Added
- Released SQL-First paradigm documentation system (Manifesto, Comparison Matrix, Paradigm Standard, Technical Whitepaper, Porting Guide).
- Enhanced `BaseCondition` builder: supports `add(String sql, boolean condition)` for dynamic fragment control.
- Introduced `@BusinessAuth` annotation for non-intrusive data permission control via AOP.
- Optimized batch operations `saveBatch`/`replaceBatch` by reusing Spring `JdbcTemplate`'s native batch processing.

### Improved
- Audit field population logic: `createTime`/`updateTime` automatically filled, `dr` soft delete handled by default.
- `page` method now features intelligent COUNT SQL parsing to avoid subquery performance pitfalls.
- Added `Sql.fill()` utility to print complete SQL with parameters, enhancing debugging experience.

### Fixed
- Fixed issue where `BaseDao.update` incorrectly included the primary key as an updatable field.
- Fixed sequence overflow issue in `SnowflakeId` under extreme concurrency.

---

## [1.1.0] - 2025-08-01
### Added
- Experimental `@BusinessAuth` data permission annotation.
- `BaseDao.updateNull` method for full-field updates (including nulls).
- `BaseCondition.mergeParams` static method for convenient merging of multiple condition class parameters.

### Improved
- Refactored `BaseJdbc`, extracting pagination logic into separate methods for easier overriding.
- Optimized `Sql.countSql` parsing algorithm to support more complex SQL (subqueries, CTEs).

### Fixed
- Fixed pagination SQL compatibility issues with Oracle database (manual dialect configuration required).

---

## [1.0.0] - 2024-05-10
### Initial Release
- Core `BaseDao` implemented, enabling zero-code single-table CRUD.
- Dual-cache design: entity metadata parsed via reflection at startup, zero reflection overhead at runtime.
- Supports four primary key strategies: Snowflake, UUID, auto-increment, and custom.
- Automatic audit field population: `createTime`, `createBy`, `updateTime`, `updateBy`, `dr`.
- `BaseCondition` builder supporting AND/OR/LIKE/IN and other dynamic conditions.
- Native SQL executor `BaseJdbc` providing `list`/`page`/`row`/`field`/`update` methods.
- Built-in `Page` model for unified pagination results.
- Seamless integration with Spring transaction management, no extra configuration required.

---