package com.snf.dsds.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.utils.ZipUtils;
import com.snf.dsds.dao.CtdDataRecordsDao;
import com.snf.dsds.service.ICtdDataRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 10:18
 **/
@Slf4j
@Service
public class CtdDataRecordServiceImpl implements ICtdDataRecordsService {

    @Value("${ctd.download.path}")
    private String downloadPath;

    @Value("${ctd.upload.path}")
    private String uploadPath;

    @Value("${ctd.zip.filename:ctd-data.zip}")
    private String zipFileName;

    @Value("${ctd.zip.errlogFileName:error-log.txt}")
    private String errlogFileName;

    @Value("${ctd.zip.dataFileName:data.txt}")
    private String dataFileName;

    private  Map<String,String> JSON_CONVERSION_MAP = ImmutableMap.<String, String>builder()
            .put("dataStatusName","处理状态名称")
            .put("devTypeName","设备类型名称")
            .put("startTimeStr","开始时间字符值")
            .put("finishTimeStr","结束时间字符值")
            .put("platformTypeName","平台类型名称")
            .put("dataSetSn","数据集编号")
            .put("voyageNumber","航次编号")
            .put("shipName","科考船舶")
            .put("platformType","平台类型")
            .put("platformName","平台名称")
            .put("stationNum","站位名称")
            .put("startTime","开始时间")
            .put("finishTime","结束时间")
            .put("diveNum","潜次序号")
            .put("longitudeLayout","布放经度")
            .put("latitudeLayout","布放纬度")
            .put("depthLayout","布放深度")
            .put("longitudeWork","工作经度")
            .put("latitudeWork","工作纬度")
            .put("depthWork","工作深度")
            .put("devModel","设备型号")
            .put("devSn","设备序列号")
            .put("dataFormat","数据格式")
            .put("dataStatus","处理状态")
            .put("dataFileName","原始数据文件名称")
            .put("devType","设备类型")
            .put("delFlag","删除标志")
            .put("dataExist","文件是否存在标志")
            .build();

    @Autowired
    CtdDataRecordsDao ctdDataRecordsDao;

    @Override
    public void addExcelData(List<CtdDataRecord> list) throws SQLIntegrityConstraintViolationException {
        // 将文件保存导本地将数据保存数据到数据库
        if(ctdDataRecordsDao.batchInsert(list) == 0){
            throw new CtdException("没有添加数据到数据库，请联系管理员！");
        }
    }

    @Override
    public void addCtdDataRecord(CtdDataRecord ctdDataRecord) {
        CtdDataRecord oldCtdData = ctdDataRecordsDao.ctdDataExist(ctdDataRecord.getDataSetSn());
        if(oldCtdData != null && !oldCtdData.getDelFlag()){
            throw new CtdException("数据已经存在，请勿重复添加！");
        }
        if(oldCtdData != null && StringUtils.isNotEmpty(oldCtdData.getDataSetSn()) && oldCtdData.getDelFlag()){
            ctdDataRecord.setDelFlag(false);
            ctdDataRecordsDao.updateCtdDataRecord(ctdDataRecord);
        }else{
            if(ctdDataRecordsDao.addCtdDataRecord(ctdDataRecord) != 1){
                throw new CtdException("没有添加数据到数据库，请联系管理员！");
            }
        }
    }

    @Override
    public List<CtdDataRecord>  queryListByParam(SearchParameter searchParameter) {
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryListBySearchParameter(searchParameter);
        return ctdDataRecordList;
    }

    @Override
    public void updateCtdDataRecord(CtdDataRecord ctdDataRecord) {
        if(ctdDataRecordsDao.updateCtdDataRecord(ctdDataRecord)!=1){
            throw new CtdException("该数据不存在，不能修改！");
        }
    }

    @Override
    public boolean checkFileExist(String voyageNumber, String fileName) {
        Boolean flag = ctdDataRecordsDao.queryFileExist(voyageNumber, fileName);
        if(flag == null){
            throw new CtdException("数据库没有对应数据，不允许上传！");
        }
        return flag;
    }

    @Override
    public void setDataExist(String fileName,Boolean exist) {
        ctdDataRecordsDao.updateExist(fileName,exist);
    }

