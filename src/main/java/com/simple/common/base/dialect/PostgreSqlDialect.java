package com.simple.common.base.dialect;

public class PostgreSqlDialect implements DbDialect {

    @Override
    public String buildPageSql(String sql, int offset, int limit) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }
 
}