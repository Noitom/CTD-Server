package com.snf.dsds.dao;

import com.snf.dsds.bean.SearchType;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DataSearchDao {

    List<SearchType> getSearchTypeList();


}
