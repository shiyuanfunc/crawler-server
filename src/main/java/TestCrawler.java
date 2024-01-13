/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */


import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author MUSI
 * @Date 2024/1/13 9:22 PM
 * @Description
 * @Version
 **/
public class TestCrawler {


    private static final String base_path = "/Users/songxiaohui/Documents/dev/workspace/terra/business/crawler-server/temp/";
    private static final String videoListTxt = "video_list2.txt";
    private static final String base_domain = "https://askzycdn.com";

    private static AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws Exception{
        String m3u8Url =
                 "https://askzycdn.com/20240112/qStJASHd/index.m3u8";
//        "https://askzycdn.com/20240112/qStJASHd/2000kb/hls/index.m3u8";
        String outputFileName = "output.mp4";

        URL url = new URL(new URL("https://askzycdn.com/20240112/qStJASHd/index.m3u8"), "https://askzycdn.com/20240112/qStJASHd/20241221.m3u8");
        System.out.println(url.toString());

//        mergeVideoSegments(outputFileName);
//
//        try {
//            downloadAndMergeM3U8Video(m3u8Url, outputFileName);
//            System.out.println("Video downloaded and merged successfully!");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private static void downloadAndMergeM3U8Video(String m3u8Url, String outputFileName) throws IOException, InterruptedException {

        m3u8Url = getRealM3u8Url(m3u8Url);
        System.out.println(m3u8Url);

        // Step 1: Download M3U8 video segments
        downloadM3U8Video(m3u8Url);

        // Step 2: Merge video segments using FFmpeg
        mergeVideoSegments(outputFileName);
    }


    private static String getRealM3u8Url(String m3u8Url) throws IOException{
        String tempUrl = "";
        URL url = new URL(m3u8Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(".m3u8")) {
                tempUrl = base_domain + line;
            }
        }
        reader.close();
        return tempUrl;
    }

    private static void downloadM3U8Video(String m3u8Url) throws IOException {
        URL url = new URL(m3u8Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(".ts")) {
                downloadVideoSegment(line, m3u8Url);
            }
        }
        reader.close();
    }

    private static void downloadVideoSegment(String segmentUrl, String m3u8Url) throws IOException {
        System.out.println("下载片段序号: " + count.addAndGet(1) );
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

        writeVideoNameText(fileName);
    }

    private static void mergeVideoSegments(String outputFileName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-f", "concat", "-safe", "0", "-i", "video_list.txt", "-c", "copy", outputFileName);
        processBuilder.directory(new File(base_path));
        Process process = processBuilder.start();
        process.waitFor(3600, TimeUnit.SECONDS);
    }

    private static void writeVideoNameText(String videoNameText) {
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
            osw.write(String.format(fileName, (base_path + videoNameText)));
            osw.write("\n");
            //写入完成关闭流
            osw.close();
        }catch (Exception ex) {
            // pass
            ex.printStackTrace();
        }
    }
}
