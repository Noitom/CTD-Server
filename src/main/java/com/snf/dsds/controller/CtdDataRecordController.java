package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.service.ICtdDataRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-28 09:13
 **/
@Slf4j
@RestController
public class CtdDataRecordController {

    @Autowired
    ICtdDataRecordsService ctdDataRecordsService;

    /**
     * 增加ctd数据
     * @return
     */
    @RequestMapping("addCtdDataRecord")
    public RespBean addCtdDataRecord(@RequestBody  CtdDataRecord ctdDataRecord){
        log.info("进入增加ctd数据接口");
        try{
            ctdDataRecordsService.addCtdDataRecord(ctdDataRecord);
        }catch (Exception e){
            log.error("添加ctd数据出现错误，原因【{}】",e);
            return RespBean.error("添加数据出现错误，请联系管理员！");
        }
        return RespBean.ok("添加成功");
    }

    @RequestMapping("requestCtdRecords")
    public RespBean requestCtdRecords(@RequestBody SearchParameter searchParameter){
        try{
            return RespBean.ok("查询成功",ctdDataRecordsService.queryListByParam(searchParameter));
        }catch (Exception e){
            log.error("查询ctd数据出现错误，原因【{}】",e);
            return RespBean.error("查询出现错误，请联系管理员！");

        }
    }


}
