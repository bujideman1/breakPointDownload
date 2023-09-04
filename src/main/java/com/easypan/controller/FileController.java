package com.easypan.controller;

import com.easypan.entity.po.FileInfo;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileInfoService fileInfoService;
    @RequestMapping("/hello")
    public String hello(){
        return "hello World!";
    }
    @GetMapping("/download/{fileId}")
    public void downloadOceanfile(@PathVariable("fileId") String fileId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        fileInfoService.downloadFile(fileId, request, response);
    }

    @RequestMapping("/all")
    public ResponseVO all(){
        List<FileInfo> list = fileInfoService.list();
        return ResponseVO.okResult(list);
    }
}
