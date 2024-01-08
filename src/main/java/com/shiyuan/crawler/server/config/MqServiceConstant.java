/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;

/**
 * @Author MUSI
 * @Date 2024/1/6 11:23 AM
 * @Description
 * @Version
 **/
public class MqServiceConstant {


    /**
     * 消息topic
     */
    @Getter
    public enum MqTopicEnum {

        CRAWLER_VIDEO_INFO("crawler_video_info", null, "获取视频信息")
        ;

        MqTopicEnum(String topic, String tag, String desc) {
            this.topic = topic;
            this.desc = desc;
            if (StringUtils.isBlank(tag)) {
                tag = FilterExpression.SUB_ALL.getExpression();
            }
            this.tag = tag;
        }

        /**
         * topic
         */
        private String topic;


        private String tag;
        /**
         * 描述
         */
        private String desc;
    }
}
