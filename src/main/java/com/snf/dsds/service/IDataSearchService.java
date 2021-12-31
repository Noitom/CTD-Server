package com.snf.dsds.service;

import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.bean.SearchType;

import java.util.List;
import java.util.Map;

public interface IDataSearchService {

    List<SearchType> getSearchTypeList();

    Map<String,Long> getStrIdMap(Integer... searchType);

    Map<Long,String> getIdStrMap(Integer... searchType);

    Long addSearchParameter(SearchParameter searchParameter);

    void deletdSearchParameter(Long id);

    void updateSearchParameter(SearchParameter searchParameter);

}
