package com.damon4u.demo.service.impl;

import com.damon4u.demo.domain.User;
import com.damon4u.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-03-07 21:51
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class UserServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    @Resource
    private UserService userService;

    @Test
    public void save() {
        User user = new User("alice", "save");
        userService.save(user);
    }

    /**
     * 由于默认走的从库，如果试图对从库进行写操作，那么会抛权限异常
     */
    @Test(expected = BadSqlGrammarException.class)
    public void saveUseDefaultDataSource() {
        User user = new User("alice", "save to slave error");
        userService.saveUseDefaultDataSource(user);
    }

    /**
     * 普通的读操作，走默认的从库
     */
    @Test
    public void getUserById() {
        User user = userService.getUserById(1L);
        logger.info("user={}", user);
    }


    @Test(expected = BadSqlGrammarException.class)
    public void readAndWrite() {
        User user = new User("bob", "readAndWrite");
        userService.readAndWrite(user);
    }
}