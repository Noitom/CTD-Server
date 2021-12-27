package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 18:33
 **/
@Data
public class SearchParameter implements Serializable {
    private static final long serialVersionUID = 6546422997378046880L;

    /**id*/
    private Long id;
    /**类型*/
    private Long searchType;
    /**名称*/
    private String name;
}
