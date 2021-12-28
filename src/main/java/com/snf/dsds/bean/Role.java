package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 11:13
 **/
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = -89216080972912090L;
    private long id;
    private String role;
    private String desc;
}
