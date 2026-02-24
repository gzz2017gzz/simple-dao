package com.hq.common.base;

import static com.hq.common.base.key.Const.INT_1;
import static com.hq.common.base.utils.Sql.wash;
import static org.springframework.util.Assert.isTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hq.common.base.utils.ReflectUtil;
import com.hq.common.base.utils.Sql;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 高振中
 * @summary 【JDBC基类】用于自定义SQL语:像联表操作,自定义的INSERT/UPDATE,只能在DAO中使用的方法
 * @date 2024-05-10 21:45:31
 **/
@Slf4j
public abstract class BaseJdbc {

    @Autowired
    protected JdbcTemplate jdbc;// 注入jdbc模板(占位符)
    @Autowired
    protected NamedParameterJdbcTemplate namedJdbc;// 注入jdbc模板(命名参数)


    /**
     * @param sql   自定义SQL语句
     * @param cond  查询条件
     * @param clazz 返回行实体类Class
     * @方法说明 分页查询
     */
    protected <T, C extends BaseCondition> Page<T> page(String sql, final C cond, final Class<T> clazz) {
        return page(true, sql, cond, clazz);
    }

    protected <T, C extends BaseCondition> Page<T> page(boolean logSql, String sql, final C cond, final Class<T> clazz) {
        sql = wash(sql);
        String countSql = Sql.countSql(sql);
        Integer count = field(logSql, countSql, Integer.class, cond);
        List<T> data = list(logSql, sql + cond.where() + cond.orders() + " LIMIT " + (cond.getPage() - INT_1) * cond.getSize() + "," + cond.getSize(), clazz, cond.array());
        return new Page<>(data, cond.getSize(), count, cond.getPage());
    }

    /**
     * @param sql   自定义SQL语句
     * @param cond  查询条件
     * @param clazz 返回行实体类Class
     * @方法说明 嵌套分页查询(兼容性好效率低)
     */
    protected <T, C extends BaseCondition> Page<T> page0(String sql, final C cond, final Class<T> clazz) {
        return page0(true, sql, cond, clazz);
    }

    protected <T, C extends BaseCondition> Page<T> page0(boolean logSql, String sql, final C cond, final Class<T> clazz) {
        sql = wash(sql);
        String countSql = "SELECT COUNT(1) FROM (" + sql + cond.where() + ") t";
        Integer count = field(logSql, countSql, Integer.class, cond.array());
        List<T> data = list(logSql, sql + cond.where() + cond.orders() + " LIMIT " + (cond.getPage() - INT_1) * cond.getSize() + "," + cond.getSize(), clazz, cond.array());
        return new Page<>(data, cond.getSize(), count, cond.getPage());
    }

    /**
     * @param list 目标对象集
     * @param sql  自定义SQL语句
     * @方法说明 批操作 命名参数方式
     */
    protected <T> int[] batchOperate(final List<T> list, final String sql) {
        return batchOperate(true, list, sql);
    }

    protected <T> int[] batchOperate(boolean logSql, final List<T> list, final String sql) {
        if (logSql) {
            log.info(Sql.fill(sql, list, fieldMap()));
        }
        return namedJdbc.batchUpdate(sql, list.stream().map(BeanPropertySqlParameterSource::new).toArray(SqlParameterSource[]::new));
    }

    abstract Map<String, Field> fieldMap();

    /**
     * @param sql  自定义SQL语句
     * @param cond 查询条件
     * @方法说明 查询记录个数
     */
    protected <C extends BaseCondition> Integer count(final String sql, final C cond) {
        return field(true, sql, Integer.class, cond);
    }

    protected <C extends BaseCondition> Integer count(boolean logSql, final String sql, final C cond) {
        return field(logSql, sql, Integer.class, cond);
    }

    /**
     * @param sql 自定义SQL语句
     * @param obj 可变参数
     * @方法说明 查询记录个数
     */
    protected Integer count(final String sql, final Object... obj) {
        return field(true, sql, Integer.class, obj);
    }

    protected Integer count(boolean logSql, final String sql, final Object... obj) {
        return field(logSql, sql, Integer.class, obj);
    }

    /**
     * @param sql 自定义SQL语句
     * @param obj 可变参数
     * @方法说明 执行INSERT/UPDATE/DELETE操作
     */
    protected Integer update(String sql, final Object... obj) {
        return update(true, sql, obj);
    }

