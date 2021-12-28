package com.snf.dsds.service.impl;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.dao.CtdDataRecordsDao;
import com.snf.dsds.service.ICtdDataRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void addExcelData(List<CtdDataRecord> list) {
        // 将文件保存导本地将数据保存数据到数据库
        ctdDataRecordsDao.batchInsert(list);
    }

    @Override
    public void addCtdDataRecord(CtdDataRecord ctdDataRecord) {
        ctdDataRecordsDao.addCtdDataRecord(ctdDataRecord);
    }

    @Override
    public List<CtdDataRecord>  queryListByParam(SearchParameter searchParameter) {
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryListBySearchParameter(searchParameter);
        return ctdDataRecordList;
    }
}
