package com.snf.dsds.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;


/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-29 10:57
 **/
@Slf4j
public class ZipFileUtils {

    /**
     * 压缩目录到磁盘zip
     * @throws ZipException
     */
    public static void zipFile() throws ZipException {
        // 生成的压缩文件
        ZipFile zipFile = new ZipFile("C:\\Users\\yckj2494\\Desktop\\a.zip");
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
        // 压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
        // 要打包的文件夹
        File currentFile = new File("C:\\Users\\yckj2494\\Desktop\\20210224160154710");
        File[] fs = currentFile.listFiles();
        // 遍历test文件夹下所有的文件、文件夹
        for (File f : fs) {
            if (f.isDirectory()) {
                zipFile.addFolder(f.getPath(), parameters);
            } else {
                zipFile.addFile(f, parameters);
            }
        }
    }

    /**
     * http响应zip
     * @param srcDir
     * @param out
     * @throws ZipException
     * @throws IOException
     */
    public static void zipFile(String srcDir, OutputStream out ) throws ZipException, IOException {
        final long start = System.currentTimeMillis();
        // 要打包的文件夹
        File currentFile = new File(srcDir);
        // 生成的压缩文件
        final File file = new File(srcDir + ".zip");
        ZipFile zipFile = new ZipFile(file);
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
        // 压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);

        File[] fs = currentFile.listFiles();
        // 遍历test文件夹下所有的文件、文件夹
        for (File f : fs) {
            if (f.isDirectory()) {
                zipFile.addFolder(f.getPath(), parameters);
            } else {
                zipFile.addFile(f, parameters);
            }
        }
        try( InputStream fis = new FileInputStream(zipFile.getFile())) {
            IOUtils.copy(fis, out);
        }
        file.delete();
        log.info("打包耗时={}ms",System.currentTimeMillis()-start);
    }
    /**
     * 解压zip
     * @param zipFile
     * @throws ZipException
     */
    public static void unzip(String zipFile) throws ZipException {
        long startTime = System.currentTimeMillis();
        ZipFile zipFile2 = new ZipFile(zipFile);
        //设置编码格式
        zipFile2.setFileNameCharset("gbk");
        if (!zipFile2.isValidZipFile()) {
            //可能不是zip文件，可能是rar
            throw new ZipException("文件不合法或不存在");
        }
        //检查是否需要密码
        //checkEncrypted(zipFile2);
        List<FileHeader> fileHeaderList = zipFile2.getFileHeaders();
        for (int i = 0; i < fileHeaderList.size(); i++) {
            FileHeader fileHeader = fileHeaderList.get(i);
            zipFile2.extractFile(fileHeader, "temp");
        }
        System.out.println("解压成功！");
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) + "ms");
    }

    public static void main(String[] args) throws ZipException {
        final long start = System.currentTimeMillis();
        zipFile();
        final long time = System.currentTimeMillis() - start;
        System.out.println("耗时="+time);
    }

}
