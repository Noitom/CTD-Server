package com.snf.dsds;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.snf.dsds.dao")
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class DsdsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DsdsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DsdsApplication.class);
    }

}
