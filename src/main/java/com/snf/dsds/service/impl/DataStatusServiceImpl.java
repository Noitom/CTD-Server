package com.snf.dsds.service.impl;

import com.snf.dsds.bean.DataStatus;
import com.snf.dsds.dao.DataStatusDao;
import com.snf.dsds.service.IDataSearchService;
import com.snf.dsds.service.IDataStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 16:23
 **/
@Service
public class DataStatusServiceImpl implements IDataStatusService {

    @Autowired
    DataStatusDao dataStatusDao;

    @Override
    public Map<String,Long> getDataStatusMap() {
        List<DataStatus> dataStatuses = dataStatusDao.quertList();
        Map<String,Long> resultMap = new HashMap<>();
        for(DataStatus dataStatus:dataStatuses){
            resultMap.put(dataStatus.getStatus(),dataStatus.getId());
        }
        return resultMap;
    }

}
