package com.snf.dsds.controller;

import com.snf.dsds.bean.RespBean;
import com.snf.dsds.common.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-24 17:32
 **/
@Slf4j
@RestController
public class FileController {

    @PostMapping("/uploadFile")
    public RespBean uploadFile(@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传数据接口");
        String fileName = multipartFile.getOriginalFilename();
        String path =  "E:/file/"+fileName;
        File file = new File(path);
        try{
            multipartFile.transferTo(file);
        }catch (Exception e){
            log.error(e.getMessage());
            return RespBean.error("上传失败");
        }
        return RespBean.ok("上传成功");
    }

    @PostMapping("/uploadExcel")
    public RespBean uploadExcel(@RequestParam("file") MultipartFile multipartFile){
        log.info("进入上传excel接口");
        try{
            ExcelUtils.excelToShopIdList(multipartFile.getInputStream());
        }catch (Exception e){
            log.error("上传失败【{}】",e.getMessage());
            return RespBean.error("上传失败");
        }
        return RespBean.ok("上传成功");
    }
}
