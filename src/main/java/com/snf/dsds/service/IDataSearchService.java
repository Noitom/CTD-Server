package com.snf.dsds.service;

import com.snf.dsds.bean.SearchType;

import java.util.List;
import java.util.Map;

public interface IDataSearchService {

    List<SearchType> getSearchTypeList();

    Map<String,Long> getDataMap(Integer searchType);

}
