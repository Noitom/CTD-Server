package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.common.Exception.CtdException;
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

    private static final Integer SEARCH_TYPE_1 = 1;//设备类型
    private static final Integer SEARCH_TYPE_4 = 4;//处理状态

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
        }catch (Exception e){
            log.error("上传失败【{}】",e);
            return RespBean.error(e.getMessage());
        }
        return RespBean.ok("上传成功");
    }

    /**
     * 将excel解析数据转换为CtdDataRecord对象，校验格式是否正确
     */
    private List<CtdDataRecord> dataToCtdDataRecordList(List<List<Object>> dataList){
        List<CtdDataRecord> ctdDataRecordList = new ArrayList<>();
        //从数据库获取平台类型
        Map<String,Long> platforyTypeMap = dataSearchService.getDataMap(SEARCH_TYPE_1);
        //从数据库获取处理状态
        Map<String,Long> dataStatusMap =dataSearchService.getDataMap(SEARCH_TYPE_4);
        //记录错误行map
        Map<Integer,String> errRowColMap = new LinkedHashMap<>();
        for(int i =0;i<dataList.size();i++){
            List rowList = dataList.get(i);
            CtdDataRecord ctdDataRecord = new CtdDataRecord();
            ctdDataRecord.setVoyageNumber((String) rowList.get(0));
            ctdDataRecord.setShipName((String) rowList.get(1));
            Long platformType = platforyTypeMap.get((String) rowList.get(2));
            if (platformType == null){
                errRowColMap.put(i+1,"平台类型");
            }else{
                ctdDataRecord.setPlatformType(platformType);
            }
            ctdDataRecord.setPlatformName((String)rowList.get(3));
            ctdDataRecord.setStationNum((String)rowList.get(4));
            try{
                Long startTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(5))).getTime();
                ctdDataRecord.setStartTime(startTime/1000);
                Long finishTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(6))).getTime();
                ctdDataRecord.setFinishTime(finishTime/1000);
            }catch (Exception e){
                log.error("获取上传数据中的时间戳出现错误，错误原因【{}】",e.getMessage());
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"开始时间或结束时间":errRowColMap.get(i+1)+"，开始时间或结束时间");
            }
            ctdDataRecord.setDiveNum((String)rowList.get(7));
            //经度、维度和深度要乘以不同的系数
            try{
                ctdDataRecord.setLongitudeLayout(conversion((String)rowList.get(8),"100000000",1));
                ctdDataRecord.setLatitudeLayout(conversion((String)rowList.get(9),"100000000",2));
                ctdDataRecord.setDepthLayout(conversion((String)rowList.get(10),"10000",3));
                ctdDataRecord.setLongitudeWork(conversion((String)rowList.get(11),"100000000",1));
                ctdDataRecord.setLatitudeWork(conversion((String)rowList.get(12),"100000000",2));
                ctdDataRecord.setDepthWork(conversion((String)rowList.get(13),"10000",3));
            }catch (Exception e){
                log.error("经纬度校验有错误，错误原因【{}】",e);
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"经纬度数据":errRowColMap.get(i+1)+"，经纬度数据");
            }
            ctdDataRecord.setDevModel((String)rowList.get(14));
            ctdDataRecord.setDevSn((String)rowList.get(15));
            ctdDataRecord.setDataSetSn((String)rowList.get(16));
            ctdDataRecord.setDataFormat((String)rowList.get(17));
            Long dataStatus =  dataStatusMap.get((String) rowList.get(18));
            if(dataStatus == null){
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"处理状态":errRowColMap.get(i+1)+"，处理状态");
            }else{
                ctdDataRecord.setDataStatus(dataStatus);
            }
            ctdDataRecord.setDataFileName((String)rowList.get(19));
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
