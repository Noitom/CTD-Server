package com.snf.dsds.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-23 09:17
 **/
@ConfigurationProperties(prefix = "spring.encrypt")
public class EncryptProperties {
    private final static String DEFAULT_KEY = "aaaaaaaaaaaaaaaa";
    private String key = DEFAULT_KEY;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
