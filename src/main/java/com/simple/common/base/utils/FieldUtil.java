package com.simple.common.base.utils;

import static com.simple.common.base.key.Const.BYTE_0;
import static com.simple.common.base.key.Const.LONG_0;
import static com.simple.common.base.key.Const.Sql.COLON;
import static com.simple.common.base.key.Const.Sql.COMMA;
import static com.simple.common.base.key.Const.Sql.CREATE_BY;
import static com.simple.common.base.key.Const.Sql.CREATE_TIME;
import static com.simple.common.base.key.Const.Sql.DR;
import static com.simple.common.base.key.Const.Sql.EQQ;
import static com.simple.common.base.key.Const.Sql.LEFT_BRACKET;
import static com.simple.common.base.key.Const.Sql.NO_UPDATE;
import static com.simple.common.base.key.Const.Sql.R_BRACKET;
import static com.simple.common.base.key.Const.Sql.T;
import static com.simple.common.base.key.Const.Sql.UPDATE_BY;
import static com.simple.common.base.key.Const.Sql.UPDATE_TIME;
import static com.simple.common.base.key.Const.Sql.VALUES;
import static com.simple.common.base.key.Const.Sql.WHERE;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simple.common.base.BaseCondition;
import com.simple.common.base.annotation.Id;

import lombok.SneakyThrows;

/**
 * @author 高振中
 * @summary 【INSERT,UPDATE:SQL与参数处理工具】
 * @date 2026-02-15 22:45:31
 **/
public final class FieldUtil {

    // ==================== 私有反射工具 ====================
    @SneakyThrows
    private static Object getFieldValue(Field field, Object target) {
        return field.get(target);
    }

    @SneakyThrows
    private static void setFieldValue(Field field, Object target, Object value) {
        field.set(target, value);
    }

    // ==================== 会话与用户 ====================
    public static Long userId() {
        return  1000L;
    }
    // ==================== 内部数据载体 ====================
    private record FieldUpdate(String column, Object value) {
    }

    public static class Update {
        private final StringBuilder sql = new StringBuilder();
        public Object[] array;

        public String sql() {
            return sql.toString();
        }
    }

    public static class Insert<T> {
        private final StringBuilder sql = new StringBuilder();
        public T t;

        public String sql() {
            return sql.toString();
        }
    }

    // ==================== 主键判断 ====================
    public static <T> boolean idIsNull(Map<String, Field> fieldMap, T t, String idName) {
        Field field = fieldMap.get(StringUtil.toLowerCamel(idName));
        return Objects.isNull(getFieldValue(field, t));
    }

    // ==================== 插入构造 ====================
    public static <T> Insert<T> snowId(List<Field> fields, T t, Object id, String idName) {
        Insert<T> insert = new Insert<>();
        // 先填充空字段
        fields.stream()
                .filter(f -> Objects.isNull(getFieldValue(f, t)))
                .forEach(f -> {
                    String name = f.getName();
                    if (name.equals(CREATE_TIME)) {
                        setFieldValue(f, t, LocalDateTime.now());
                    } else if (name.equals(CREATE_BY)) {
                        setFieldValue(f, t, userId());
                    } else if (name.equals(DR)) {
                        setFieldValue(f, t, BYTE_0);
                    } else if (StringUtil.toLine(name).equals(idName)) {
                        setFieldValue(f, t, id);
                    }
                });

        // 过滤非空字段，构建SQL
        List<Field> notNullFields = fields.stream()
                .filter(f -> Objects.nonNull(getFieldValue(f, t)))
                .toList();

        insert.sql.append(LEFT_BRACKET)
                .append(notNullFields.stream().map(f -> StringUtil.toLine(f.getName())).collect(Collectors.joining(COMMA)))
                .append(R_BRACKET)
                .append(VALUES)
                .append(notNullFields.stream().map(f -> COLON + f.getName()).collect(Collectors.joining(COMMA)))
                .append(R_BRACKET);
        insert.t = t;
        return insert;
    }

