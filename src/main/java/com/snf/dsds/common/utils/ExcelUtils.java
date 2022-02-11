package com.snf.dsds.common.utils;

import com.snf.dsds.common.Exception.CtdException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-26 16:35
 **/
@Slf4j
public class ExcelUtils {
    /**
     * 从第rowNum行开始解析excel
     * @param rowNum
     * @param inputStream
     * @return
     */
    public static List<List<Object>> excelToShopIdList(int rowNum,InputStream inputStream) {
        //解析数据集
        List<List<Object>> list = new ArrayList<>();
        //每行数据
        List<Object> rowList;
        //空行记录map
        Map<Integer, List<Integer>> errRolMap = new HashMap<>();
        //空列记录list
        List<Integer> errColList ;
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
            //工作表对象
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int rowLength = sheet.getLastRowNum()+ 1 ;
            log.debug("总行数有多少行"+rowLength);
            //工作表的列
            Row row = sheet.getRow(0);

            //总列数
            int colLength = row.getLastCellNum();
            log.debug("总列数有多少列"+colLength);
            //得到指定的单元格
            Cell cell = row.getCell(0);
            for (int i = rowNum - 1; i < rowLength; i++) {
                rowList = new ArrayList<>();
                errColList = new ArrayList<>();
                row = sheet.getRow(i);
                for (int j = 0; j < colLength; j++) {
                    cell = row.getCell(j);

                    if (cell == null || cell.getCellType() == CellType.BLANK.getCode()) {
                        errColList.add(j+1);
                    }else {//记录空的列
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        rowList.add(data);
                    }
                }
                if(!CollectionUtils.isEmpty(errColList) && errColList.size() != 17){//记录空行
                    errRolMap.put(i+1,errColList);
                }
                if(CollectionUtils.isEmpty(rowList)){
                    continue;
                }
                list.add(rowList);
            }
        } catch (Exception e) {
            log.error("解析excel出现错误");
            throw new CtdException("解析excel出现错误");
        }
        if(!org.springframework.util.CollectionUtils.isEmpty(errRolMap)){
            String message = "导入数据中";
            for( Map.Entry<Integer,List<Integer>> entry:errRolMap.entrySet()){
                message += "第" +entry.getKey() +"行的第"+Arrays.toString(entry.getValue().toArray())+"列;";
            }
            message+="数据为空，请检查或联系管理员！";
            throw new CtdException(message);
        }
        return list ;
    }

    public static void main(String[] args) throws FileNotFoundException {
//        InputStream inputStream = new FileInputStream(new File("E:/test.xls"));
//        System.out.println(excelToShopIdList(2,inputStream));
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1640311345L)));
    }

    //解析Excel日期格式
    public static String ExcelDoubleToFormatDate(String strDate,String format) {
        if(StringUtils.isEmpty(format)){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date tDate = DoubleToDate(Double.parseDouble(strDate));
            return sdf.format(tDate);
        }catch (Exception e){
            log.error("excel日期转换时间出现错误，原因【{}】",e.getMessage());
            throw new CtdException("导入时间转换错误，请检查");
        }
    }

    //解析Excel日期格式
    public static Date DoubleToDate(Double dVal) {
        Date tDate = new Date();
        BigDecimal bigDVal = new BigDecimal(dVal);
        BigDecimal num = new BigDecimal(25569);
        tDate.setTime(bigDVal.subtract(num).multiply(new BigDecimal(24 )).multiply(new BigDecimal(3600))
                .multiply(new BigDecimal(1000)).add(new BigDecimal(tDate.getTimezoneOffset())
                        .multiply(new BigDecimal(60000))).longValue());
        return tDate;
    }

    public static Long ExcelDoubleToTimestamp(String strDate){
        try {
            Date tDate = DoubleToDate(Double.parseDouble(strDate));
            return tDate.getTime();
        }catch (Exception e){
            log.error("excel日期转换时间戳出现错误，原因【{}】",e.getMessage());
            throw new CtdException("导入时间转换错误，请检查");
        }
    }
}
