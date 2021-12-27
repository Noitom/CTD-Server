package com.snf.dsds.dao;

import com.snf.dsds.bean.CtdDataRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 10:33
 **/
public interface CtdDataRecordsDao {

    int batchInsert(@Param("list") List<CtdDataRecord> list);
}
