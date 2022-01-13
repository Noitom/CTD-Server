package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.User;
import com.snf.dsds.common.annotation.Decrypt;
import com.snf.dsds.common.annotation.Encrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 09:26
 **/
@RestController
public class TestController {

    @PostMapping("/test")
    public RespBean test(@RequestBody User user){
        return RespBean.ok("请求成功",user);
    }


}
