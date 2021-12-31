package com.snf.dsds.dao;

import com.snf.dsds.bean.Role;
import com.snf.dsds.bean.User;
import com.snf.dsds.bean.UserIcon;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User findByUsername(String username);

    User findUser(User user);

    List<User> queryUsers();

    int insert(User user);

    int delete(Long id);

    int update(User user);

    List<Role> queryRoles();

    List<UserIcon> queryIcons();

    List<Map<String,String>> querySexs();

}
