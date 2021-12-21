package com.snf.dsds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.snf.dsds.dao")
@SpringBootApplication
public class DsdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsdsApplication.class, args);
    }

}
