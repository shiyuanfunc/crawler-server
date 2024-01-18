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
import java.util.concurrent.TimeUnit;

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
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", script_path, videoUrl, videoName);
            processBuilder.directory(new File(workspace_path));
            Process process = processBuilder.start();
            process.waitFor();
            process.waitFor(3600, TimeUnit.SECONDS);
            log.info("execShell 执行完成 {}, {}", videoUrl, videoName);
        } catch (Exception ex) {
            log.info("执行shell脚本异常", ex);
        }
    }
}
