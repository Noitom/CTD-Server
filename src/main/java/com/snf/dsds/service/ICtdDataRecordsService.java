package com.snf.dsds.service;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.SearchParameter;

import java.util.List;

public interface ICtdDataRecordsService {

    /**
     * 将上传的excel保存导本地将数据批量导入数据库
     */
    void addExcelData(List<CtdDataRecord> list);

    void addCtdDataRecord(CtdDataRecord ctdDataRecord);

    List<CtdDataRecord> queryListByParam(SearchParameter searchParameter);

    void updateCtdDataRecord(CtdDataRecord ctdDataRecord);

    /**
     * 校验航次编号对应的二进制文件是否已经上传
     */
    boolean checkFileExist(String voyageNumber,String fileName);

    void setDataExist(String fileName,Boolean exist);
}
