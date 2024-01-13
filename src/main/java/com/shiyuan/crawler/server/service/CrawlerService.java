/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.service;

import com.alibaba.fastjson.JSON;
import com.shiyuan.crawler.server.utils.ThreadPoolUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author MUSI
 * @Date 2024/1/8 12:33 PM
 * @Description
 * @Version
 **/

@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String base_path = "/data/dev/downloadspace/";
    private static final String outputFileNameTemplate = "%s.mp4";
    private static final String videoNameFileTxt = "%s.txt";
    private static AtomicInteger count = new AtomicInteger();
    private static final String REGEX = "\\p{P}";

    /**
     * @param videoInfo
     */
    public void handlerCrawlerTask(VideoInfo videoInfo) {

        if (videoInfo == null || StringUtils.isAnyBlank(videoInfo.getVideoUrl(), videoInfo.getVideoName())) {
            return;
        }
        Boolean result = redisTemplate.opsForHash().putIfAbsent("video_repeat_key", videoInfo.getVideoUrlKey(), "1");
        if (!Boolean.TRUE.equals(result)) {
            log.info("video has handler {}", JSON.toJSONString(videoInfo));
            return;
        }

        String videoUrl = videoInfo.getVideoUrl();
        String videoName = videoInfo.getVideoName().replace(REGEX, "").replaceAll(" ", "");
        String videoUrlKey = videoInfo.getVideoUrlKey();

        ThreadPoolUtils.submit(() -> {
            downloadAndMergeM3U8Video(videoUrl, String.format(outputFileNameTemplate, videoName), String.format(videoNameFileTxt, videoUrlKey));
        });
    }

    private static void downloadAndMergeM3U8Video(String m3u8Url, String outputFileName, String videoNameFileText) {

        try {
            String baseUrl = m3u8Url;
            // 获取真是m3u8地址
            String tempUrl = getRealM3u8Url(m3u8Url);
            log.info("getRealM3u8Url 获取真是m3u8地址: {}", tempUrl);
            if (StringUtils.isBlank(tempUrl)) {
                m3u8Url = tempUrl;
            }
            m3u8Url = new URL(new URL(baseUrl), m3u8Url).toString();

            // Step 1: Download M3U8 video segments
            List<String> filePaths = downloadM3U8Video(m3u8Url, videoNameFileText);

            // Step 2: Merge video segments using FFmpeg
            mergeVideoSegments(outputFileName, videoNameFileText);

            // step3 删除文件
            deleteFile(filePaths);
        } catch (Exception ex) {
            log.info("执行链接异常 {}", m3u8Url, ex);
        }
    }


    private static String getRealM3u8Url(String m3u8Url) throws IOException {
        URL url = new URL(m3u8Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(".m3u8")) {
                m3u8Url = line;
            }
        }
        reader.close();
        return m3u8Url;
    }

    private static List<String> downloadM3U8Video(String m3u8Url, String videoNameFileText) {

        List<String> filePaths = new ArrayList<>();
        try {
            URL url = new URL(m3u8Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".ts")) {
                    String filePath = downloadVideoSegment(line, m3u8Url, videoNameFileText);
                    filePaths.add(base_path + filePath);
                }
            }
            reader.close();
        } catch (Exception ex) {

        }
        return filePaths;
    }

    private static String downloadVideoSegment(String segmentUrl, String m3u8Url, String videoNameFileText) throws IOException {
        log.info("下载片段序号: " + count.addAndGet(1));
        URL url = new URL(new URL(m3u8Url), segmentUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        InputStream in = conn.getInputStream();
        String fileName = StringUtils.replace(segmentUrl, "/", "_");
        File file = new File(base_path + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        in.close();

        writeVideoNameText(fileName, videoNameFileText);
        return fileName;
    }

    private static void mergeVideoSegments(String outputFileName, String segmentNameFile) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-f", "concat", "-safe", "0", "-i", segmentNameFile, "-c", "copy", outputFileName);
        processBuilder.directory(new File(base_path));
        Process process = processBuilder.start();
        process.waitFor(3600, TimeUnit.SECONDS);
    }

    private static void writeVideoNameText(String videoNameText, String videoListTxt) {

        try {
            File file = new File(base_path + videoListTxt);
            FileOutputStream fos = null;
            if (!file.exists()) {
                file.createNewFile();
                fos = new FileOutputStream(file);
            } else {
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file, true);
            }

            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

            String fileName = "file '%s'";
            String tempName = base_path + videoNameText;
            osw.write(String.format(fileName, tempName));
            osw.write("\n");
            //写入完成关闭流
            osw.close();
        } catch (Exception ex) {
            // pass
            ex.printStackTrace();
        }
    }

    private static void deleteFile(List<String> fileNameLists) {
        if (CollectionUtils.isEmpty(fileNameLists)) {
            return;
        }
        for (String fileNameList : fileNameLists) {
            try {
                File file = new File(fileNameList);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception ex) {
                log.info("删除文件 {}", fileNameList, ex);
            }
        }
    }
}
