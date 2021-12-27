package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 14:12
 **/
@Data
public class PlatformType implements Serializable {
    private static final long serialVersionUID = 6303761761688432959L;

    /**id*/
    private Long id;
    /**名称*/
    private String name;
}
