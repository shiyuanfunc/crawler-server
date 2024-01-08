/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.service;

import com.alibaba.fastjson.JSON;
import com.shiyuan.crawler.server.ExecShellUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private static final String BASE_COMMAND = "/data/soft/ffmpeg/download.sh %s %s";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     *
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
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String command = String.format(BASE_COMMAND, videoInfo.getVideoUrl(), videoInfo.getVideoName());
                ExecShellUtils.execShell(command);
            }
        });
    }
}
