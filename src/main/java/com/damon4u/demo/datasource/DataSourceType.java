package com.damon4u.demo.datasource;

/**
 * Description:
 *  数据源类型枚举
 *
 * @author damon4u
 * @version 2018-03-07 17:04
 */
public enum DataSourceType {
    /**
     * 标记写数据源
     */
    WRITE("write"),

    /**
     * 标记读数据源
     */
    READ("read"),
    ;

    private String type;

    DataSourceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
