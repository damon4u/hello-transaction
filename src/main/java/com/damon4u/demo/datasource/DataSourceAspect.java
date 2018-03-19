package com.damon4u.demo.datasource;

import com.damon4u.demo.annotation.DataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * Description:
 * service层的拦截，数据源切换
 * 不在dao层做，实现事务
 * 需要注意，aop的order要比事务的小，保证aop先执行，将数据源设置到context中
 *
 * @author damon4u
 * @version 2017-08-22 13:05
 */
@Aspect
@Order(1)
public class DataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    @Around("execution(* com.damon4u.demo.service..*.*(..)) && @annotation(com.damon4u.demo.annotation.DataSource)")
    public Object setReadDataSourceType(ProceedingJoinPoint jp) throws Throwable {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method targetMethod = methodSignature.getMethod();
        final DataSource dataSource = targetMethod.getAnnotation(DataSource.class);
        final String originDataSource = DataSourceContextHolder.getReadOrWrite();
        DataSourceContextHolder.setDataSourceType(dataSource.value().getType());
        try {
            return jp.proceed();
        } catch (Throwable throwable) {
            logger.error("DataSourceAspect error.", throwable);
            throw throwable;
        } finally {
            DataSourceContextHolder.setDataSourceType(originDataSource);
        }
    }
}
