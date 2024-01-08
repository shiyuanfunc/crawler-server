/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @Author MUSI
 * @Date 2024/1/7 11:54 AM
 * @Description
 * @Version
 **/
public class VideoInfo implements Serializable {

    @Getter
    @AllArgsConstructor
    public enum VideoChannel {
        TSRJ("tsrj", "https://www.tsrj01.top/");
        private String code;
        private String url;
    }


    private String videoName;

    private String videoImage;

    private String videoUrl;

    /**
     * videoUrl MD5
     */
    private String videoUrlKey;

    /**
     * 资源来源
     */
    private String channelCode;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        if (StringUtils.isNotBlank(videoUrl)) {
            this.videoUrlKey = Md5Crypt.md5Crypt(this.videoUrl.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String getVideoUrlKey() {
        return videoUrlKey;
    }

    public void setVideoUrlKey(String videoUrlKey) {
        this.videoUrlKey = videoUrlKey;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
}
