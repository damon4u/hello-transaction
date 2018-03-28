package com.damon4u.demo.service.impl;

import com.damon4u.demo.annotation.DataSource;
import com.damon4u.demo.dao.UserMapper;
import com.damon4u.demo.datasource.DataSourceType;
import com.damon4u.demo.domain.User;
import com.damon4u.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-03-07 21:45
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    /**
     * 最普通的保存操作
     *
     * 加了主库注解，保存到主库中
     * @param user  用户信息
     */
    @Override
    @DataSource(DataSourceType.WRITE)
    public void save(User user) {
        userMapper.insert(user);
    }

    /**
     * 保存用户
     * 不加注解，使用默认的数据源（从库）
     *
     * @param user  用户信息
     */
    @Override
    public void saveUseDefaultDataSource(User user) {
        userMapper.insert(user);
    }

    /**
     * 根据id查询用户
     * 默认使用从库
     * @param id
     * @return
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 先读后写
     * 外部方法没有注解
     *
     * 这个方法会报错！
     * 外部调用这个方法，没有aop，然后这个内部使用this的方式调用带有注解的方法，aop不起作用
     *
     * 这是Spring AOP自身缺陷限制的，一个类内部方法间的this调用是不会触发aop的
     * 只要这个类有一个
     * @param user
     */
    @Override
    public void readAndWrite(User user) {
        User userById = getUserById(1L);
        logger.info("userById={}", userById);
        save(user);
    }
}
