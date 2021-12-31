package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: dsds
 * @description: 数据检索Controller
 * @author: zhouyuj
 * @create: 2021-12-24 10:11
 **/
@Slf4j
@RestController
public class DataSearchController {

    @Autowired
    IDataSearchService dataSearchService;

    @RequestMapping(value = "/requestSearchParameter",method = RequestMethod.GET)
    public RespBean getSearchTypeList(){
        return RespBean.ok("获取查询类型成功",dataSearchService.getSearchTypeList());
    }

    @PostMapping("addSearchParameter")
    public RespBean addSearchParameter(@RequestBody SearchParameter searchParameter){
        try{
            Long id = dataSearchService.addSearchParameter(searchParameter);
            return RespBean.ok("添加成功",id);
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }
        catch (Exception e){
            log.error("添加查询参数出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @PostMapping("deleteSearchParameter")
    public RespBean deleteSearchParameter(@RequestBody SearchParameter searchParameter){
        try{
            dataSearchService.deletdSearchParameter(searchParameter.getId());
            return RespBean.ok("删除成功");
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("删除查询参数出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

    @PostMapping("updateSearchParameter")
    public RespBean updateSearchParameter(@RequestBody SearchParameter searchParameter){
        try{
            dataSearchService.updateSearchParameter(searchParameter);
            return RespBean.ok("修改成功");
        }catch (CtdException e){
            return RespBean.error(e.getMessage());
        }catch (Exception e){
            log.error("修改查询参数出现错误，原因【{}】",e);
            return RespBean.error("出现系统错误，请联系管理员！");
        }
    }

}