    @Override
    public void deleteCtdDataRecord(String dataSetSn) {
        if(ctdDataRecordsDao.delete(dataSetSn) != 1){
            throw new CtdException("该数据不存在，不能删除！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCtdDataRecords(String[] dataSetSns) {
        // 删除数据和文件
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryDataBySns(dataSetSns);
        if(CollectionUtils.isEmpty(ctdDataRecordList)){
            throw new CtdException("数据不存在，不能删除！");
        }
        //先删除数据，在删除文件
        ctdDataRecordsDao.deletes(dataSetSns);
        //删除所有的文件
        String filepath;
        String filename;
        for (CtdDataRecord ctdDataRecord:ctdDataRecordList){
            filepath = uploadPath + ctdDataRecord.getVoyageNumber();
            filename = ctdDataRecord.getDataFileName();
            File file = new File(filepath,filename);
            if(file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public String queryAndZipData(String[] dataSetSns){
        // 从数据库查询数据
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryDataBySns(dataSetSns);
        //记录不存在的数据List
        List<String> dataNotExistList = new ArrayList<>(Arrays.asList(dataSetSns));
        //记录不存在的文件List
        List<String> fileNotExistList = new ArrayList<>();
        // 保存要打包的文件
        List<File> needZipFileList = new ArrayList<>();
        File errlogFile = new File(downloadPath,errlogFileName);
        File dataFileTxt = new File(downloadPath,dataFileName);

        String dataFilePath;
        String dataFileName;
        for(CtdDataRecord ctdDataRecord:ctdDataRecordList){
            //将数据存在的数据集编号移除list
            dataNotExistList.remove(ctdDataRecord.getDataSetSn());
            dataFilePath = uploadPath + ctdDataRecord.getVoyageNumber();
            dataFileName = ctdDataRecord.getDataFileName();
            File dataFile = new File(dataFilePath,dataFileName);
            //校验文件是否存在
            if(!dataFile.exists()){
                //将文件不存在的数据集编号添加到list
                fileNotExistList.add(ctdDataRecord.getDataSetSn());
                continue;
            }
            //将文件保存起来，方便打zip压缩包
            needZipFileList.add(dataFile);
        }
        String errLog = "";
        if(!CollectionUtils.isEmpty(dataNotExistList)){
            errLog += "数据库不存在以下数据集编号的数据：" + Arrays.toString(dataNotExistList.toArray()) + ";\r\n";
        }
        if(!CollectionUtils.isEmpty(fileNotExistList)){
            errLog += "系统不存在以下数据集编号的数据文件：" + Arrays.toString(fileNotExistList.toArray()) + ";\r\n";
        }
        //校验下载文件夹是否存在
        File downloadDir = new File(downloadPath);
        if(!downloadDir.exists()){
            downloadDir.mkdirs();
        }
        String zipPathFileName = downloadPath + zipFileName;

        String dataStr = JSON.toJSONString(ctdDataRecordList,SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        //dataStr中的英文替换成中文
        dataStr = englishToChinese(dataStr);
        // 将不存在的文件写入error-log.txt
        try(OutputStreamWriter errOp = new OutputStreamWriter(new FileOutputStream(errlogFile));
            OutputStreamWriter dataOp = new OutputStreamWriter(new FileOutputStream(dataFileTxt));
            FileOutputStream fos = new FileOutputStream(new File(zipPathFileName));){
            if(StringUtils.isNotEmpty(errLog)){
                errOp.write(errLog);
                errOp.flush();
                needZipFileList.add(errlogFile);
            }
            dataOp.write(dataStr);
            dataOp.flush();
            needZipFileList.add(dataFileTxt);
            // 将数据和文件一起打成zip包到src
            ZipUtils.toZip(needZipFileList,fos);
        }catch (Exception e){
            log.error("将数据和文件信息写入文件出错，原因【{}】",e);
        }
        //返回压缩好的文件地址
        return zipFileName;
    }

    /**
     * 将返回数据的英文转中文
     * @param dataStr
     * @return
     */
    private String englishToChinese(String dataStr){
        String resultStr = dataStr;
        for(Map.Entry<String,String> entry:JSON_CONVERSION_MAP.entrySet()){
            resultStr = resultStr.replaceAll(entry.getKey(),entry.getValue());
        }
        return resultStr;
    }


}
