package com.damon4u.demo.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-03-07 17:12
 */
public class MultipleRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = DataSourceContextHolder.getReadOrWrite();
        if (StringUtils.isBlank(typeKey)) {
            logger.error(">>>>>>>>>>>> default use read database");
            return DataSourceType.READ.getType();
        }
        if (typeKey.equals(DataSourceType.WRITE.getType())) {
            logger.info(">>>>>>>>>>>> use write datasource");
            return DataSourceType.WRITE.getType();
        }
        logger.info(">>>>>>>>>>>> use read datasource");
        return DataSourceType.READ.getType();
    }
}
