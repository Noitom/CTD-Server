package com.snf.dsds.dao;

import com.snf.dsds.bean.PlatformType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 14:13
 **/
public interface PlatformTypeDao {

    @Select("select * from t_platform_type ")
    List<PlatformType> queryPlatformTypes();

}
