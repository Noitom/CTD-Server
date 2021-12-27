package com.snf.dsds.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-26 16:35
 **/
public class ExcelUtils {
    public static List<Object> excelToShopIdList(InputStream inputStream) {
        List<Object> list = new ArrayList<>();
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
            //工作表对象
            Sheet sheet = workbook.getSheetAt(0);
            //总行数
            int rowLength = sheet.getLastRowNum() -1;
            System.out.println("总行数有多少行"+rowLength);
            //工作表的列
            Row row = sheet.getRow(0);

            //总列数
            int colLength = row.getLastCellNum();
            System.out.println("总列数有多少列"+colLength);
            //得到指定的单元格
            Cell cell = row.getCell(0);
            for (int i = 0; i < rowLength; i++) {
                row = sheet.getRow(i);
                for (int j = 0; j < colLength+1; j++) {
                    cell = row.getCell(j);
//                     System.out.println(cell);
                    if (cell != null) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        String data = cell.getStringCellValue();
                        data = data.trim();
                        System.out.print(data);
                        //判断data是否是数字
                        if (StringUtils.isNumeric(data)){
                            list.add(Integer.parseInt(data));
                        }
                        System.out.print("\t");
                    }
                }
                System.out.print("\r\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list ;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(new File("E:/test.xls"));
        excelToShopIdList(inputStream);
    }
}
