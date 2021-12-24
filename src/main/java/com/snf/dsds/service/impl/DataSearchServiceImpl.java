package com.snf.dsds.service.impl;

import com.snf.dsds.bean.SearchType;
import com.snf.dsds.dao.DataSearchDao;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
