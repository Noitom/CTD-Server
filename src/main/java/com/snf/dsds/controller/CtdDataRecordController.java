package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.projectEnum.SearchTypeEnum;
import com.snf.dsds.service.ICtdDataRecordsService;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @Autowired
    IDataSearchService dataSearchService;

    private static final String FORMAT_DATE = "yyyy-MM-dd";

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

    /**
     * 查询ctd接口
     * @param searchParameter
     * @return
     */
    @RequestMapping("requestCtdRecords")
    public RespBean requestCtdRecords(@RequestBody SearchParameter searchParameter){
        try{
            List<CtdDataRecord> ctdDataRecords = ctdDataRecordsService.queryListByParam(searchParameter);
            Map<Long,String> searchParamTypeMap = dataSearchService.getIdStrMap(SearchTypeEnum.SEARCH_TYPE_DEVTYPE.getValue(),
                    SearchTypeEnum.SEARCH_TYPE_PLATFORM.getValue(),SearchTypeEnum.SEARCH_TYPE_DATASTATUS.getValue());
            for(CtdDataRecord ctdDataRecord: ctdDataRecords){
                ctdDataRecord.setDevTypeName(searchParamTypeMap.get(ctdDataRecord.getDevType()));
                ctdDataRecord.setPlatformTypeName(searchParamTypeMap.get(ctdDataRecord.getPlatformType()));
                ctdDataRecord.setDataStatusName(searchParamTypeMap.get(ctdDataRecord.getDataStatus()));
                ctdDataRecord.setStartTimeStr(DateFormatUtils.format(ctdDataRecord.getStartTime()*1000,FORMAT_DATE));
                ctdDataRecord.setFinishTimeStr(DateFormatUtils.format(ctdDataRecord.getFinishTime()*1000,FORMAT_DATE));
            }
            return RespBean.ok("查询成功",ctdDataRecords);
        }catch (Exception e){
            log.error("查询ctd数据出现错误，原因【{}】",e);
            return RespBean.error("查询出现错误，请联系管理员！");

        }
    }

    /**
     * 更新ctd接口
     */
    @RequestMapping("uploadCtdDataRecords")
    public RespBean uploadCtdDataRecords(@RequestBody  CtdDataRecord ctdDataRecord){
        log.info("进入更新ctd数据接口");
        try{
            ctdDataRecordsService.updateCtdDataRecord(ctdDataRecord);
        }catch (Exception e){
            log.error("更新ctd数据出现错误，原因【{}】",e);
            return RespBean.error("修改数据出现错误，请联系管理员！");
        }
        return RespBean.ok("修改成功");
    }


}
