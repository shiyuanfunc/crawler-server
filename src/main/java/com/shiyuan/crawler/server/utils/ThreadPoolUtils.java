/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author MUSI
 * @Date 2024/1/13 11:43 PM
 * @Description
 * @Version
 **/

@Slf4j
public class ThreadPoolUtils {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(4, 4, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("CrawlerServer_POOL_%d").build());

    private static final ThreadPoolExecutor THREAD_POOL_ASYNC_EXECUTOR = new ThreadPoolExecutor(8, 8, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("CrawlerServer_POOL_%d").build());


    public static void submit(Runnable runnable) {
        THREAD_POOL_EXECUTOR.submit(runnable);
    }

    public static void submitGetSegmentTask(Runnable runnable) {
        log.info("提交任务 submitGetSegmentTask ");
        THREAD_POOL_ASYNC_EXECUTOR.submit(runnable);
    }

    public static void printJosQueue() {
        int size = THREAD_POOL_EXECUTOR.getQueue().size();
        log.info("THREAD_POOL_EXECUTOR 下载资源线程池队列任务: {}", size);

        int count = THREAD_POOL_ASYNC_EXECUTOR.getQueue().size();
        log.info("THREAD_POOL_ASYNC_EXECUTOR 下载资源异常线程池队列任务: {}", count);

    }
}
