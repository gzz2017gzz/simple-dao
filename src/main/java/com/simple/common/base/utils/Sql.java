package com.simple.common.base.utils;

import lombok.SneakyThrows;

import static com.simple.common.base.key.Const.BLANK;
import static com.simple.common.base.key.Const.Sql.ALIAS;
import static com.simple.common.base.key.Const.Sql.COMMA;
import static com.simple.common.base.key.Const.Sql.COUNT1;
import static com.simple.common.base.key.Const.Sql.DELETE;
import static com.simple.common.base.key.Const.Sql.EQQ;
import static com.simple.common.base.key.Const.Sql.FROM;
import static com.simple.common.base.key.Const.Sql.IN;
import static com.simple.common.base.key.Const.Sql.INSERT;
import static com.simple.common.base.key.Const.Sql.INTO;
import static com.simple.common.base.key.Const.Sql.NULL;
import static com.simple.common.base.key.Const.Sql.QUOT;
import static com.simple.common.base.key.Const.Sql.REPLACE;
import static com.simple.common.base.key.Const.Sql.SELECT;
import static com.simple.common.base.key.Const.Sql.SEMICOLON_WRAP;
import static com.simple.common.base.key.Const.Sql.T;
import static com.simple.common.base.key.Const.Sql.T_SET;
import static com.simple.common.base.key.Const.Sql.UPDATE;
import static com.simple.common.base.key.Const.Sql.WHERE;
import static com.simple.common.base.key.Const.Sql.WHERE_T;
import static com.simple.common.base.key.Const.Sql.WRAP;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.simple.common.base.BaseCondition;

/**
 * @author 高振中
 * @summary 【SQL处理工具】
 * @date 2024-05-10 21:45:31
 **/
public final class Sql {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * SQL构建器
     */
    public static class Builder {
        private final StringBuilder sql = new StringBuilder();

        public String sql() {
            return sql.toString();
        }

        public Builder select() {
            sql.append(SELECT);
            return this;
        }

        public Builder count() {
            sql.append(COUNT1);
            return this;
        }

        public Builder as() {
            sql.append(ALIAS);
            return this;
        }

        public Builder delete() {
            sql.append(DELETE);
            return this;
        }

        public Builder insert() {
            sql.append(INSERT);
            return this;
        }

        public Builder replace() {
            sql.append(REPLACE);
            return this;
        }

        public Builder into() {
            sql.append(INTO);
            return this;
        }

        public Builder update() {
            sql.append(UPDATE);
            return this;
        }

        public Builder from() {
            sql.append(FROM);
            return this;
        }

        public Builder id(final String idName) {
            sql.append(idName).append(EQQ);
            return this;
        }

        public Builder idIn(final String idName, Object... ids) {
            sql.append(idName).append(IN).append(in(ids));
            return this;
        }

        public Builder where() {
            sql.append(WHERE);
            return this;
        }

        public Builder forUpdate() {
            sql.append(BLANK).append("FOR UPDATE");
            return this;
        }

        public <C extends BaseCondition> Builder where(final C c) {
            sql.append(c.where());
            return this;
        }

        public Builder whereT() {
            sql.append(WHERE_T);
            return this;
        }

        public Builder set(final String fields) {
            sql.append(T_SET).append(fields);
            return this;
        }

        public Builder values(final String values) {
            sql.append(values);
            return this;
        }

        public Builder table(final String table) {
            sql.append(table);
            return this;
        }

        public Builder fields(final List<Field> fields) {
            sql.append(String.join(COMMA, fields.stream().map(i -> T + StringUtil.toLine(i.getName())).toList()));
            return this;
        }
    }

    /**
     * 把组数拼接成(?,?,?)的形式条件
     */
    public static String in(final Object... ids) {
        return String.format("(%s)", String.join(",", Collections.nCopies(ids.length, "?")));
    }

