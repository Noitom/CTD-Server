package com.snf.dsds.bean;

import lombok.Data;

/**
 * @Classname CtdDetail
 * @Description 温盐深详情类
 * @Date 2022/1/21 11:39
 * @Created by admin
 */
@Data
public class CtdDetail {

    private String fileName;
    private Double temperature;
    private Double salinity;
    private Double depth;

    public CtdDetail() {
    }

    public CtdDetail(String fileName, Double temperature, Double salinity, Double depth) {
        this.fileName = fileName;
        this.temperature = temperature;
        this.salinity = salinity;
        this.depth = depth;
    }
}
