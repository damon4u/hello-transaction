package com.damon4u.demo.annotation;

import com.damon4u.demo.datasource.DataSourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 *  数据源类型注解
 *  注：只能标注方法，不能标注类
 *
 * @author damon4u
 * @version 2018-03-07 17:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DataSource {

    /**
     * 这里默认使用读数据源，如果忘记指定数据源，而尝试写操作，那么数据库会报错
     *
     * @return 方法使用数据源类型
     */
    DataSourceType value() default DataSourceType.READ;
}
