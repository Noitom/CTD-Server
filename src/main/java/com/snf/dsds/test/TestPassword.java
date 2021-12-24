package com.snf.dsds.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-24 09:23
 **/
public class TestPassword {
    public static void main(String[] args) {
        String pass = "admin";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final String passHash = encoder.encode(pass);
        System.out.println(passHash);
    }
}
