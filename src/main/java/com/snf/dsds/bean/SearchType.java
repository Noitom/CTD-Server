package com.snf.dsds.bean;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-24 10:10
 **/
@Data
public class SearchType implements Serializable {
    private static final long serialVersionUID = 5923475973304356771L;

    /**
     * 类型id
     */
    private Integer id;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 子数据
     */
    private List<SearchParameter> searchParameters;


}
