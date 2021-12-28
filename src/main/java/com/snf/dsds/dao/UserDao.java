package com.snf.dsds.dao;

import com.snf.dsds.bean.User;

public interface UserDao {

    User findByUsername(String username);

    User findUser(User user);

}
