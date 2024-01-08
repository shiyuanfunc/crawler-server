/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author MUSI
 * @Date 2024/1/8 12:27 PM
 * @Description
 * @Version
 **/
@Data
@Component
@ConfigurationProperties(prefix = "config.mq")
public class MqConfig {
    private String endpoint;
    private String producerGroup;
    private String consumerGroup;
}
