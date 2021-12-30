package com.snf.dsds.dao;

import com.snf.dsds.bean.User;

import java.util.List;

public interface UserDao {

    User findByUsername(String username);

    User findUser(User user);

    List<User> queryUsers();

    int insert(User user);

}
