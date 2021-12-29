package com.snf.dsds.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        String dataStr = JSON.toJSONString(ctdDataRecordList);
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


}
