package com.easypan.service;

import com.easypan.entity.po.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public interface FileInfoService extends IService<FileInfo> {

    void downloadFile(String fileId, HttpServletRequest request, HttpServletResponse response );
}
