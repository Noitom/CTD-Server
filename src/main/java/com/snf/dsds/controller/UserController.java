package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.User;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

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
        }catch (CtdException e){
            log.error("没有查到用户信息");
            return RespBean.error("没有查到用户信息！");
        }
        catch (Exception e){
            log.error("获取用户信息出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @GetMapping("/requestUsers")
    public RespBean requestUsers(){
        try{
            return RespBean.ok("查询成功",userService.getUsers());
        }catch (Exception e){
            log.error("获取用户信息出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @PostMapping("addUser")
    public RespBean addUser(@RequestBody  User user){
        try{
            User result = userService.addUser(user);
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("id",result.getId());
            resultMap.put("reg",result.getReg());
            return RespBean.ok("添加成功",resultMap);
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("添加用户信息出现错误，原因【{}】",e);
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
                return RespBean.error("用户名重复，请检查！");
            }
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @PostMapping("deleteUser")
    public RespBean deleteUser(@RequestBody User user){
        try{
            userService.deleteUser(user.getId());
            return RespBean.ok("删除成功");
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("删除用户信息出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @PostMapping("updateUser")
    public RespBean updateUser(@RequestBody  User user){
        try{
            userService.updateUser(user);
            return RespBean.ok("修改成功");
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("修改用户信息出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @GetMapping("requestUserParameter")
    public RespBean getUserParameter(){
        try{
            return RespBean.ok("查询成功",userService.getUserParameter());
        }catch (Exception e){
            log.error("查询用户参数出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }
}
