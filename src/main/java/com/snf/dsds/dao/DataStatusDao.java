package com.snf.dsds.dao;

import com.snf.dsds.bean.DataStatus;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DataStatusDao {

    @Select("select * from t_ctd_data_status")
    List<DataStatus> quertList();
}
