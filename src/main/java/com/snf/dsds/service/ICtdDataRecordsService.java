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
}
