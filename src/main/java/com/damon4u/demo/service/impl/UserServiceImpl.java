package com.damon4u.demo.service.impl;

import com.damon4u.demo.annotation.DataSource;
import com.damon4u.demo.dao.UserMapper;
import com.damon4u.demo.datasource.DataSourceType;
import com.damon4u.demo.domain.User;
import com.damon4u.demo.service.UserService;
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

    @Resource
    private UserMapper userMapper;

    @Override
    @DataSource(DataSourceType.WRITE)
    public void save(User user) {
        userMapper.insert(user);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
