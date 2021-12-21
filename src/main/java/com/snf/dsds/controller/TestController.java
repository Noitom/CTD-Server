package com.snf.dsds.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 09:26
 **/
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "test success";
    }

}
