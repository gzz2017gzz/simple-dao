package com.simple.common.base.utils;

import static com.simple.common.base.key.Const.INT_0;
import static  com.simple.common.base.utils.StringUtil.toLowerCamel;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.simple.common.base.annotation.Exclude;
import com.simple.common.base.annotation.Id;
import com.simple.common.base.annotation.Table;

import lombok.SneakyThrows;

/**
 * @author 高振中
 * @summary 【反射工具】
 * @date 2024-05-10 21:45:31
 **/
public final class ReflectUtil {
    private ReflectUtil() {
    } // Cannot be constructed

    /**
     * 为对像中指定属性赋值
     *
     * @param <T>    PO类范型
     * @param fields PO类字段集
     * @param t      PO类变量
     * @param name   属性名
     * @param value  属性名
     */

    @SneakyThrows
    public static <T> void setValue(Map<String, Field> fields, T t, String name, Object value) {
        Field field = fields.get(name);
        field.set(t, value);
    }

    @SneakyThrows
    public static <T> Object getValue(Map<String, Field> fields, T t, String name) {
        Field field = fields.get(name);
        return field.get(t);
    }

    /**
     * 从实体类中过滤出所有字段
     *
     * @param clazz PO类类型
     * @return PO类字段集
     */
    public static List<Field> fields(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(i -> !i.isAnnotationPresent(Exclude.class)).peek(f -> f.setAccessible(true)).toList();
    }

    /**
     * 从实体类中过滤出表名称
     *
     * @param clazz PO类类型
     * @return 表名称
     */
    public static String tableName(final Class<?> clazz) {
        return clazz.getDeclaredAnnotationsByType(Table.class)[INT_0].value();
    }

    /**
     * 从[PO类字段集]中过滤主键名
     *
     * @param fields PO类字段集
     * @return 主键名
     */
    public static String idName(final List<Field> fields) {
        return StringUtil.toLine(fields.stream().filter(i -> i.isAnnotationPresent(Id.class)).toList().getFirst().getName());
    }

    /**
     * 从[PO类字段集]中过滤主键类型
     *
     * @param fields PO类字段集
     * @return 主键类型
     */
    public static String idType(final List<Field> fields) {
        return fields.stream().filter(i -> i.isAnnotationPresent(Id.class)).toList().getFirst().getDeclaredAnnotation(Id.class).value();
    }

    /**
     * 是否具有删除标记字段
     *
     * @param fields PO类字段集
     * @return 布尔值
     */

    public static boolean hasDr(final List<Field> fields,String fieldName) {
        return fields.stream().anyMatch(i -> i.getName().contains(toLowerCamel(fieldName)));
    }
}
