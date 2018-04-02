package com.damon4u.demo.service;

import com.damon4u.demo.domain.User;

import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-03-07 21:45
 */
public interface UserService {

    void save(User user);

    void saveUseDefaultDataSource(User user);

    User getUserById(Long id);

    void readAndWriteDefaultDataSource(User user);

    void readAndWriteWithSelfIoC(User user);

    void saveWithTransaction(User user);

    void saveByInnerAop(User user);

    void saveByInnerAopWithSelfIoC(User user);

    void saveByInnerClass(User user) throws ExecutionException, InterruptedException;

    void saveByInnerClassWithSelfIoC(User user) throws ExecutionException, InterruptedException;

    void readAndWriteWithTransaction(User user);

    void readAndWriteWithTransactionUseMaster(User user);
}
