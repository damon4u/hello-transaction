package com.damon4u.demo.service.impl;

import com.damon4u.demo.annotation.DataSource;
import com.damon4u.demo.dao.UserMapper;
import com.damon4u.demo.datasource.DataSourceType;
import com.damon4u.demo.domain.User;
import com.damon4u.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * 注入自己，只能用这种方式，不能用constructor，且必须是单例，否则会产生循环依赖
     */
    @Resource
    private UserService userService;

    /**
     * 最普通的保存操作
     * <p>
     * 加了主库注解，保存到主库中
     *
     * @param user 用户信息
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
     * 会抛异常
     *
     * @param user 用户信息
     */
    @Override
    public void saveUseDefaultDataSource(User user) {
        userMapper.insert(user);
    }

    /**
     * 根据id查询用户
     * 默认使用从库
     *
     * @param id    userId
     * @return      用户信息
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }


    /////// 以下是类内部方法间调用AOP不生效的 ////////

    /**
     * 先读后写
     * 外部方法没有注解
     * <p>
     * 这个方法会报错！
     * 外部调用这个方法，没有aop，然后这个内部使用this的方式调用带有注解的方法，aop不起作用
     * <p>
     * 这是Spring AOP自身缺陷限制的，一个类内部方法间的this调用是不会触发aop的
     * 只要这个类有一个
     *
     * @param user 用户信息
     */
    @Override
    public void readAndWriteDefaultDataSource(User user) {
        User userById = getUserById(1L);
        logger.info("userById={}", userById);
        save(user);
    }

    /**
     * 使用注入的自己调用方法，每次都会从容器中拿那个单例对象（代理），每次aop都会生效
     *
     * @param user  用户信息
     */
    @Override
    public void readAndWriteWithSelfIoC(User user) {
        User userById = userService.getUserById(1L);
        logger.info("userById={}", userById);
        userService.save(user);
    }

    /**
     * 使用主库，带事务的写
     * 内部抛RuntimeException，默认会回滚这种异常，不会保存成功
     *
     * @param user
     */
    @Override
    @Transactional
    @DataSource(DataSourceType.WRITE)
    public void saveWithTransaction(User user) {
        userMapper.insert(user);
        throw new RuntimeException("saveWithTransaction exception");
    }

    /**
     * 类内部方法间调用，一个没有事务的方法调用一个有事务的方法，也要注意aop不生效导致事务丢失的情况
     *
     * 本例子就是会丢失事务，异常不回滚
     *
     * 当然主库要在外层指明，否则走读库就更不行了
     *
     * @param user
     */
    @Override
    @DataSource(DataSourceType.WRITE)
    public void saveByInnerAop(User user) {
        saveWithTransaction(user);
    }

    /**
     * 使用内部注入的方式解决方法间调用事务丢失的问题
     *
     * @param user
     */
    @Override
    public void saveByInnerAopWithSelfIoC(User user) {
        userService.saveWithTransaction(user);
    }

    /**
     * 使用外部方法新建内部类对象执行事务方法，必须使用内部注入，使用userService来调用，否则AOP无法实现
     * 在saveByInnerClass方法上面指定数据源不好用，加事务都不好使
     *
     * @param user
     */
    @Override
    @Transactional
    @DataSource(DataSourceType.WRITE)
    public void saveByInnerClass(User user) throws ExecutionException, InterruptedException {
        Future<?> submit = EXECUTOR_SERVICE.submit(new FooJob(user));
        submit.get();
    }

    @Override
    public void saveByInnerClassWithSelfIoC(User user) throws ExecutionException, InterruptedException {
        Future<?> submit = EXECUTOR_SERVICE.submit(new FooIoCJob(user));
        submit.get();
    }

    /**
     * 执行不了，无法切换数据源，无法回滚
     */
    class FooJob implements Runnable {

        private User user;

        FooJob(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            try {
                saveWithTransaction(user);
            } catch (Exception e) {
                logger.error("FooJob saveWithTransaction error.{}", e.getMessage());
            }
        }
    }

    /**
     * 正常执行，切换数据源，有事务，遇到异常回滚
     */
    class FooIoCJob implements Runnable {

        private User user;

        FooIoCJob(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            try {
                userService.saveWithTransaction(user);
            } catch (Exception e) {
                logger.error("FooIoCJob saveWithTransaction error.{}", e.getMessage());
            }
        }
    }

    /////// 以下是Transaction不切换数据源的 ////////

    /**
     * 这个方法会报错！
     *
     * 表面看，已经使用userService去调用本类中的方法了，AOP应该会生效
     * 在save方法中，应该能够使用主库去写，但是加了事务之后，没有生效
     * 这是由于事务在readAndWriteWithTransaction方法开始时，就获取了数据源，并缓存在ThreadLocal中，本例子中是默认的读库
     * 即只会调用一次determineCurrentLookupKey方法
     * 之后事务中使用的数据源都会用这个，不会再切换
     * 因此AOP的order应该高于事务的order
     * @param user
     */
    @Override
    @Transactional
    public void readAndWriteWithTransaction(User user) {
        User userById = userService.getUserById(1L);
        logger.info("userById={}", userById);
        userService.save(user);
    }

    /**
     * 当然，一般情况下，我们在打开事务时，会指定为写数据源
     *
     * 如果需要非事务读，那可以把非事务读扔到事务方法外面
     * 否则就全部使用主库读了
     *
     * 用了事务注解，下面就没必要使用userService去调用本类中的方法了，反正也切换不了
     *
     * @param user
     */
    @Override
    @Transactional
    @DataSource(DataSourceType.WRITE)
    public void readAndWriteWithTransactionUseMaster(User user) {
        User userById = userService.getUserById(1L);
        logger.info("userById={}", userById);
        userService.save(user);
    }
}
