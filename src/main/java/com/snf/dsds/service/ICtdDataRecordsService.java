package com.snf.dsds.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.CtdDetail;
import com.snf.dsds.bean.SearchParameter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface ICtdDataRecordsService {

    /**
     * 将上传的excel保存导本地将数据批量导入数据库
     */
    void addExcelData(List<CtdDataRecord> list,List<String> noExistVoyageNums) throws SQLIntegrityConstraintViolationException, JsonProcessingException;

    void addCtdDataRecord(CtdDataRecord ctdDataRecord);

    List<CtdDataRecord> queryListByParam(SearchParameter searchParameter);

    void updateCtdDataRecord(CtdDataRecord ctdDataRecord);

    /**
     * 校验航次编号对应的二进制文件是否已经上传
     */
    boolean checkFileExist(String voyageNumber,String fileName);

    void saveCtdDetail(List<CtdDetail> ctdDetails, String fileName, Boolean exist);

    void deleteCtdDataRecord(String dataSetSn);

    void deleteCtdDataRecords(String[] dataSetSns);

    String queryAndZipData(String[] dataSetSns) throws IOException;

    List<CtdDataRecord> requestHistory();

    List<CtdDetail> getCtdDetails(String fileName);
}
