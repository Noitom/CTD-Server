package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.projectEnum.ParsExcelEnum;
import com.snf.dsds.common.utils.ExcelUtils;
import com.snf.dsds.service.ICtdDataRecordsService;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-24 17:32
 **/
@Slf4j
@RestController
public class DataUploadController {

    @Autowired
    ICtdDataRecordsService ctdDataRecordsService;

    @Autowired
    IDataSearchService dataSearchService;

    private static final Integer SEARCH_TYPE_PLATFORM = 2;//平台类型
    private static final Integer SEARCH_TYPE_DATASTATUS = 4;//处理状态
    private static final Integer SEARCH_TYPE_DEVTYPE = 1;//设备类型

    @PostMapping("/uploadFile")
    public RespBean uploadFile(@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传数据接口");
        String fileName = multipartFile.getOriginalFilename();
        String path =  "E:/file/"+fileName;
        File file = new File(path);
        try{
            multipartFile.transferTo(file);
        }catch (Exception e){
            log.error(e.getMessage());
            return RespBean.error("上传失败");
        }
        return RespBean.ok("上传成功");
    }

    @PostMapping("/uploadExcel")
    public RespBean uploadExcel(@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传excel接口");
        List<List<Object>> dataList ;
        try{
            dataList = ExcelUtils.excelToShopIdList(2,multipartFile.getInputStream());
            if(CollectionUtils.isEmpty(dataList)){
                return RespBean.error("上传文件内容为空");
            }
            // 将数据转换到bean对象中
            List<CtdDataRecord> ctdDataRecordList = dataToCtdDataRecordList(dataList);
            //将文件保存到本地并将数据存入数据库
            ctdDataRecordsService.addExcelData(ctdDataRecordList);
        }catch (CtdException e){
            log.error("上传失败【{}】",e);
            return RespBean.error(e.getMessage());
        }catch (Exception ex){
            log.error("出现系统错误，原因【{}】",ex);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
        return RespBean.ok("上传成功");
    }

    /**
     * 将excel解析数据转换为CtdDataRecord对象，校验格式是否正确
     */
    private List<CtdDataRecord> dataToCtdDataRecordList(List<List<Object>> dataList){
        List<CtdDataRecord> ctdDataRecordList = new ArrayList<>();
        //从数据库获取平台类型,处理状态,设备类型
        Map<String,Long> searchParamTypeMap = dataSearchService.getStrIdMap(SEARCH_TYPE_PLATFORM,SEARCH_TYPE_DATASTATUS,SEARCH_TYPE_DEVTYPE);

        //记录错误行map
        Map<Integer,String> errRowColMap = new LinkedHashMap<>();
        for(int i =0;i<dataList.size();i++){
            List rowList = dataList.get(i);
            CtdDataRecord ctdDataRecord = new CtdDataRecord();
            ctdDataRecord.setVoyageNumber((String) rowList.get(ParsExcelEnum.VOYAGE_NUMBER.ordinal()));
            ctdDataRecord.setShipName((String) rowList.get(ParsExcelEnum.SHIP_NAME.ordinal()));
            Long platformType = searchParamTypeMap.get((String) rowList.get(ParsExcelEnum.PLATFORM_TYPE.ordinal()));
            if (platformType == null){
                errRowColMap.put(i+1,"平台类型");
            }else{
                ctdDataRecord.setPlatformType(platformType);
            }
            ctdDataRecord.setPlatformName((String)rowList.get(ParsExcelEnum.PLATFORM_NAME.ordinal()));
            ctdDataRecord.setStationNum((String)rowList.get(ParsExcelEnum.STATION_NUM.ordinal()));
            try{
                Long startTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(ParsExcelEnum.START_TIME.ordinal()))).getTime();
                ctdDataRecord.setStartTime(startTime/1000);
                Long finishTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(ParsExcelEnum.FINISH_TIME.ordinal()))).getTime();
                ctdDataRecord.setFinishTime(finishTime/1000);
            }catch (Exception e){
                log.error("获取上传数据中的时间戳出现错误，错误原因【{}】",e.getMessage());
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"开始时间或结束时间":errRowColMap.get(i+1)+"，开始时间或结束时间");
            }
            ctdDataRecord.setDiveNum((String)rowList.get(ParsExcelEnum.DIVE_NUM.ordinal()));
            //经度、维度和深度要乘以不同的系数
            try{
                ctdDataRecord.setLongitudeLayout(conversion((String)rowList.get(ParsExcelEnum.LONGITUDE_LAYOUT.ordinal()),"100000000",1));
                ctdDataRecord.setLatitudeLayout(conversion((String)rowList.get(ParsExcelEnum.LATITUDE_LAYOUT.ordinal()),"100000000",2));
                ctdDataRecord.setDepthLayout(conversion((String)rowList.get(ParsExcelEnum.DEPTH_LAYOUT.ordinal()),"10000",3));
                ctdDataRecord.setLongitudeWork(conversion((String)rowList.get(ParsExcelEnum.LONGITUDE_WORK.ordinal()),"100000000",1));
                ctdDataRecord.setLatitudeWork(conversion((String)rowList.get(ParsExcelEnum.LATITUDE_WORK.ordinal()),"100000000",2));
                ctdDataRecord.setDepthWork(conversion((String)rowList.get(ParsExcelEnum.DEPTH_WORK.ordinal()),"10000",3));
            }catch (Exception e){
                log.error("经纬度校验有错误，错误原因【{}】",e);
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"经纬度数据":errRowColMap.get(i+1)+"，经纬度数据");
            }
            Long dveType = searchParamTypeMap.get((String) rowList.get(ParsExcelEnum.DEV_TYPE.ordinal()));
            if (dveType == null){
                errRowColMap.put(i+1,"设备类型");
            }else{
                ctdDataRecord.setDevType(dveType);
            }
            ctdDataRecord.setDevModel((String)rowList.get(ParsExcelEnum.DEV_MODEL.ordinal()));
            ctdDataRecord.setDevSn((String)rowList.get(ParsExcelEnum.DEV_SN.ordinal()));
            ctdDataRecord.setDataSetSn((String)rowList.get(ParsExcelEnum.DATA_SET_SN.ordinal()));
            ctdDataRecord.setDataFormat((String)rowList.get(ParsExcelEnum.DATA_FORMAT.ordinal()));
            Long dataStatus =  searchParamTypeMap.get((String) rowList.get(ParsExcelEnum.DATA_STATUS.ordinal()));
            if(dataStatus == null){
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"处理状态":errRowColMap.get(i+1)+"，处理状态");
            }else{
                ctdDataRecord.setDataStatus(dataStatus);
            }
            ctdDataRecord.setDataFileName((String)rowList.get(ParsExcelEnum.DATA_FILE_NAME.ordinal()));
            ctdDataRecordList.add(ctdDataRecord);
        }
        if (!CollectionUtils.isEmpty(errRowColMap)){
            String message = "导入数据中";
            for( Map.Entry<Integer,String> entry:errRowColMap.entrySet()){
                message += "第" +entry.getKey() +"行的"+entry.getValue()+";";
            }
            message+="数据错误，请检查或联系管理员！";
            throw new CtdException(message);
        }
        return ctdDataRecordList;
    }

    /**
     * 字符串转double*参数转long
     */
    private Long conversion(String str,String param,int flag){
        BigDecimal num1 = new BigDecimal(str);
        if (flag ==1 &&
                (num1.compareTo(new BigDecimal(180)) == 1 || num1.compareTo(new BigDecimal(-180)) == -1 )){
            throw new CtdException("导入数据中有经度数据超出正常范围，请检查！");
        }
        if(flag ==2 &&
                (num1.compareTo(new BigDecimal(90)) == 1 || num1.compareTo(new BigDecimal(-90)) == -1 )){
            throw new CtdException("导入数据中有维度度数据超出正常范围，请检查！");
        }
        BigDecimal num2 = new BigDecimal(param);
        BigDecimal result = num1.multiply(num2);
        return result.longValue();
    }
}
