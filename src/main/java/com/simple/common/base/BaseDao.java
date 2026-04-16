package com.simple.common.base;

import static com.simple.common.base.key.Const.BYTE_0;
import static com.simple.common.base.key.Const.INT_0;
import static com.simple.common.base.key.Const.IdType.CUSTOM;
import static com.simple.common.base.key.Const.IdType.SNOW;
import static com.simple.common.base.key.Const.IdType.UUID;
import static com.simple.common.base.key.Const.Sql.COLON;
import static com.simple.common.base.key.Const.Sql.COMMA;
import static com.simple.common.base.key.Const.Sql.CREATE_BY;
import static com.simple.common.base.key.Const.Sql.CREATE_TIME;
import static com.simple.common.base.key.Const.Sql.INSERT_INTO;
import static com.simple.common.base.key.Const.Sql.LEFT_BRACKET;
import static com.simple.common.base.key.Const.Sql.REPLACE_INTO;
import static com.simple.common.base.key.Const.Sql.R_BRACKET;
import static com.simple.common.base.key.Const.Sql.R_VALUES;
import static com.simple.common.base.key.Const.Sql.UPDATE_BY;
import static com.simple.common.base.key.Const.Sql.UPDATE_TIME;
import static com.simple.common.base.utils.FieldUtil.autoId;
import static com.simple.common.base.utils.FieldUtil.byCondition;
import static com.simple.common.base.utils.FieldUtil.byConditionWithNull;
import static com.simple.common.base.utils.FieldUtil.byObject;
import static com.simple.common.base.utils.FieldUtil.byObjectWithNull;
import static com.simple.common.base.utils.FieldUtil.snowId;
import static com.simple.common.base.utils.FieldUtil.userId;
import static com.simple.common.base.utils.ReflectUtil.getValue;
import static com.simple.common.base.utils.ReflectUtil.hasDr;
import static com.simple.common.base.utils.ReflectUtil.idName;
import static com.simple.common.base.utils.ReflectUtil.idType;
import static com.simple.common.base.utils.ReflectUtil.tableName;
import static com.simple.common.base.utils.StringUtil.toLine;
import static com.simple.common.base.utils.StringUtil.toLowerCamel;
import static com.simple.common.base.utils.StringUtil.uuid;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.GenericTypeResolver;

import com.simple.common.base.key.SnowflakeId;
import com.simple.common.base.utils.FieldUtil;
import com.simple.common.base.utils.ReflectUtil;
import com.simple.common.base.utils.Sql;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;

/**
 * @author 高振中
 * @summary 【DAO基类】单表操作:允许在Service中直接使用的方法
 * @date 2024-05-10 21:45:31
 **/
@Scope("prototype")
public class BaseDao<T> extends BaseSql {
	private final Class<T> clazz; // 实体类类型
	private final List<Field> fields;// 实体类对应的字段列表
	private final Map<String, Field> fieldMap;// 实体类对应的字段Map
	private final String table;// 实体类对应的表名
	private final String idName;// 表主键名
	private final String idType;// 表主键类型
	protected Boolean checkRef = false;// [检查表的引用关系]的开关
	@Value("${simple-dao.show-sql:true}")
	private boolean showSql;// 默认是否显带参SQL
	@Value("${simple-dao.logic-delete.field:dr}")
	private String logicDeleteField;

