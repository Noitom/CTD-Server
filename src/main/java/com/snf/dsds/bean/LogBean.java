package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-30 11:24
 **/
@Data
public class LogBean implements Serializable {
    private static final long serialVersionUID = -947679729059109463L;

    private String username;
    private String requestUrl;
    private String requestParam;
    private Date requestDate;
}
