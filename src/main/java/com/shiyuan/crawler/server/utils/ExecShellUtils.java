/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * @Author MUSI
 * @Date 2024/1/18 9:44 AM
 * @Description
 * @Version
 **/
@Slf4j
public class ExecShellUtils {

    private static final String workspace_path = "/data/soft/ffmpeg";
    private static final String script_path = "/data/soft/ffmpeg/download.sh";

    public static void execShell(String videoUrl, String videoName) {
        log.info("exec shell {}, {}", videoUrl, videoName);
        if (StringUtils.isAnyBlank(videoUrl, videoName)) {
            return;
        }
        boolean fileExist = checkFileExist(videoName);
        log.info("当前文件是否存在：{}", fileExist);
        if (fileExist) {
            videoName = videoName + UUID.randomUUID().toString().replaceAll("-", "");
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", script_path, videoUrl, videoName);
            processBuilder.directory(new File(workspace_path));
            Process process = processBuilder.start();

            // 获取进程的输出流和错误流
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();


            new Thread(() -> {
                try {
                    // 读取输出流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("execShell 标准输出流 {}", line);
                    }
                }catch (Exception ex) {
                    log.error("execShell", ex);
                }
            }).start();

            new Thread(() -> {
                try {
                    // 读取错误流
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        log.info("execShell Error: {}", errorLine);
                    }
                }catch (Exception ex) {
                    log.error("execShell", ex);
                }
            }).start();

            process.waitFor();
            log.info("execShell 执行完成 {}, {}", videoUrl, videoName);
        } catch (Exception ex) {
            log.info("执行shell脚本异常", ex);
        }
    }

    private static boolean checkFileExist(String videoName) {
        if (StringUtils.isBlank(videoName)) {
            return false;
        }
        String workspace = "/data/dev/downloadspace/%s.mp4";
        String filePath = String.format(workspace, videoName);
        File file = new File(filePath);
        return file.exists();
    }
}
