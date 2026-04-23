package com.simple.common.base.dialect;

public class SqlServerDialect implements DbDialect {

    @Override
    public String buildPageSql(String sql, int offset, int limit) {
        return sql + " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
    }
}