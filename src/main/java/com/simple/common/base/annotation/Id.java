package com.simple.common.base.annotation;

import static com.simple.common.base.key.Const.IdType.SNOW;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
