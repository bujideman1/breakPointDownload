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

            // 获取Range请求头
            String rangeHeader = request.getHeader("Range");

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] rangeData = rangeHeader.substring(6).split("-");
                long startByte = Long.parseLong(rangeData[0]);
                long endByte = rangeData.length > 1 ? Long.parseLong(rangeData[1]) : fileSize - 1;

                if (startByte < 0 || startByte >= fileSize || endByte >= fileSize) {
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    response.setHeader("Content-Range", "bytes */" + fileSize);
                    return;
                }

                long contentLength = endByte - startByte + 1;
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + fileSize);
                response.setContentLengthLong(contentLength);

                byte[] buffer = new byte[1024];
                int bytesRead;
                long bytesRemaining = contentLength;
                raf.seek(startByte);

                while (bytesRemaining > 0) {
                    bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining));
                    if (bytesRead == -1) {
                        break;
                    }
                    response.getOutputStream().write(buffer, 0, bytesRead);
                    bytesRemaining -= bytesRead;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentLengthLong(fileSize);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = raf.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
            }

            raf.close();
        } catch (Exception e) {
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




