package com.snf.dsds.dao;

import com.snf.dsds.bean.CtdDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname CtdDetailDao
 * @Description TODO
 * @Date 2022/1/21 11:46
 * @Created by admin
 */
public interface CtdDetailDao {

    int batchInsert(@Param("ctdDetailList") List<CtdDetail> ctdDetailList);

    int deleteData(String fileName);

    int deleteDatas(@Param("fileNames") List<String> fileNames);

    List<CtdDetail> queryCtdDetails(String fileName);
}
