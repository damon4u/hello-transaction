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

import java.io.IOException;

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
    public void readAndWriteDefaultDataSource() {
        User user = new User("bob", "readAndWriteDefaultDataSource");
        userService.readAndWriteDefaultDataSource(user);
    }

    @Test
    public void readAndWriteWithSelfIoC() {
        User user = new User("bob", "readAndWriteWithSelfIoC");
        userService.readAndWriteWithSelfIoC(user);
    }

    @Test(expected = BadSqlGrammarException.class)
    public void readAndWriteWithTransaction() {
        User user = new User("catalina", "readAndWriteWithTransaction");
        userService.readAndWriteWithTransaction(user);
    }

    @Test
    public void readAndWriteWithTransactionUseMaster() {
        User user = new User("catalina", "readAndWriteWithTransactionUseMaster");
        userService.readAndWriteWithTransactionUseMaster(user);
    }

    /**
     * 带事务的写，异常回滚
     */
    @Test(expected = RuntimeException.class)
    public void saveWithTransaction() {
        User user = new User("damon", "saveWithTransaction");
        userService.saveWithTransaction(user);
    }

    /**
     * 这个方法事务不起作用，不会滚，数据插入到数据库中
     */
    @Test(expected = RuntimeException.class)
    public void saveByInnerAop() {
        User user = new User("ellen", "saveByInnerAop");
        userService.saveByInnerAop(user);
    }

    /**
     * 这个例子中，数据会正常回滚
     */
    @Test(expected = RuntimeException.class)
    public void saveByInnerAopWithSelfIoC() {
        User user = new User("fork", "saveByInnerAopWithSelfIoC");
        userService.saveByInnerAopWithSelfIoC(user);
    }

    @Test
    public void saveByInnerClass() throws Exception {
        User user = new User("google", "saveByInnerClass");
        userService.saveByInnerClass(user);
    }

    @Test
    public void saveByInnerClassWithSelfIoC() throws Exception {
        User user = new User("hello", "saveByInnerClassWithSelfIoC");
        userService.saveByInnerClassWithSelfIoC(user);
    }
}