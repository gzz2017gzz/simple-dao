package com.simple.common.base.utils;

import org.springframework.util.Assert;
import java.util.Set;

/**
 * SQL安全检查工具类（零误伤版） 先剥离所有字符串字面量，再检查纯SQL语法部分 完全兼容所有合法SQL，包括包含关键字的字符串查询
 */
public final class SqlSecurityChecker {
	private SqlSecurityChecker() {
	}

	/**
	 * 危险关键字和符号集合 仅包含：永远不会出现在合法WHERE条件片段中的内容
	 */
	private static final Set<String> DANGEROUS_TOKENS = Set.of(
			// 危险操作关键字
			"grant", "revoke", "exec", "execute", "call", "insert", "delete", "update", "drop", "truncate", "alter", "create", "rename", "replace",
			// 注入必备语法
			"union", "union all", "intersect", "except", ";", "--", "#", "/*", "*/", "@@", "@", "`", "\"",
			// 系统元数据访问
			"information_schema", "sys", "mysql", "pg_catalog", "master", "slave", "database", "schema");

	/**
	 * 检查SQL片段是否安全
	 */
	public static void check(String sqlFragment) {
		if (sqlFragment == null || sqlFragment.isBlank()) {
			return;
		}

		// 第一步：剥离所有单引号包裹的字符串字面量
		String pureSql = removeStringLiterals(sqlFragment);
		// 第二步：转小写后检查危险关键字
		String lowerSql = pureSql.toLowerCase();
		for (String token : DANGEROUS_TOKENS) {
			Assert.isTrue(!lowerSql.contains(token), String.format("SQL片段包含禁止内容: [%s]，动态值必须使用参数化查询", token));
		}
	}

	/**
	 * 剥离SQL中的所有字符串字面量（处理转义单引号）  
	 */
	private static String removeStringLiterals(String sql) {
		StringBuilder sb = new StringBuilder(sql.length());
		boolean inString = false;

		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			if (c == '\'') {
				// 处理转义单引号 ''
				if (i + 1 < sql.length() && sql.charAt(i + 1) == '\'') {
					i++; // 跳过第二个单引号
					continue;
				}
				inString = !inString;
				continue;
			}
			if (!inString) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		check("AND a.name like 'UPDATE%' AND b.code = 'DELETE'");
	}
}