    public static <T> Insert<T> autoId(List<Field> fields, T t) {
        Insert<T> insert = new Insert<>();

        // 自动填充审计字段
        fields.stream()
                .filter(f -> Objects.isNull(getFieldValue(f, t)))
                .forEach(f -> {
                    String name = f.getName();
                    if (name.equals(CREATE_TIME)) {
                        setFieldValue(f, t, LocalDateTime.now());
                    } else if (name.equals(CREATE_BY) && userId() > LONG_0) {
                        setFieldValue(f, t, userId());
                    } else if (name.equals(DR)) {
                        setFieldValue(f, t, BYTE_0);
                    }
                });

        List<String> columnNames = fields.stream().map(f -> StringUtil.toLine(f.getName())).toList();
        List<String> paramNames = fields.stream().map(f -> COLON + f.getName()).toList();

        insert.sql.append(LEFT_BRACKET)
                .append(String.join(COMMA, columnNames))
                .append(R_BRACKET)
                .append(VALUES)
                .append(String.join(COMMA, paramNames))
                .append(R_BRACKET);
        insert.t = t;
        return insert;
    }

    // ==================== 更新构造（条件更新） ====================
    private static <T, C extends BaseCondition> Update buildUpdate(List<Field> fields, T t, C c, boolean includeNull) {
        Update update = new Update();
        // 收集需要更新的字段及其值
        List<FieldUpdate> updates = fields.stream()
                .flatMap(field -> {
                    String name = field.getName();
                    Object value = getFieldValue(field, t);
                    if (name.equals(UPDATE_TIME)) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), LocalDateTime.now()));
                    } else if (name.equals(UPDATE_BY) && userId() > LONG_0) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), userId()));
                    } else if (includeNull || (!NO_UPDATE.contains(name) && value != null)) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), value));
                    } else {
                        return Stream.empty();
                    }
                }).toList();
        // 构建 SET 子句
        String setClause = updates.stream().map(fu -> fu.column() + " = ?").collect(Collectors.joining(COMMA));
        // 收集参数
        List<Object> param = updates.stream().map(FieldUpdate::value).collect(Collectors.toCollection(ArrayList::new));
        // 组装最终 SQL
        update.sql.append(setClause);
        update.sql.append(c.where());
        param.addAll(c.getParamList());
        update.array = param.toArray();
        return update;
    }

    public static <T, C extends BaseCondition> Update byCondition(List<Field> fields, T t, C c) {
        return buildUpdate(fields, t, c, false);
    }

    public static <T, C extends BaseCondition> Update byConditionWithNull(List<Field> fields, T t, C c) {
        return buildUpdate(fields, t, c, true);
    }

    // ==================== 更新构造（主键更新） ====================
    private static <T> Update buildUpdateByObject(List<Field> allFields, T t, boolean includeNull) {
        // 分离主键和非主键
        List<Field> idFields = allFields.stream().filter(f -> f.getDeclaredAnnotationsByType(Id.class).length > 0).toList();
        List<Field> noIdFields = allFields.stream().filter(f -> f.getDeclaredAnnotationsByType(Id.class).length == 0).toList();
        Update update = new Update();
        // 收集更新字段
        List<FieldUpdate> updates = noIdFields.stream()
                .flatMap(field -> {
                    String name = field.getName();
                    Object value = getFieldValue(field, t);
                    if (name.equals(UPDATE_TIME)) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), LocalDateTime.now()));
                    } else if (name.equals(UPDATE_BY)) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), userId()));
                    } else if (includeNull || (value != null && !NO_UPDATE.contains(name))) {
                        return Stream.of(new FieldUpdate(T + StringUtil.toLine(name), value));
                    } else {
                        return Stream.empty();
                    }
                }).toList();

        // 构建 SET 子句
        String setClause = updates.stream().map(fu -> fu.column() + " = ?").collect(Collectors.joining(COMMA));
        List<Object> param = updates.stream().map(FieldUpdate::value).collect(Collectors.toCollection(ArrayList::new));
        update.sql.append(setClause);
        Field idField = idFields.getFirst();
        update.sql.append(WHERE).append(StringUtil.toLine(idField.getName())).append(EQQ);
        Object idVal = getFieldValue(idField, t);
        param.add(idVal);
        update.array = param.toArray();
        return update;
    }

    public static <T> Update byObject(List<Field> fields, T t) {
        return buildUpdateByObject(fields, t, false);
    }

    public static <T> Update byObjectWithNull(List<Field> fields, T t) {
        return buildUpdateByObject(fields, t, true);
    }
}