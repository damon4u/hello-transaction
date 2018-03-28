package com.damon4u.demo.service;

import com.damon4u.demo.domain.User;

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

    void readAndWrite(User user);
}
