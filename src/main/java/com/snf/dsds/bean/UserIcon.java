package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-28 15:13
 **/
@Data
public class UserIcon implements Serializable {
    private static final long serialVersionUID = 4476905322411317461L;

    private Integer id;
    private String icon;
}
