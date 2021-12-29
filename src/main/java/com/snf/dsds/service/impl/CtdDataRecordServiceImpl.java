package com.snf.dsds.service.impl;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.dao.CtdDataRecordsDao;
import com.snf.dsds.service.ICtdDataRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
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


}
