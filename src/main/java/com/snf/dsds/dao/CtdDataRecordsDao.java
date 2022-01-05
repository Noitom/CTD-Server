package com.snf.dsds.dao;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.SearchParameter;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 10:33
 **/
public interface CtdDataRecordsDao {

    int batchInsert(@Param("list") List<CtdDataRecord> list) throws SQLIntegrityConstraintViolationException;

    int addCtdDataRecord(CtdDataRecord ctdDataRecord);

    List<CtdDataRecord> queryListBySearchParameter(SearchParameter searchParameter);

    int updateCtdDataRecord(CtdDataRecord ctdDataRecord);

    Boolean queryFileExist(@Param("voyageNumber") String voyageNumber,@Param("fileName") String fileName);

    int updateExist(@Param("fileName")String fileName,@Param("exist") Boolean exist);

    int delete(String dataSetSn);

    int deletes(@Param("dataSetSns") String[] dataSetSns);

    CtdDataRecord ctdDataExist(String dataSetSn);

    List<CtdDataRecord> queryDataBySns(@Param("dataSetSns") String[] dataSetSns);

    List<String> checkSnExist(List<CtdDataRecord> list);

    List<String> checkDataFileExist(List<CtdDataRecord> list);

}
