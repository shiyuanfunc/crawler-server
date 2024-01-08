/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.service;

import com.alibaba.fastjson.JSON;
import com.shiyuan.crawler.server.config.ConsumerServiceBean;
import com.shiyuan.crawler.server.config.MqServiceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author MUSI
 * @Date 2024/1/8 12:35 PM
 * @Description
 * @Version
 **/

@Slf4j
@Component
public class CrawlerVideoConsumerService extends ConsumerServiceBean.ConsumerService {

    private final CrawlerService crawlerService;

    public CrawlerVideoConsumerService(CrawlerService crawlerService) {
        super(MqServiceConstant.MqTopicEnum.CRAWLER_VIDEO_INFO.getTopic());
        this.crawlerService = crawlerService;
    }

    @Override
    public boolean consumer(ConsumerServiceBean.ConsumerInfoBo consumerInfoBo) {
        log.info("CrawlerVideoConsumerService 处理视频下载 {}", JSON.toJSONString(consumerInfoBo));
        VideoInfo videoInfo = super.convertInstance(consumerInfoBo, VideoInfo.class);
        crawlerService.handlerCrawlerTask(videoInfo);
        return true;
    }
}
