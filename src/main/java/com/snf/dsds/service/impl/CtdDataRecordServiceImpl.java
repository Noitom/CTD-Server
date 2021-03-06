package com.snf.dsds.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.snf.dsds.bean.CtdDataRecord;
import com.snf.dsds.bean.CtdDetail;
import com.snf.dsds.bean.SearchParameter;
import com.snf.dsds.common.Exception.CtdException;
import com.snf.dsds.common.projectEnum.SearchTypeEnum;
import com.snf.dsds.common.utils.ZipUtils;
import com.snf.dsds.dao.CtdDataRecordsDao;
import com.snf.dsds.dao.CtdDetailDao;
import com.snf.dsds.dao.DataSearchDao;
import com.snf.dsds.service.ICtdDataRecordsService;
import com.snf.dsds.service.IDataSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-27 10:18
 **/
@Slf4j
@Service
public class CtdDataRecordServiceImpl implements ICtdDataRecordsService {

    @Value("${ctd.download.path}")
    private String downloadPath;

    @Value("${ctd.upload.path}")
    private String uploadPath;

    @Value("${ctd.zip.filename:ctd-data.zip}")
    private String zipFileName;

    @Value("${ctd.zip.errlogFileName:error-log.txt}")
    private String errlogFileName;

    @Value("${ctd.zip.dataFileName:data.txt}")
    private String dataFileName;

    private static final String FORMAT_DATE = "yyyy-MM-dd";

    private  Map<String,String> JSON_CONVERSION_MAP = ImmutableMap.<String, String>builder()
            .put("dataStatusName","ε€ηηΆζ")
            .put("devTypeName","θ?Ύε€η±»ε")
            .put("startTimeStr","εΌε§ζΆι΄ε­η¬¦εΌ")
            .put("finishTimeStr","η»ζζΆι΄ε­η¬¦εΌ")
            .put("platformTypeName","εΉ³ε°η±»ε")
            .put("dataSetSn","ζ°ζ?ιηΌε·")
            .put("voyageNumber","θͺζ¬‘ηΌε·")
            .put("shipName","η§θθΉθΆ")
            .put("platformType","εΉ³ε°η±»ε")
            .put("platformName","εΉ³ε°εη§°")
            .put("stationNum","η«δ½εη§°")
            .put("startTime","εΌε§ζΆι΄")
            .put("finishTime","η»ζζΆι΄")
            .put("diveNum","ζ½ζ¬‘εΊε·")
            .put("longitudeLayout","εΈζΎη»εΊ¦")
            .put("latitudeLayout","εΈζΎηΊ¬εΊ¦")
            .put("depthLayout","εΈζΎζ·±εΊ¦")
            .put("longitudeWork","ε·₯δ½η»εΊ¦")
            .put("latitudeWork","ε·₯δ½ηΊ¬εΊ¦")
            .put("depthWork","ε·₯δ½ζ·±εΊ¦")
            .put("devModel","θ?Ύε€εε·")
            .put("devSn","θ?Ύε€εΊεε·")
            .put("dataFormat","ζ°ζ?ζ ΌεΌ")
            .put("dataStatus","ε€ηηΆζ")
            .put("dataFileName","εε§ζ°ζ?ζδ»Άεη§°")
            .put("devType","θ?Ύε€η±»ε")
            .put("delFlag","ε ι€ζ εΏ")
            .put("dataExist","ζ°ζ?ζδ»Άε­ε¨ζ εΏ")
            .build();

    @Autowired
    CtdDataRecordsDao ctdDataRecordsDao;

    @Autowired
    DataSearchDao dataSearchDao;

    @Autowired
    IDataSearchService dataSearchService;

