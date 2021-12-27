package com.snf.dsds.dao;

import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.bean.SearchType;

import java.util.List;

public interface DataSearchDao {

    List<SearchType> getSearchTypeList();

    List<SearchParameter> querySearchParamBySearchType(Integer searchType);


}
