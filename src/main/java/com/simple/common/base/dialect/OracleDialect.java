package com.simple.common.base.dialect;

public class OracleDialect implements DbDialect {

    @Override
    public String buildPageSql(String sql, int offset, int limit) {
        int endRow = offset + limit;
        return "SELECT * FROM (SELECT t__.*, ROWNUM rn__ FROM (" + sql + ") t__ WHERE ROWNUM <= " + endRow + ") WHERE rn__ > " + offset;
    }
}