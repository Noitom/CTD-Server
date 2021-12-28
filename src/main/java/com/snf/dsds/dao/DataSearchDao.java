package com.snf.dsds.dao;

import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.bean.SearchType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataSearchDao {

    List<SearchType> getSearchTypeList();

    List<SearchParameter> querySearchParamBySearchType(@Param("searchTypes") Integer[] searchTypes);


}