	/**
	 * @方法说明 系统启动时通过反射去解析表结构
	 */
	@SuppressWarnings("unchecked")
	protected BaseDao() {
		this.clazz = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseDao.class);
		this.fields = ReflectUtil.fields(clazz);
		this.table = tableName(clazz);
		this.idName = idName(fields);
		this.idType = idType(fields);
		this.fieldMap = fields.stream().collect(Collectors.toMap(Field::getName, Function.identity()));
	}
	
	private Boolean hasDr;// 是否包括删除标记
	@PostConstruct
	private void init() {
		hasDr = hasDr(fields, logicDeleteField);
	}

	/**
	 * @param t 记录对象
	 * @param c 查询条件
	 * @方法说明 按条件修改记录(null不参与update)
	 */
	public <C extends BaseCondition> int update(final T t, final C c) {
		FieldUtil.Update update = byCondition(fields, t, c);
		String sql = Sql.builder().update().table(table).set(update.sql()).sql();
		return update(sql, update.array);
	}

	/**
	 * @param t 记录对象
	 * @param c 查询条件
	 * @方法说明 按条件修改记录(null参与update)
	 */
	public <C extends BaseCondition> int updateNull(final T t, final C c) {
		FieldUtil.Update update = byConditionWithNull(fields, t, c);
		String sql = Sql.builder().update().table(table).set(update.sql()).sql();
		return update(sql, update.array);
	}

	/**
	 * @param t 记录对象
	 * @方法说明 按主键修改记录(null不参与update)
	 */
	public int update(final T t) {
		FieldUtil.Update update = byObject(fields, t);
		String sql = Sql.builder().update().table(table).set(update.sql()).sql();
		return update(sql, update.array);
	}

	/**
	 * @param t 记录对象
	 * @方法说明 按主键修改记录(null参与update)
	 */
	public int updateNull(final T t) {
		FieldUtil.Update update = byObjectWithNull(fields, t);
		String sql = Sql.builder().update().table(table).set(update.sql()).sql();
		return update(sql, update.array);
	}

	/**
	 * @param t 记录对象
	 * @方法说明 保存记录
	 */
	public T save(final T t) {
		return save(showSql, t);
	}

	public T save(boolean show, final T t) {
		if (SNOW.equals(idType) || UUID.equals(idType)) {
			Object id = SNOW.equals(idType) ? SnowflakeId.nextId() : uuid();
			FieldUtil.Insert<T> insert = snowId(fields, t, id, idName, logicDeleteField);
			String sql = Sql.builder().insert().into().table(table).values(insert.sql()).sql();
			save(show, insert.t, sql);
			return insert.t;
		} else if (CUSTOM.equals(idType)) {
			FieldUtil.Insert<T> insert = autoId(fields, t, logicDeleteField);
			String sql = Sql.builder().insert().into().table(table).values(insert.sql()).sql();
			save(insert.t, sql);
			return insert.t;
		} else {
			FieldUtil.Insert<T> insert = autoId(fields, t, logicDeleteField);
			String sql = Sql.builder().insert().into().table(table).values(insert.sql()).sql();
			return saveKey(show, insert.t, sql, idName);
		}
	}

	/**
	 * @param t 记录对象
	 * @方法说明 保存记录
	 */
	public T replace(final T t) {
		if (SNOW.equals(idType) || UUID.equals(idType)) {
			if (!FieldUtil.idIsNull(fieldMap, t, idName)) {
				FieldUtil.Update update = byObject(fields, t);
				String sql = Sql.builder().update().table(table).set(update.sql()).sql();
				update(sql, update.array);
				return t;
			} else {
				Object id = SNOW.equals(idType) ? SnowflakeId.nextId() : uuid();
				FieldUtil.Insert<T> insert = snowId(fields, t, id, idName, logicDeleteField);
				String sql = Sql.builder().insert().into().table(table).values(insert.sql()).sql();
				save(insert.t, sql);
				return insert.t;
			}
		} else {
			FieldUtil.Insert<T> insert = autoId(fields, t, logicDeleteField);
			String sql = Sql.builder().replace().into().table(table).values(insert.sql()).sql();
			return saveKey(insert.t, sql, idName);
		}
	}

	/**
	 * @param c 查询条件
	 * @方法说明 查询记录个数是否大于0
	 */
	public <C extends BaseCondition> boolean exists(final C c) {
		return count(c) > INT_0;
	}

	/**
	 * @param c 查询条件
	 * @方法说明 查询记录个数
	 */
	public <C extends BaseCondition> int count(final C c) {
		String sql = Sql.builder().select().count().from().table(table).as().sql();
		return count(sql, c);
	}

	/**
	 * @param c 查询条件
	 * @方法说明 查询分页列表
	 */
	public <C extends BaseCondition> Page<T> page(final C c) {
		String sql = Sql.builder().select().fields(fields).from().table(table).as().sql();
		return page(sql, c, clazz);
	}

	/**
	 * @param id 主键
	 * @方法说明 按主键查询单个记录
	 */
	public T findById(final Object id) {
		return findById(id, false);
	}

	/**
	 * @param id   主键
	 * @param lock 是否加行锁
	 * @方法说明 按主键查询单个记录
	 */
	public T findById(final Object id, final Boolean lock) {
		String sql;
		if (lock) {
			sql = Sql.builder().select().fields(fields).from().table(table).as().whereT().id(idName).forUpdate().sql();
		} else {
			sql = Sql.builder().select().fields(fields).from().table(table).as().whereT().id(idName).sql();
		}
		return list(sql, clazz, id).stream().findFirst().orElse(null);
	}

	/**
	 * @param c 查询条件对象
	 * @方法说明 按条件查询单个记录, 行数不唯一时抛异常
	 */
	public <C extends BaseCondition> T row(final C c) {
		String sql = Sql.builder().select().fields(fields).from().table(table).as().where(c).sql();
		return row(sql, clazz, c.array());
	}

	/**
	 * @param c 条件
	 * @return T
	 * @方法说明 获取一行记录，若有多行数据则抛出异常
	 */
	public <C extends BaseCondition> T findOne(final C c) {
		return findOne(showSql, c);
	}

	public <C extends BaseCondition> T findOne(boolean show, final C c) {
		List<T> list = list(show, c);
		if (list.size() > 1) {
			throw new RuntimeException("rows than more 1");
		}
		return list.stream().findFirst().orElse(null);
	}

	/**
	 * @param c 条件
	 * @方法说明 按条件查询列表
	 */
	public <C extends BaseCondition> List<T> list(final C c) {
		return list(showSql, c);
	}

	public <C extends BaseCondition> List<T> list(boolean show, final C c) {
		String sql = Sql.builder().select().fields(fields).from().table(table).as().sql();
		return list(show, sql, c, clazz);
	}

	/**
	 * @param list 记录列表
	 * @方法说明 批量替换记录
	 */
	public List<T> replaceBatch(final List<T> list) {
		return saveBatch(showSql, list, REPLACE_INTO);
	}

	public List<T> replaceBatch(boolean show, final List<T> list) {
		return saveBatch(show, list, REPLACE_INTO);
	}

	/**
	 * @param list 记录列表
	 * @方法说明 批量保存记录
	 */
	public List<T> saveBatch(final List<T> list) {
		return saveBatch(showSql, list, INSERT_INTO);
	}

	public List<T> saveBatch(boolean show, final List<T> list) {
		return saveBatch(show, list, INSERT_INTO);
	}

	/**
	 * @param list 记录列表
	 * @方法说明 批量保存记录
	 */
	@SneakyThrows
	public List<T> saveBatch(boolean show, final List<T> list, final String type) {
		LocalDateTime dateTime = LocalDateTime.now();
		String sql = type + table + LEFT_BRACKET + String.join(COMMA, fields.stream().map(i -> toLine(i.getName())).toList()) + R_VALUES + String.join(COMMA, fields.stream().map(i -> COLON + i.getName()).toList()) + R_BRACKET;
		for (T t : list) {
			boolean idIsNull = Objects.isNull(getValue(fieldMap, t, toLowerCamel(idName)));
			for (Field f : fields) {
				String name = f.getName();
				if (idIsNull) {
					if (toLine(name).equals(idName) && UUID.equals(idType)) {
						f.set(t, uuid());
					}
					if (toLine(name).equals(idName) && SNOW.equals(idType)) {
						f.set(t, SnowflakeId.nextId());
					}
					if (name.equals(CREATE_TIME)) {
						f.set(t, dateTime);
					}
					if (name.equals(CREATE_BY)) {
						f.set(t, userId());
					}
					if (name.equals(logicDeleteField)) {
						f.set(t, BYTE_0);
					}
				} else {
					if (name.equals(UPDATE_TIME)) {
						f.set(t, dateTime);
					}
					if (name.equals(UPDATE_BY)) {
						f.set(t, userId());
					}
				}
			}
		}
		batchOperate(show, list, sql);
		return list;
	}

	/**
	 * @方法说明 取实体中的字段
	 */
	@Override
	Map<String, Field> fieldMap() {
		return fieldMap;
	}

	/**
	 * @param ids 主键数组
	 * @方法说明 按主键数组删除记录
	 */
	public int delete(final Object... ids) {
		return delete(showSql, ids);
	}

	public int delete(boolean show, final Object... ids) {
		String sql;
		if (hasDr) {
			sql = Sql.builder().update().table(table).set(logicDeleteField+" = 1").where().idIn(idName, ids).sql();
		} else {
			sql = Sql.builder().delete().from().table(table).where().idIn(idName, ids).sql();
		}
		return update(show, sql, ids);
	}

	/**
	 * @param c 查询条件
	 * @方法说明 按条件删除记录
	 */
	public <C extends BaseCondition> int delete(final C c) {
		return delete(showSql, c);
	}

	public <C extends BaseCondition> int delete(boolean show, final C c) {
		String sql;
		if (hasDr) {
			sql = Sql.builder().update().table(table).set(logicDeleteField+" = 1").where(c).sql();
		} else {
			sql = Sql.builder().delete().from().table(table).as().where(c).sql();
		}
		return update(show, sql, c.array());
	}
}
