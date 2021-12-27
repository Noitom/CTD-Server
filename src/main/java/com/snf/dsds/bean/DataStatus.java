package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 16:19
 **/
@Data
public class DataStatus implements Serializable {
    private static final long serialVersionUID = 110657400098270689L;

    /**id*/
    private Long id;
    /**名称*/
    private String status;
}
