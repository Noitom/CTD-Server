package com.snf.dsds.service.impl;

import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.bean.SearchType;
import com.snf.dsds.dao.DataSearchDao;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-24 10:14
 **/
@Slf4j
@Service
public class DataSearchServiceImpl implements IDataSearchService {

    @Autowired
    DataSearchDao dataSearchDao;

    @Override
    public List<SearchType> getSearchTypeList() {
        return dataSearchDao.getSearchTypeList();
    }

    @Override
    public Map<String, Long> getStrIdMap(Integer... searchType) {
        List<SearchParameter> searchParameters = dataSearchDao.querySearchParamBySearchType(searchType);
        Map<String,Long> resultMap = new HashMap<>();
        for(SearchParameter searchParameter:searchParameters){
            resultMap.put(searchParameter.getName(),searchParameter.getId());
        }
        return resultMap;
    }

    @Override
    public Map<Long,String> getIdStrMap(Integer... searchType) {
        List<SearchParameter> searchParameters = dataSearchDao.querySearchParamBySearchType(searchType);
        Map<Long,String> resultMap = new HashMap<>();
        for(SearchParameter searchParameter:searchParameters){
            resultMap.put(searchParameter.getId(),searchParameter.getName());
        }
        return resultMap;
    }

}
