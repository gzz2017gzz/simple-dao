package com.hq.common.base;

import com.hq.common.base.utils.Sql;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.hq.common.base.key.Const.BLANK;
import static com.hq.common.base.key.Const.EMPTY;
import static com.hq.common.base.key.Const.INT_0;
import static com.hq.common.base.key.Const.INT_1;
import static com.hq.common.base.key.Const.INT_10;

/**
 * @author 高振中
 * @summary 【拼加条件】基类
 * @date 2024-05-10 21:45:31
 **/
@Setter
@Getter
public abstract class BaseCondition {
    @Schema(description = "导出字段")
    private List<String> fields = new ArrayList<>();// 参数值
    @Schema(hidden = true)
    private List<Object> paramList = new ArrayList<>();// 参数值
    @Schema(hidden = true)
    private StringBuilder condition = new StringBuilder();// 条件语句
    @Schema(hidden = true)
    @Accessors(chain = true)
    private String orders = EMPTY;// 排序子句
    @Schema(hidden = true)
    private String dataAuth = EMPTY;// 权限子句
    @Schema(description = "页大小")
    private Integer size = INT_10;// 页大小(每页记录个数)
    @Schema(description = "当前页")
    private Integer page = INT_1;// 当前页

    /**
     * @param sql   SQL片段
     * @param value 字段值变量
     * @方法说明 拼加运算符(=, >, <, < >, ! =)和参数(用于联表)
     */
    protected final void add(final String sql, final Object value) {
        if (Objects.nonNull(value) && StringUtils.hasText(sql) && StringUtils.hasText(value.toString())) {
            condition.append(BLANK).append(sql);
            paramList.add(value);
        }
    }

    /**
     * @param sql    字段名表达式
     * @param values 字段值数组
     * @方法说明 拼加IN/NOT IN子句(主表/关联表)
     */
    protected final void add(final String sql, final Object[] values) {
        if (StringUtils.hasText(sql) && ArrayUtils.isNotEmpty(values)) {
            condition.append(BLANK).append(sql).append(Sql.in(values));
            paramList.addAll(Arrays.asList(values));
        }
    }

    /**
     * @param sql   字段名表达式
     * @param value 字段值变量
     * @param site  %出现的位置
     * @方法说明 拼加LIKE条件:site=1->%张三,site=2->张三%,site=3->%张三%(用于主表,非主表)
     */
    protected final void add(final String sql, final String value, final int site) {
        if (Objects.nonNull(sql) && StringUtils.hasText(value)) {
            condition.append(BLANK).append(sql);
            String escapedValue = value.replace("%", "\\%").replace("_", "\\_");
            switch (site) {
                case 1 -> paramList.add("%" + escapedValue);
                case 2 -> paramList.add(escapedValue + "%");
                case 3 -> paramList.add("%" + escapedValue + "%");
            }
        }
    }

    /**
     * @方法说明 条件参数List转数组
     */
    public final Object[] array() {
        return paramList.toArray();
    }

    /**
     * @方法说明 把第一个AND换成WHERE
     */
    public final String where() {
        return and().replaceFirst("(?i)(AND)", "WHERE");
    }

    /**
     * @方法说明 取条件字符串
     */
    public final String and() {
        condition.setLength(INT_0);/* 清除查询条件 */
        paramList.clear();
        addCondition();
        condition.append(dataAuth);
        return condition.toString();
    }

    /**
     * @方法说明 拼加条件(在具体的条件类中实现)
     */
    protected abstract void addCondition();

    /**
     * @方法说明 取排序子句
     */
    protected final String orders() {
        return StringUtils.hasText(orders) ? " ORDER BY " + orders : EMPTY;
    }

    /**
     * @param field 字段名表达式
     * @param value 字段值变量
     * @方法说明 拼加运算符(=, >, <, < >, ! =)和参数(主表)
     */
    protected final void and(final String field, final Object value) {
        add("AND t." + field + " ?", value);
    }

    /**
     * @param field 字段名表达式
     * @param value 字段值变量
     * @param site  %出现的位置 site=1->%张三,site=2->张三%,site=3->%张三%
     * @方法说明 拼加LIKE条件(主表)
     */
    protected final void and(final String field, final String value, final int site) {
        add("AND t." + field + " ?", value, site);
    }

    /**
     * @param sql   String
     * @param logic boolean
     * @方法说明 根据参数判断是否拼接条件
     */
    protected final void add(final String sql, boolean logic) {
        if (logic && Objects.nonNull(sql) && StringUtils.hasText(sql)) {
            condition.append(BLANK).append(sql);
        }
    }

    /**
     * @param sql String
     * @方法说明 根据参数判断是否拼接条件
     */
    protected final void add(final String sql) {
        add(sql, true);
    }

    /**
     * @param field 字段名表达式
     * @param ids   字段值数组
     * @方法说明 拼加IN子句(主表)
     */
    protected final void in(final String field, final Object[] ids) {
        add("AND t." + field + " IN ", ids);
    }

    /**
     * @param field 字段名表达式
     * @param ids   字段值数组
     * @方法说明 拼加NOT IN子句(主表)
     */
    protected final void notIn(final String field, final Object[] ids) {
        add("AND t." + field + " NOT IN ", ids);
    }

    /**
     * @方法说明 合并条件参数
     */
    public static Object[] mergeParams(BaseCondition first, BaseCondition... rest) {
        return Stream.concat(Stream.of(first), Arrays.stream(rest)).flatMap(cond -> cond.getParamList().stream()).toArray();
    }
}
