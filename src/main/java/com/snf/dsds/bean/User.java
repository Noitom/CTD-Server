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

    /**用户　ID，系统唯一*/
    private Integer id;
    /**名称*/
    private String username;
    /**加密后密码（AES 加密）*/
    private String password;
    /**角色类型*/
    private Integer role;
    /**性别*/
    private Integer sex;
    /**职业或职称*/
    private String title;
    /**系统头像 ID*/
    private Integer icon;
    /**注册时间*/
    private Integer reg;

    private Role roleObj;

    private UserIcon userIcon;

}
