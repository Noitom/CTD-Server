package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.User;
import com.snf.dsds.common.annotation.Encrypt;
import com.snf.dsds.common.config.EncryptProperties;
import com.snf.dsds.common.utils.AESUtils;
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

    @Encrypt
    @PostMapping("/test")
    public RespBean test(@RequestBody User user) throws Exception {
        user.setPassword(AESUtils.aesDecrypt(user.getPassword(), EncryptProperties.getKey()));
        return RespBean.ok("请求成功",user);
    }


}
