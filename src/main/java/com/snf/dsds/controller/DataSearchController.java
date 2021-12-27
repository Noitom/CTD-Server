package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.service.IDataSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dsds
 * @description: 数据检索Controller
 * @author: zhouyuj
 * @create: 2021-12-24 10:11
 **/
@RestController
public class DataSearchController {

    @Autowired
    IDataSearchService dataSearchService;

    @RequestMapping(value = "/requestSearchType",method = RequestMethod.GET)
    public RespBean getSearchTypeList(){
        return RespBean.ok("获取查询类型成功",dataSearchService.getSearchTypeList());
    }


}
