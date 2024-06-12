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

    private Integer id;
    private String  fileName;
    private Double  temperature;
    private Double  salinity;
    private Double  depth;
    private Double  soundSpeed;

    public CtdDetail() {
    }

    public CtdDetail(Integer id, String fileName, Double temperature, Double salinity, Double depth, Double soundSpeed) {
        this.id = id;
        this.fileName = fileName;
        this.temperature = temperature;
        this.salinity = salinity;
        this.depth = depth;
        this.soundSpeed = soundSpeed;
    }
}
