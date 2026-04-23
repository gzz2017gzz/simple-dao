package com.simple.common.base.dialect;

public class MySqlDialect implements DbDialect {

    @Override
    public String buildPageSql(String sql, int offset, int limit) {
        return sql + " LIMIT " + offset + ", " + limit;
    }
 
}