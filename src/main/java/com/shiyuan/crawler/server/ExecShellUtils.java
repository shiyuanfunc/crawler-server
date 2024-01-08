/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @Author MUSI
 * @Date 2024/1/7 12:25 PM
 * @Description
 * @Version
 **/

@Slf4j
public class ExecShellUtils {

    /***
     * 执行脚本
     * @param shellCommand
     */
    public static void execShell(String shellCommand) {

        log.info("ExecShellUtils exec shell command: {}", shellCommand);
        try {
            if (StringUtils.isBlank(shellCommand)) {
                return;
            }
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[]{"/bin/sh", "-c", shellCommand});
            InputStreamReader ir = new InputStreamReader(process
                    .getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("exec over >>>>>>");
        }catch (Exception ex) {
            log.info("execShell error command:{}", shellCommand, ex);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        execShell("/Users/songxiaohui/Documents/dev/workspace/terra/wechant-token/test.sh song 456");
    }

}
