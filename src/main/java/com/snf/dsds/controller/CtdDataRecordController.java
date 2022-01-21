package com.snf.dsds.controller;

import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.CtdDetail;
import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.projectEnum.SearchTypeEnum;
import com.snf.dsds.service.ICtdDataRecordsService;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
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
    @PostMapping("addCtdDataRecord")
    public RespBean addCtdDataRecord(@RequestBody  CtdDataRecord ctdDataRecord){
        log.info("进入增加ctd数据接口");
        try{
            ctdDataRecordsService.addCtdDataRecord(ctdDataRecord);
        }catch (CtdException e){
            log.error("出现业务异常，原因【{}】",e);
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("添加ctd数据出现错误，原因【{}】",e);
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
                return RespBean.error("Ctd数据文件名称重复，请检查！");
            }
            return RespBean.error("添加数据出现错误，请联系管理员！");
        }
        return RespBean.ok("添加成功");
    }

    /**
     * 查询ctd接口
     * @param searchParameter
     * @return
     */
    @PostMapping("requestCtdRecords")
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
    @RequestMapping(value = "updateCtdDataRecord",method = RequestMethod.POST)
    public RespBean updateCtdDataRecord(@RequestBody  CtdDataRecord ctdDataRecord){
        log.info("进入更新ctd数据接口");
        try{
            ctdDataRecord.setDelFlag(null);
            ctdDataRecordsService.updateCtdDataRecord(ctdDataRecord);
        }catch (CtdException e){
            log.error("更新ctd数据出现错误,原因【{}】",e);
            return RespBean.error(e.getMessage());
        }
        catch (Exception e){
            log.error("更新ctd数据出现错误，原因【{}】",e);
            if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
                return RespBean.error("Ctd数据文件名称重复，请检查！");
            }
            return RespBean.error("修改数据出现错误，请联系管理员！");
        }
        return RespBean.ok("修改成功");
    }

    /**
     * 删除ctd数据
     */
    @RequestMapping(value = "deleteCtdDataRecords",method = RequestMethod.POST)
    public RespBean deleteCtdDataRecord(@RequestBody String[] dataSetSns){
        log.info("进入删除ctd数据接口");
        try{
            ctdDataRecordsService.deleteCtdDataRecords(dataSetSns);
            return RespBean.ok("删除成功");
        }catch (CtdException e){
            log.error("出现功能错误，原因【{}】",e);
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("出现系统错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @GetMapping("requestHistory")
    public RespBean requestHistory(){
        return RespBean.ok("获取成功",ctdDataRecordsService.requestHistory());
    }

    @PostMapping("/requestCtdDetails")
    public RespBean requestCtdDetails(@RequestBody CtdDetail ctdDetail){
        Long start = System.currentTimeMillis();
        List<CtdDetail> ctdDetails = null;
        try{
            ctdDetails = ctdDataRecordsService.getCtdDetails(ctdDetail.getFileName());
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("出现系统错误，原因【{}】",e);
            return RespBean.error("查询出现错误，请联系管理员！");
        }
        log.info("查询耗时【{}毫秒】",System.currentTimeMillis()-start);
        return RespBean.ok("查询成功",ctdDetails);
    }


}
