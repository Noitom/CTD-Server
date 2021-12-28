package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.User;
import com.snf.dsds.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-28 15:00
 **/
@Slf4j
@RestController
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @RequestMapping("/requestUserInfo")
    public RespBean requestUserInfo(@RequestBody User user){
        try{
            return RespBean.ok("查询成功",userService.getUserInfo(user));
        }catch (Exception e){
            log.error("获取用户信息出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }


}
