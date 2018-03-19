package com.damon4u.demo.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * 一个线程的数据源持有器，AOP会动态改变线程使用的数据源类型
 *
 * @author damon4u
 * @version 2018-03-07 17:14
 */
public class DataSourceContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);

    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void setDataSourceType(String dataSourceType) {
        context.set(dataSourceType);
        logger.info(">>>>>>>>>>>> switch to {} datasource", dataSourceType);
    }

    public static String getReadOrWrite() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
