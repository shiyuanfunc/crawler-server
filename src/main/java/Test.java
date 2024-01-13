/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

/**
 * @Author MUSI
 * @Date 2024/1/8 8:52 PM
 * @Description
 * @Version
 **/
public class Test {

//    public static void main(String[] args) throws IOException, InterruptedException {
////        String command = "/data/soft/ffmpeg/download.sh %s %s";
//        String command = "/Users/songxiaohui/Documents/dev/workspace/terra/wechant-token/test.sh %s %s";
//        String format = String.format(command, "https://play.hgm4u9.com/20230110/s3Utaif8/index.m3u8", "1231312");
//        System.out.println(format);
//        Process exec = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", format});
//        System.out.println(exec.waitFor());
//    }


    public static void main(String[] args) {


        String partten = "\\p{P}";

        String name = "【幸福老哥双飞】！一线天极品美穴！翘起 屁股求 操，雨露均沾 轮番上，紧致：'嫩 穴操 的舒服，让人羡慕";
        String s = name.replaceAll(partten, "").replaceAll(" ", "");

        System.out.println(s);

//        // ffmpeg -user_agent "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
//        // -headers "Referer: https://www.69se.tv/" -i $1 -c copy -bsf:a aac_adtstoasc -movflags +faststart /data/dev/downloadspace/$2.mp4 >> /data/dev/downloadspace/download.log
//
//        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36";
//        String headers =  "Referer: https://www.69se.tv/";
//
//        String path = "/data/dev/downloadspace/%s.mp4";
//        String logPath = "/data/dev/downloadspace/download.log";
//
//        String url = "https://play.hgm4u9.com/20221205/w7XTlEfU/index.m3u8";
//        String fileName = "12312312";
//
//        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-user_agent", userAgent, "-headers", headers,
//                "-i", url, "-c", "copy", "bsf:a", "aac_adtstoasc", "-movflags", "+faststart", String.format(path, fileName), ">>", logPath);
//
//        try{
//
//            Process process = processBuilder.start();
//            int i = process.waitFor();
//            System.out.println("结果: " + i);
//        }catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}
