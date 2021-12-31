package com.snf.dsds.service.impl;

import com.snf.dsds.bean.User;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.utils.PasswordUtils;
import com.snf.dsds.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 10:44
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("进入登录接口，登录用户【{}】",username);
        User user = userDao.findByUsername(username);
        if(user==null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //用于添加用户的权限。要把用户权限添加到authorities
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), authorities);
    }


    public User getUserInfo(User user){
        User result = userDao.findUser(user);
        if(result == null ){
            throw new CtdException("没有查到用户信息");
        }
        return result;
    }

    public List<User> getUsers(){
        return userDao.queryUsers();
    }

    public User addUser(User user){
        // 密码进行加密
        user.setPassword(PasswordUtils.encryptPassword(user.getPassword()));
        // 生成时间
        user.setReg(System.currentTimeMillis());
        // 保存入库
        if(userDao.insert(user) == 0){
            throw new CtdException("添加失败，请重试！");
        }
        return user;
    }

    public void deleteUser(Long id){
        if(userDao.delete(id) == 0){
            throw new CtdException("删除记录不存在，请检查！");
        }
    }

    public void updateUser(User user){
        if(StringUtils.isNotEmpty(user.getPassword())){
            // 密码进行加密
            user.setPassword(PasswordUtils.encryptPassword(user.getPassword()));
        }
        // 保存入库
        if(userDao.update(user) == 0){
            throw new CtdException("修改记录不存在，请检查！");
        }
    }

    public Map<String,Object> getUserParameter(){
        Map<String,Object> result = new HashMap<>();
        result.put("roles",userDao.queryRoles());
        result.put("icons",userDao.queryIcons());
        result.put("sexs",userDao.querySexs());
        return result;
    }
}
