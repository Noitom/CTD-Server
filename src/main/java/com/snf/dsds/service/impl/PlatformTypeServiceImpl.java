package com.snf.dsds.service.impl;

import com.snf.dsds.bean.PlatformType;
import com.snf.dsds.dao.PlatformTypeDao;
import com.snf.dsds.service.IPlatformTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 14:57
 **/
@Service
public class PlatformTypeServiceImpl implements IPlatformTypeService {

    @Autowired
    private PlatformTypeDao platformTypeDao;

    @Override
    public Map<String, Long> getPlatformTypeMap() {
        List<PlatformType> platformTypes = platformTypeDao.queryPlatformTypes();
        Map<String, Long> resultMap = new HashMap<>();
        for(PlatformType platformType:platformTypes){
            resultMap.put(platformType.getName(),platformType.getId());
        }
        return resultMap;
    }
}