    @Autowired
    CtdDetailDao ctdDetailDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addExcelData(List<CtdDataRecord> list,List<String> noExistVoyageNums) throws SQLIntegrityConstraintViolationException, JsonProcessingException {
        // ζ ‘ιͺζ°ζ?ηΌε·ζ―ε¦ε·²η»ε­ε¨
        List<String> snExistList = ctdDataRecordsDao.checkSnExist(list);
        String msg;
        if(!CollectionUtils.isEmpty(snExistList)){
            msg = "ε―Όε₯ζ°ζ?δΈ­οΌζ°ζ?ηΌε·δΈΊ"+new ObjectMapper().writeValueAsString(snExistList)+"ηζ°ζ?ε·²η»ε­ε¨οΌθ―·ζ£ζ₯ζθη³»η?‘ηεοΌ";
            msg = msg.replaceAll("\"","'");
            throw new CtdException(msg);
        }
        List<String> dataFileExistList = ctdDataRecordsDao.checkDataFileExist(list);
        if(!CollectionUtils.isEmpty(dataFileExistList)){
            msg = "ε―Όε₯ζ°ζ?δΈ­οΌζ°ζ?ζδ»Άεη§°δΈΊ"+new ObjectMapper().writeValueAsString(dataFileExistList)+"ηθ?°ε½ε·²η»ε­ε¨οΌθ―·ζ£ζ₯ζθη³»η?‘ηεοΌ";
            msg = msg.replaceAll("\"","'");
            throw new CtdException(msg);
        }
        if (!CollectionUtils.isEmpty(noExistVoyageNums)){
            //ζ·»ε θͺζ¬‘ηΌε·
            dataSearchDao.batchInsertVoyageNums(noExistVoyageNums);
        }
        // ε°ζδ»ΆδΏε­ε―Όζ¬ε°ε°ζ°ζ?δΏε­ζ°ζ?ε°ζ°ζ?εΊ
        if(ctdDataRecordsDao.batchInsert(list) == 0){
            throw new CtdException("ζ²‘ζζ·»ε ζ°ζ?ε°ζ°ζ?εΊοΌθ―·θη³»η?‘ηεοΌ");
        }
    }

    @Override
    public void addCtdDataRecord(CtdDataRecord ctdDataRecord) {
        CtdDataRecord oldCtdData = ctdDataRecordsDao.ctdDataExist(ctdDataRecord.getDataSetSn());
        if(oldCtdData != null && !oldCtdData.getDelFlag()){
            throw new CtdException("ζ°ζ?ε·²η»ε­ε¨οΌθ―·εΏιε€ζ·»ε οΌ");
        }
        if(oldCtdData != null && StringUtils.isNotEmpty(oldCtdData.getDataSetSn()) && oldCtdData.getDelFlag()){
            ctdDataRecord.setDelFlag(false);
            ctdDataRecordsDao.updateCtdDataRecord(ctdDataRecord);
        }else{
            if(ctdDataRecordsDao.addCtdDataRecord(ctdDataRecord) != 1){
                throw new CtdException("ζ²‘ζζ·»ε ζ°ζ?ε°ζ°ζ?εΊοΌθ―·θη³»η?‘ηεοΌ");
            }
        }
    }

