package com.easypan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easypan.entity.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.po.FileInfo;
import com.easypan.service.FileInfoService;
import com.easypan.mapper.FileInfoMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>
    implements FileInfoService {
    @Resource
    private AppConfig appConfig;
    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    public void downloadFile(String fileId, HttpServletRequest request,HttpServletResponse response ) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo==null){
            return;
        }
        try {
            String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            String fileName = fileInfo.getFileName();
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream");

            RandomAccessFile raf = new RandomAccessFile(filePath, "r");
            long fileSize = raf.length();
            long startByte = 0;
            long endByte = fileSize - 1;
            long start = 0;
            long end = 0;
            String range = request.getHeader("Range");

            if (range != null) {
                startByte = Long.parseLong(range.split("=")[1].split("-")[0]);
                endByte = Long.parseLong(range.split("=")[1].split("-")[1]);
                start = startByte;
                end = endByte;
                raf.seek(start);
                byte[] buf = new byte[(int) (end - start + 1)];
                int read = raf.read(buf);
                response.setStatus(206);
                response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + fileSize);
                raf.close();
                response.getOutputStream().write(buf, 0, read);
                return;
            } else {
                raf.seek(0);
                byte[] buf = new byte[(int) fileSize];
                raf.read(buf);
                raf.close();
                response.setStatus(200);
                response.setContentLength((int) fileSize);
                response.getOutputStream().write(buf);
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readFile(HttpServletResponse response, String filePath) {
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
//            logger.error("读取文件异常", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
//                    logger.error("IO异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
//                    logger.error("IO异常", e);
                }
            }
        }
    }

}




