package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.CtdDetail;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.projectEnum.ParsExcelEnum;
import com.snf.dsds.common.projectEnum.SearchTypeEnum;
import com.snf.dsds.common.utils.DateUtils;
import com.snf.dsds.common.utils.ExcelUtils;
import com.snf.dsds.service.ICtdDataRecordsService;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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

    @Value("${ctd.upload.path}")
    private String uploadPath;

    @Value("${ctd.download.path}")
    private String downloadPath;

    static ReentrantLock lock = new ReentrantLock();


    /**
     * 上传ctd数据文件
     * @param voyageNumber
     * @param multipartFile
     * @return
     */
    @PostMapping("/uploadCtdDataFiles")
    public RespBean uploadCtdDataFile(String voyageNumber,@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传数据接口");
        Long start = System.currentTimeMillis();
        try{
//            lock.lock();
            String fileName = multipartFile.getOriginalFilename();
            //校验指定航次编号对应的数据是否已经上传了该文件，不存在的才支持上传
            if(ctdDataRecordsService.checkFileExist(voyageNumber,fileName)){
                throw new CtdException("文件已经上传，请勿重复上传！");
            }
            String path =  uploadPath +voyageNumber;
            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(path,fileName);
            multipartFile.transferTo(file);
            // 将ctd数据解析并保存到数据库
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader read = new InputStreamReader (inputStream,"UTF-8");
            BufferedReader reader=new BufferedReader(read);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\r\n");
            }
            inputStream.close();
            String[] strArr = StringUtils.split(stringBuilder.toString(),"\r\n");
            List<CtdDetail> ctdDetails = new ArrayList<>();
            // 相同深度的数据只存一条
            List<String> deptList = new ArrayList<>();
            for(int i=1;i<strArr.length;i++){
                String[] arr = strArr[i].split("，");
                if (arr.length < 5) continue;
                if(deptList.contains(arr[2])){
                    continue;
                }
                deptList.add(arr[2]);
                CtdDetail ctdDetail = new CtdDetail(fileName,Double.parseDouble(arr[1])
                        ,Double.parseDouble(arr[4]),Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
                ctdDetails.add(ctdDetail);
            }
            // 更新数据库状态
            ctdDataRecordsService.saveCtdDetail(ctdDetails,fileName,true);
        }catch (CtdException e){
            log.error("上传出现错误，原因【{}】",e.getMessage());
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("上传错误，错误原因【{}】", e);
            return RespBean.error("上传失败,请联系管理员！");
        }finally {
//            lock.unlock();
            log.info("上传数据接口执行耗时【{}毫秒】",System.currentTimeMillis()-start);
        }
        return RespBean.ok("上传成功");
    }

    /**
     * 上传excel
     * @param multipartFile
     * @return
     */
    @PostMapping("/uploadCtdDataRecords")
    public RespBean uploadExcel(@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传excel接口");
        List<List<Object>> dataList ;
        try{
            lock.lock();
            String path = uploadPath+"excel/";
            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            String name = multipartFile.getOriginalFilename();
            String[] strings = name.split("\\.");
            String fileName = strings[0]+"-"+ DateUtils.currentFormatDate("yyyyMMddHHmmss")+"."+strings[1];
            File file = new File(path,fileName);
            multipartFile.transferTo(file);
            FileInputStream inputStream = new FileInputStream(file);
            dataList = ExcelUtils.excelToShopIdList(2,inputStream);
            if(CollectionUtils.isEmpty(dataList)){
                return RespBean.error("上传文件内容为空");
            }
            // 将数据转换到bean对象中,并将不存在的航次编号保存到列表中
            List<String> noExistVoyageNums = new ArrayList<>();
            List<CtdDataRecord> ctdDataRecordList = dataToCtdDataRecordList(dataList,noExistVoyageNums);
            //将文件保存到本地并将数据存入数据库
            ctdDataRecordsService.addExcelData(ctdDataRecordList,noExistVoyageNums);
        }catch (CtdException e){
            log.error("上传失败【{}】",e);
            return RespBean.error(e.getMessage());
        }catch (Exception ex){
            log.error("出现系统错误，原因【{}】",ex);
            return RespBean.error("出现系统错误，请联系管理员！");
        }finally {
            lock.unlock();
        }
        return RespBean.ok("上传成功");
    }

    /**
     * 将excel解析数据转换为CtdDataRecord对象，校验格式是否正确
     */
    private List<CtdDataRecord> dataToCtdDataRecordList(List<List<Object>> dataList,List<String> noExistVoyageNums){
        List<CtdDataRecord> ctdDataRecordList = new ArrayList<>();
        //从数据库获取平台类型,处理状态,设备类型
        Map<String,Long> searchParamTypeMap = dataSearchService.getStrIdMap(SearchTypeEnum.SEARCH_TYPE_PLATFORM.getValue()
                ,SearchTypeEnum.SEARCH_TYPE_DATASTATUS.getValue(),SearchTypeEnum.SEARCH_TYPE_DEVTYPE.getValue(),
                SearchTypeEnum.SEARCH_TYPE_VOYAGENUMBER.getValue());

        //记录错误行map
        Map<Integer,String> errRowColMap = new LinkedHashMap<>();
        for(int i =0;i<dataList.size();i++){
            List rowList = dataList.get(i);
            CtdDataRecord ctdDataRecord = new CtdDataRecord();
            // 校验航次编号是否存在
            String voyageNumber = (String) rowList.get(ParsExcelEnum.VOYAGE_NUMBER.ordinal());
            if (! searchParamTypeMap.containsKey(voyageNumber) && !noExistVoyageNums.contains(voyageNumber)){
                noExistVoyageNums.add(voyageNumber);
            }
            ctdDataRecord.setVoyageNumber(voyageNumber);
            ctdDataRecord.setShipName((String) rowList.get(ParsExcelEnum.SHIP_NAME.ordinal()));
            Long platformType = searchParamTypeMap.get((String) rowList.get(ParsExcelEnum.PLATFORM_TYPE.ordinal()));
            if (platformType == null){
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"平台类型":errRowColMap.get(i+1)+"，平台类型");
            }else{
                ctdDataRecord.setPlatformType(platformType);
            }
            ctdDataRecord.setPlatformName((String)rowList.get(ParsExcelEnum.PLATFORM_NAME.ordinal()));
            try{
                Long startTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(ParsExcelEnum.START_TIME.ordinal()))).getTime();
                ctdDataRecord.setStartTime(startTime/1000);
                Long finishTime = HSSFDateUtil.getJavaDate(Double.parseDouble((String)rowList.get(ParsExcelEnum.FINISH_TIME.ordinal()))).getTime();
                ctdDataRecord.setFinishTime(finishTime/1000);
            }catch (Exception e){
                log.error("获取上传数据中的时间戳出现错误，错误原因【{}】", e.getMessage());
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"开始时间或结束时间":errRowColMap.get(i+1)+"，开始时间或结束时间");
            }
            ctdDataRecord.setDiveNum((String)rowList.get(ParsExcelEnum.DIVE_NUM.ordinal()));
            //经度、维度和深度要乘以不同的系数
            try{
                ctdDataRecord.setLongitudeLayout(conversion((String)rowList.get(ParsExcelEnum.LONGITUDE_LAYOUT.ordinal()),1));
                ctdDataRecord.setLatitudeLayout(conversion((String)rowList.get(ParsExcelEnum.LATITUDE_LAYOUT.ordinal()),2));
                ctdDataRecord.setDepthLayout(conversion((String)rowList.get(ParsExcelEnum.DEPTH_LAYOUT.ordinal()),3));
            }catch (Exception e){
                log.error("经纬度校验有错误，错误原因【{}】",e);
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"经纬度数据":errRowColMap.get(i+1)+"，经纬度数据");
            }
            Long dveType = searchParamTypeMap.get((String) rowList.get(ParsExcelEnum.DEV_TYPE.ordinal()));
            if (dveType == null){
                errRowColMap.put(i+1,StringUtils.isEmpty(errRowColMap.get(i+1))?"设备类型":errRowColMap.get(i+1)+"，设备类型");
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
     * 字符串转double并校验经纬度
     */
    private Double conversion(String str,int flag){
        BigDecimal num1 = new BigDecimal(str);
        if (flag ==1 &&
                (num1.compareTo(new BigDecimal(180)) == 1 || num1.compareTo(new BigDecimal(-180)) == -1 )){
            throw new CtdException("导入数据中有经度数据超出正常范围，请检查！");
        }
        if(flag ==2 &&
                (num1.compareTo(new BigDecimal(90)) == 1 || num1.compareTo(new BigDecimal(-90)) == -1 )){
            throw new CtdException("导入数据中有维度度数据超出正常范围，请检查！");
        }
        return num1.doubleValue();
    }


    /**
     * 下载ctd数据文件
     * @param dataSetSns
     * @param response
     */
    @RequestMapping(value = "downloadRecordFiles",method = RequestMethod.POST)
    public void downloadRecordFile(@RequestBody String[] dataSetSns, HttpServletResponse response) {
        log.info("进入下载ctd数据接口");
        try{
            lock.lock();
            log.info("=============》获取到锁，开始执行");
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            String zipfile = ctdDataRecordsService.queryAndZipData(dataSetSns);
            InputStream inputStream = new FileInputStream(new File(downloadPath,zipfile));
            response.setHeader("Content-Disposition","attachment;filename=\"" + zipfile + "\"");
            IOUtils.copy(inputStream,response.getOutputStream());
            inputStream.close();
            File download = new File(downloadPath);
            FileUtils.deleteDirectory(download);
        }catch (Exception e){
            log.error("下载出现错误，原因【{}】",e);
        }finally {
            lock.unlock();
        }
    }



}
