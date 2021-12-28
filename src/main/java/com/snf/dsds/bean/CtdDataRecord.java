package com.snf.dsds.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 09:54
 **/
@Data
public class CtdDataRecord implements Serializable {
    private static final long serialVersionUID = 852666822298647109L;

    /**主键，唯一标识(数据集编号)*/
    private String dataSetSn;
    /**航次编号*/
    private String voyageNumber;
    /**科考船舶*/
    private String shipName;
    /**平台类型*/
    private Long platformType;
    /**平台类型名称*/
    private String platformTypeName;
    /**平台名称*/
    private String platformName;
    /**站位名称*/
    private String stationNum;
    /**开始时间*/
    private Long startTime;
    /**开始时间字符串*/
    private String startTimeStr;
    /**结束时间*/
    private Long finishTime;
    /**结束时间字符串*/
    private String finishTimeStr;
    /**潜次序号*/
    private String diveNum;
    /**布放经度（向后保存 8 位小数，实际数据 * 100000000）*/
    private Long longitudeLayout;
    /**布放纬度（向后保存 8 位小数，实际数据 * 100000000）*/
    private Long latitudeLayout;
    /**布放深度（向后保存 4 位小数，实际数据 * 10000）*/
    private Long depthLayout;
    /**工作经度（向后保存 8 位小数，实际数据 * 100000000）*/
    private Long longitudeWork;
    /**工作纬度（向后保存 8 位小数，实际数据 * 100000000）*/
    private Long latitudeWork;
    /**工作深度（向后保存 4 位小数，实际数据 * 10000）*/
    private Long depthWork;
    /**设备型号*/
    private String devModel;
    /**设备序列号*/
    private String devSn;
    /**数据格式*/
    private String dataFormat;
    /**处理状态*/
    private Long dataStatus;
    /**处理状态名称*/
    private String dataStatusName;
    /**原始数据文件名称*/
    private String dataFileName;
    /**设备类型*/
    private Long devType;
    /**设备类型名称*/
    private String devTypeName;

}
