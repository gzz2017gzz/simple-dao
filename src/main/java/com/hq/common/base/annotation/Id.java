package com.hq.common.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.hq.common.base.key.Const.IdType.SNOW;

/**
 * @author 高振中
 * @summary 【主键字段】注解
 * @date 2024-05-10 21:45:31
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    String value() default SNOW;/* 默认雪花主键 */
}