    /**
     * 填充参数到SQL语句(占位符 ? 方式)
     * 优化：使用遍历替代正则，解决特殊字符转义问题，性能提升
     */
    public static String fill(String sql, final Object... objects) {
        if (objects == null || objects.length == 0) return sql;

        StringBuilder sb = new StringBuilder(sql.length() + 50);
        int paramIndex = 0;
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '?') {
                if (paramIndex < objects.length) {
                    sb.append(format(objects[paramIndex++]));
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 填充参数到SQL语句(命名参数 :name 方式)
     * 优化：一次遍历完成所有参数替换，避免多次正则扫描
     */
    @SneakyThrows
    public static <T> String fill(String sql, final T t, final Map<String, Field> fieldMap) {
        StringBuilder sb = new StringBuilder(sql.length() + 100);
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == ':' && i + 1 < sql.length() && Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
                int j = i + 1;
                while (j < sql.length() && Character.isJavaIdentifierPart(sql.charAt(j))) {
                    j++;
                }
                String paramName = sql.substring(i + 1, j);
                Field field = fieldMap.get(paramName); // 直接获取，无需遍历
                if (field != null) {
                    sb.append(format(field.get(t)));
                } else {
                    sb.append(c).append(paramName);
                }
                i = j - 1;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 填充参数到批操作SQL语句(命名参数 :name 方式)
     * 优化：外层循环列表，内层使用遍历替换，解决批量打印的性能与转义问题
     */
    @SneakyThrows
    public static <T> String fill(final String sql, final List<T> list, final Map<String, Field> fieldMap) {
        StringBuilder sb = new StringBuilder(WRAP);
        for (T t : list) {
            StringBuilder rowSb = new StringBuilder(sql.length() + 50);
            for (int i = 0; i < sql.length(); i++) {
                char c = sql.charAt(i);
                if (c == ':' && i + 1 < sql.length() && Character.isJavaIdentifierStart(sql.charAt(i + 1))) {
                    int j = i + 1;
                    while (j < sql.length() && Character.isJavaIdentifierPart(sql.charAt(j))) {
                        j++;
                    }
                    String paramName = sql.substring(i + 1, j);
                    Field field = fieldMap.get(paramName);
                    if (field != null) {
                        rowSb.append(format(field.get(t)));
                    } else {
                        rowSb.append(c).append(paramName);
                    }
                    i = j - 1;
                } else {
                    rowSb.append(c);
                }
            }
            sb.append(rowSb).append(SEMICOLON_WRAP);
        }
        return sb.toString();
    }


    /**
     * 按类型格式化参数值
     */
    public static String format(Object value) {
        if (Objects.isNull(value)) {
            return NULL;
        }
        return switch (value) {
            case LocalDateTime v -> QUOT + DateUtil.formatAll(v) + QUOT;
            case LocalDate v -> QUOT + DateUtil.formatYmd(v) + QUOT;
            case LocalTime v -> QUOT + DateUtil.formatHms(v) + QUOT;
            case String v -> QUOT + v + QUOT;
            default -> value.toString();
        };
    }

    /**
     * 去除SQL中多余字符
     */
    public static String wash(String sql) {
        return sql.replaceAll("\n", " ").replaceAll("\\s+", " ").replaceAll(",\\s+|\\s+,", ",");
    }


    /**
     * 组装count-sql:思路:找第一个不在括号内的FROM生成COUNT SQL
     */
    public static String countSql(String sql) {
        int bracketCount = 0;
        for (int i = 0; i < sql.length() - 3; i++) {
            char c = sql.charAt(i);
            switch (c) {
                case '(' -> bracketCount++;
                case ')' -> bracketCount = Math.max(0, bracketCount - 1);
                default -> {
                    if (bracketCount == 0 && (c == 'f' || c == 'F') && (sql.regionMatches(true, i, "FROM ", 0, 5) || sql.regionMatches(true, i, "FROM(", 0, 5))) {
                        return "SELECT COUNT(1) " + sql.substring(i);
                    }
                }
            }
        }
        throw new IllegalArgumentException("SQL中未找到合法的主FROM关键字");
    }
}