    @Override
    public List<CtdDataRecord>  queryListByParam(SearchParameter searchParameter) {
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryListBySearchParameter(searchParameter);
        return ctdDataRecordList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCtdDataRecord(CtdDataRecord ctdDataRecord) {
        // ε€ζ­ζδ»Άεζ―ε¦δΏ?ζΉοΌδΏ?ζΉηθ―ε§ζ°ζ?ζδ»Άζ εΏθ?Ύη½?δΈΊ0
        CtdDataRecord oleData = ctdDataRecordsDao.ctdDataExist(ctdDataRecord.getDataSetSn());
        if(!StringUtils.equals(oleData.getDataFileName(),ctdDataRecord.getDataFileName())){
            ctdDataRecord.setDataExist(false);
            //ζζδ»Άεζ°ζ?ε ι€
            String fileName = oleData.getDataFileName();
            ctdDetailDao.deleteData(fileName);
            String path = uploadPath + ctdDataRecord.getVoyageNumber();
            File file = new File(path,fileName);
            if(file.exists()){
                file.delete();
            }

        }
        if(ctdDataRecordsDao.updateCtdDataRecord(ctdDataRecord)!=1){
            throw new CtdException("θ―₯ζ°ζ?δΈε­ε¨οΌδΈθ½δΏ?ζΉοΌ");
        }
    }

    @Override
    public boolean checkFileExist(String voyageNumber, String fileName) {
        Boolean flag = ctdDataRecordsDao.queryFileExist(voyageNumber, fileName);
        if(flag == null){
            throw new CtdException("ζ°ζ?εΊζ²‘ζε―ΉεΊζ°ζ?οΌδΈεθ?ΈδΈδΌ οΌ");
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCtdDetail(List<CtdDetail> ctdDetails,String fileName, Boolean exist) {
        List<CtdDetail> ctdDetailList = new ArrayList<>();
        for(int i=0;i<ctdDetails.size();i++){
            ctdDetailList.add(ctdDetails.get(i));
            if ((i != 0 && i % 10000 == 0) || i == ctdDetails.size()-1){
                ctdDetailDao.batchInsert(ctdDetailList);
                ctdDetailList.clear();
            }
        }
//        ctdDetailDao.batchInsert(ctdDetails);
        ctdDataRecordsDao.updateExist(fileName,exist);
    }

    @Override
    public void deleteCtdDataRecord(String dataSetSn) {
        if(ctdDataRecordsDao.delete(dataSetSn) != 1){
            throw new CtdException("θ―₯ζ°ζ?δΈε­ε¨οΌδΈθ½ε ι€οΌ");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCtdDataRecords(String[] dataSetSns) {
        // ε ι€ζ°ζ?εζδ»Ά
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryDataBySns(dataSetSns);
        if(CollectionUtils.isEmpty(ctdDataRecordList)){
            throw new CtdException("ζ°ζ?δΈε­ε¨οΌδΈθ½ε ι€οΌ");
        }
        //εε ι€ζ°ζ?οΌε¨ε ι€ζδ»Ά
        ctdDataRecordsDao.deletes(dataSetSns);
        //ε ι€ctdDetailζ°ζ?
        List<String> fileNames = new ArrayList<>();
        for(CtdDataRecord dataRecord:ctdDataRecordList){
            if (dataRecord.getDataExist()){
                fileNames.add(dataRecord.getDataFileName());
            }
        }
        ctdDetailDao.deleteDatas(fileNames);
        //ε ι€ζζηζδ»Ά
        String filepath;
        String filename;
        for (CtdDataRecord ctdDataRecord:ctdDataRecordList){
            filepath = uploadPath + ctdDataRecord.getVoyageNumber();
            filename = ctdDataRecord.getDataFileName();
            File file = new File(filepath,filename);
            if(file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public String queryAndZipData(String[] dataSetSns) throws JsonProcessingException {
        // δ»ζ°ζ?εΊζ₯θ―’ζ°ζ?
        List<CtdDataRecord> ctdDataRecordList = ctdDataRecordsDao.queryDataBySns(dataSetSns);
        //θ?°ε½δΈε­ε¨ηζ°ζ?List
        List<String> dataNotExistList = new ArrayList<>(Arrays.asList(dataSetSns));
        //θ?°ε½δΈε­ε¨ηζδ»ΆList
        List<String> fileNotExistList = new ArrayList<>();
        // δΏε­θ¦ζεηζδ»Ά
        List<File> needZipFileList = new ArrayList<>();
        String downloadCtdPath = downloadPath+"ctd-data";
        File downloadCtdDir = new File(downloadCtdPath);
        if(!downloadCtdDir.exists()){
            downloadCtdDir.mkdirs();
        }
        File errlogFile = new File(downloadCtdPath,errlogFileName);
        File dataFileTxt = new File(downloadCtdPath,dataFileName);

        String dataFilePath;
        String dataFileName;
        //θ·ε
        Map<Long,String> searchParamTypeMap = dataSearchService.getIdStrMap(SearchTypeEnum.SEARCH_TYPE_DEVTYPE.getValue(),
                SearchTypeEnum.SEARCH_TYPE_PLATFORM.getValue(),SearchTypeEnum.SEARCH_TYPE_DATASTATUS.getValue());
        for(CtdDataRecord ctdDataRecord:ctdDataRecordList){
            //ε°ζ°ζ?ε­ε¨ηζ°ζ?ιηΌε·η§»ι€list
            dataNotExistList.remove(ctdDataRecord.getDataSetSn());
            dataFilePath = uploadPath + ctdDataRecord.getVoyageNumber();
            dataFileName = ctdDataRecord.getDataFileName();
            File dataFile = new File(dataFilePath,dataFileName);
            //θ?Ύη½?ζ°ζ?ηεΌ
            ctdDataRecord.setDevTypeName(searchParamTypeMap.get(ctdDataRecord.getDevType()));
            ctdDataRecord.setDevType(null);
            ctdDataRecord.setPlatformTypeName(searchParamTypeMap.get(ctdDataRecord.getPlatformType()));
            ctdDataRecord.setPlatformType(null);
            ctdDataRecord.setDataStatusName(searchParamTypeMap.get(ctdDataRecord.getDataStatus()));
            ctdDataRecord.setDataStatus(null);
            ctdDataRecord.setStartTimeStr(DateFormatUtils.format(ctdDataRecord.getStartTime()*1000,FORMAT_DATE));
            ctdDataRecord.setFinishTimeStr(DateFormatUtils.format(ctdDataRecord.getFinishTime()*1000,FORMAT_DATE));
            //ζ ‘ιͺζδ»Άζ―ε¦ε­ε¨
            if(!dataFile.exists()){
                //ε°ζδ»ΆδΈε­ε¨ηζ°ζ?ιηΌε·ζ·»ε ε°list
                fileNotExistList.add(ctdDataRecord.getDataSetSn());
                continue;
            }
            //ε°ζδ»ΆδΏε­θ΅·ζ₯οΌζΉδΎΏζzipεηΌ©ε
            needZipFileList.add(dataFile);
        }
        String errLog = "";
        if(!CollectionUtils.isEmpty(dataNotExistList)){
            errLog += "ζ°ζ?εΊδΈε­ε¨δ»₯δΈζ°ζ?ιηΌε·ηζ°ζ?οΌ" + Arrays.toString(dataNotExistList.toArray()) + ";\r\n";
        }
        if(!CollectionUtils.isEmpty(fileNotExistList)){
            errLog += "η³»η»δΈε­ε¨δ»₯δΈζ°ζ?ιηΌε·ηζ°ζ?ζδ»ΆοΌ" + Arrays.toString(fileNotExistList.toArray()) + ";\r\n";
        }
        //ζ ‘ιͺδΈθ½½ζδ»Άε€Ήζ―ε¦ε­ε¨
        File downloadDir = new File(downloadPath);
        if(!downloadDir.exists()){
            downloadDir.mkdirs();
        }
        String zipPathFileName = downloadPath + zipFileName;

        String dataStr = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ctdDataRecordList);
        //dataStrδΈ­ηθ±ζζΏζ’ζδΈ­ζ
        dataStr = englishToChinese(dataStr);
        // ε°δΈε­ε¨ηζδ»Άεε₯error-log.txt
        try(OutputStreamWriter errOp = new OutputStreamWriter(new FileOutputStream(errlogFile));
            OutputStreamWriter dataOp = new OutputStreamWriter(new FileOutputStream(dataFileTxt));
            FileOutputStream fos = new FileOutputStream(new File(zipPathFileName))){
            if(StringUtils.isNotEmpty(errLog)){
                errOp.write(errLog);
                errOp.flush();
            }else{
                errOp.close();
                errlogFile.delete();
            }
            dataOp.write(dataStr);
            dataOp.flush();
            // ε―ΌεΊζ°ζ?ζδ»Ά
            for(File file:needZipFileList){
                //ε°ζδ»Άε€εΆε°δΈθ½½η?ε½δΈ
                FileUtils.copyFileToDirectory(file,downloadCtdDir);
            }
            // ε°ζδ»Άε€Ήζζzipε
            ZipUtils.toZip(downloadCtdPath,fos,true);
        }catch (Exception e){
            log.error("ε°ζ°ζ?εζδ»ΆδΏ‘ζ―εε₯ζδ»ΆεΊιοΌεε γ{}γ",e);
        }
        //θΏεεηΌ©ε₯½ηζδ»Άε°ε
        return zipFileName;
    }

    @Override
    public List<CtdDataRecord> requestHistory() {
        return ctdDataRecordsDao.requestHistory();
    }

    /**
     * ε°θΏεζ°ζ?ηθ±ζθ½¬δΈ­ζ,ε°ε€δ½ηζ°ζ?ζΈι€
     * @param dataStr
     * @return
     */
    private String englishToChinese(String dataStr){
        String resultStr = dataStr;
        for(Map.Entry<String,String> entry:JSON_CONVERSION_MAP.entrySet()){
            resultStr = resultStr.replaceAll(entry.getKey(),entry.getValue());
        }
        //ε§εεΌε·ειε·ε»ζεεε·δΈ€θΎΉηη©Ίζ Όε»ζ
        resultStr = resultStr.replaceAll("\"","").replaceAll(",","").replaceAll(" : ",":");
        return resultStr;
    }

    public List<CtdDetail> getCtdDetails(String fileName){
        List<CtdDetail> ctdDetails = ctdDetailDao.queryCtdDetails(fileName);
        if(CollectionUtils.isEmpty(ctdDetails)){
            throw new CtdException("ζ²‘ζζ₯ε°ηΈεΊζ°ζ?");
        }
        ctdDetails.remove(0);
        return ctdDetails;
    }


}