    private static final Pattern DELETE_PATTERN = Pattern.compile("DELETE", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHERE_PATTERN = Pattern.compile(" WHERE ", Pattern.CASE_INSENSITIVE);

    protected Integer update(boolean logSql, String sql, final Object... obj) {
        sql = wash(sql);
        // 垃圾阿里数据库:delete不支持别名,要把DELETE语句中的别名干掉
        if (DELETE_PATTERN.matcher(sql).find()) {
            sql = sql.replace(" t ", " ").replaceAll("t\\.", "");
        }
        if (logSql) {
            log.info(Sql.fill(sql, obj));
        }
        isTrue(WHERE_PATTERN.matcher(sql).find(), String.format("危险操作被拦截: SQL [%s] 没有WHERE条件", sql));
        return jdbc.update(sql, obj);
    }

    /**
     * @param t   保存的目标对象
     * @param sql 自定义SQL语句
     * @方法说明 执行INSERT操作 命名参数方式
     */
    protected <T> int save(final T t, final String sql) {
        return save(true, t, sql);
    }

    protected <T> int save(boolean logSql, final T t, final String sql) {
        if (logSql) {
            log.info(Sql.fill(sql, t, fieldMap()));
        }
        return namedJdbc.update(sql, new BeanPropertySqlParameterSource(t));
    }

    /**
     * @param t      保存的目标对象
     * @param sql    自定义SQL语句
     * @param idName 主键字段名称
     * @方法说明 执行INSERT操作返回数据库自增主键 命名参数方式
     */
    protected <T> T saveKey(final T t, final String sql, final String idName) {
        return saveKey(true, t, sql, idName);
    }

    protected <T> T saveKey(boolean logSql, final T t, final String sql, final String idName) {
        if (logSql) {
            log.info(Sql.fill(sql, t, fieldMap()));
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, new BeanPropertySqlParameterSource(t), keyHolder, new String[]{idName});
        Long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        ReflectUtil.setValue(fieldMap(), t, idName, key);
        return t;
    }

    /**
     * @param sql   自定义SQL语句
     * @param cond  查询条件
     * @param clazz 返回行实体类Class
     * @方法说明 查询数据列表
     */
    protected <T, C extends BaseCondition> List<T> list(String sql, final C cond, final Class<T> clazz) {
        return list(true, sql, cond, clazz);
    }

    protected <T, C extends BaseCondition> List<T> list(boolean logSql, String sql, final C cond, final Class<T> clazz) {
        sql += cond.where() + cond.orders();
        return list(logSql, sql, clazz, cond.array());
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回行实体类Class
     * @param obj   可变参数
     * @方法说明 查询数据列表
     */
    protected <T> List<T> list(String sql, final Class<T> clazz, final Object... obj) {
        return list(true, sql, clazz, obj);
    }

    protected <T> List<T> list(boolean logSql, String sql, final Class<T> clazz, final Object... obj) {
        sql = wash(sql);
        if (logSql) {
            log.info(Sql.fill(sql, obj));
        }
        return jdbc.query(sql, new BeanPropertyRowMapper<>(clazz), obj);
    }

    /**
     * @param sql   自定义SQL语句
     * @param cond  查询条件
     * @param clazz 返回列数据类型(Integer,Long,String...)
     * @方法说明 查询单列多行数据
     */
    protected <T, C extends BaseCondition> List<T> columns(String sql, final C cond, final Class<T> clazz) {
        return columns(true, sql, clazz, cond.array());
    }

    protected <T, C extends BaseCondition> List<T> columns(boolean logSql, String sql, final C cond, final Class<T> clazz) {
        sql += cond.where() + cond.orders();
        return columns(logSql, sql, clazz, cond.array());
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回列数据类型(Integer,Long,String...)
     * @param obj   可变参数
     * @方法说明 查询单列多行数据
     */
    protected <T> List<T> columns(final String sql, final Class<T> clazz, final Object... obj) {
        return columns(true, sql, clazz, obj);
    }

    protected <T> List<T> columns(boolean logSql, final String sql, final Class<T> clazz, final Object... obj) {
        if (logSql) {
            log.info(Sql.fill(sql, obj));
        }
        return jdbc.query(sql, new SingleColumnRowMapper<>(clazz), obj);
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回行实体类Class
     * @param cond  查询条件
     * @方法说明 查询[单行多列]记录:如使用聚合函数时,行数不唯一时抛异常
     */
    protected <T, C extends BaseCondition> T row(String sql, final Class<T> clazz, final C cond) {
        return row(true, sql, clazz, cond);
    }

    protected <T, C extends BaseCondition> T row(boolean logSql, String sql, final Class<T> clazz, final C cond) {
        sql += cond.where();
        return row(logSql, sql, clazz, cond.array());
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回行实体类Class
     * @param obj   可变参数
     * @方法说明 查询[单行多列]记录:如使用聚合函数时,行数不唯一时抛异常
     */
    protected <T> T row(String sql, final Class<T> clazz, final Object... obj) {
        return row(true, sql, clazz, obj);
    }

    protected <T> T row(boolean logSql, String sql, final Class<T> clazz, final Object... obj) {
        if (logSql) {
            log.info(Sql.fill(sql, obj));
        }
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), obj);
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回行实体类Class
     * @param cond  查询条件
     * @方法说明 查询[单行单列]记录:如使用聚合函数时,行数不唯一时抛异常
     */
    protected <T, C extends BaseCondition> T field(String sql, final Class<T> clazz, final C cond) {
        return field(true, sql, clazz, cond);
    }

    protected <T, C extends BaseCondition> T field(boolean logSql, String sql, final Class<T> clazz, final C cond) {
        sql += cond.where();
        return field(logSql, sql, clazz, cond.array());
    }

    /**
     * @param sql   自定义SQL语句
     * @param clazz 返回行实体类Class
     * @param obj   可变参数
     * @方法说明 查询[单行单列]记录:如使用聚合函数时,行数不唯一时抛异常
     */
    protected <T> T field(String sql, final Class<T> clazz, final Object... obj) {
        return field(true, sql, clazz, obj);
    }

    protected <T> T field(boolean logSql, String sql, final Class<T> clazz, final Object... obj) {
        if (logSql) {
            log.info(Sql.fill(sql, obj));
        }
        return jdbc.queryForObject(sql, clazz, obj);
    }
 
}
