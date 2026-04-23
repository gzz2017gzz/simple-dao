package com.simple.common.base.dialect;

/**
 * @author 高振中
 * @summary 【数据库方言】—— 只封装分页语法差异
 * @date 2026-04-23
 */
public interface DbDialect {

	/** 分页 SQL */
	String buildPageSql(String sql, int offset, int limit);

	// ============ 工厂方法 ============

	static DbDialect of(java.sql.Connection conn, String configDialect) {
		// 1. 显式配置优先
		if (configDialect != null && !configDialect.isBlank()) {
			return fromName(configDialect);
		}
		// 2. 自动检测
		if (conn != null) {
			try {
				String name = conn.getMetaData().getDatabaseProductName().toLowerCase();
				if (name.contains("postgresql"))
					return new PostgreSqlDialect();
				if (name.contains("sqlserver") || name.contains("mssql"))
					return new SqlServerDialect();
				if (name.contains("oracle"))
					return new OracleDialect();
				if (name.contains("h2"))
					return new MySqlDialect();
			} catch (Exception ignored) {
			}
		}
		// 3. 兜底 MySQL
		return new MySqlDialect();
	}

	private static DbDialect fromName(String name) {
		return switch (name.toLowerCase()) {
		case "mysql", "mariadb", "h2" -> new MySqlDialect();
		case "postgresql", "postgres" -> new PostgreSqlDialect();
		case "sqlserver", "mssql" -> new SqlServerDialect();
		case "oracle" -> new OracleDialect();
		default -> new MySqlDialect();
		};
	}
}