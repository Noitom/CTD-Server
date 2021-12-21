package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-21 10:36
 **/
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 38042216738788112L;

    /*主键*/
    private Integer id;
    /*登录名*/
    private String username;
    /*登录密码(使用BCryptPasswordEncoder加密，因此password字符长度必须大于60)*/
    private String password;
    /*真实用户名*/
    private String realname;
    /*性别*/
    private String sex;
    /*部门ID*/
    private Integer deptId;
    /*负责工作*/
    private String majorWork;
    /*政治面貌*/
    private String political;
    /*职称*/
    private String technicalTitle;
    /*电子邮箱*/
    private String email;
    /*电话(手机)*/
    private String mobilePhone;
    /*创建人*/
    private Integer crtUser;
    /*创建时间*/
    private Date crtDate;

}
