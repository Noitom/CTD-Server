package com.snf.dsds.common.projectEnum;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-28 13:59
 **/
public enum SearchTypeEnum {

    /**设备类型*/
    SEARCH_TYPE_DEVTYPE(1),
    /**平台类型*/
    SEARCH_TYPE_PLATFORM(2),
    /**处理状态*/
    SEARCH_TYPE_DATASTATUS(4),
    /**航次编号*/
    SEARCH_TYPE_VOYAGENUMBER(0)
    ;



    private Integer value;

    SearchTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


}
