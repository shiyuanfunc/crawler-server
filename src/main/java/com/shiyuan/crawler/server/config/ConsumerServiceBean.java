/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.config;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author MUSI
 * @Date 2023/11/11 3:54 PM
 * @Description
 * @Version
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(MqConfig.class)
public class ConsumerServiceBean implements InitializingBean, DisposableBean {

    public static final ClientServiceProvider CLIENT_SERVICE_PROVIDER = ClientServiceProvider.loadService();
    private PushConsumer consumer;
    private final MqConfig config;
    private static String suffix = "";


    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("消费者初始化 >>>>>> {}", JSON.toJSONString(config));
        if (config == null || StringUtils.isBlank(config.getEndpoint())) {
            return;
        }
        ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(config.getEndpoint())
                .setRequestTimeout(Duration.ofSeconds(10))
                .build();
        Map<String, FilterExpression> subScript = new HashMap<>();
        for (MqServiceConstant.MqTopicEnum topicConstant : MqServiceConstant.MqTopicEnum.values()) {
            subScript.put(topicConstant.getTopic(), new FilterExpression(topicConstant.getTag()));
        }
        try {
            consumer = CLIENT_SERVICE_PROVIDER.newPushConsumerBuilder()
                    .setConsumerGroup(config.getConsumerGroup())
                    .setClientConfiguration(configuration)
                    .setSubscriptionExpressions(subScript)
                    .setMessageListener(ConsumerListener::apply)
                    .build();
        } catch (ClientException e) {
            log.info("消费者启动失败", e);
            throw new RuntimeException(e);
        }
    }

    @Data
    @Builder
    public static class ConsumerInfoBo {
        private String topic;
        private String tag;
        private String body;
    }

    private static class ConsumerListener {
        static ConsumeResult apply(MessageView messageView) {
            ConsumerInfoBo.ConsumerInfoBoBuilder consumerInfoBoBuilder = ConsumerInfoBo.builder();
            String topic = messageView.getTopic();
            consumerInfoBoBuilder.topic(topic);
            Optional<String> messageViewTag = messageView.getTag();
            if (messageViewTag.isPresent()) {
                String tag = messageViewTag.get();
                consumerInfoBoBuilder.tag(tag);
            }
            String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
            consumerInfoBoBuilder.body(body);
            try {
                boolean consumerResult = ConsumerFactory.getConsumerInstance(topic)
                        .consumer(consumerInfoBoBuilder.build());
                if (consumerResult) {
                    return ConsumeResult.SUCCESS;
                }
            } catch (Exception ex) {
                log.info("ConsumerListener message consumer error", ex);
            }
            return ConsumeResult.FAILURE;
        }
    }

    public abstract static class ConsumerService {
        public ConsumerService(String topic) {
            if (StringUtils.isBlank(topic)) {
                throw new RuntimeException("订阅topic不可为空");
            }
            topic = topic + ConsumerServiceBean.suffix;
            ConsumerFactory.registerConsumer(topic, this);
        }

        /**
         * 转换对象
         *
         * @param consumerInfoBo
         * @param clz
         * @param <T>
         * @return
         */
        public <T> T convertInstance(ConsumerInfoBo consumerInfoBo, Class<T> clz) {
            if (consumerInfoBo == null || StringUtils.isBlank(consumerInfoBo.getBody())) {
                return null;
            }
            try {
                return JSON.parseObject(consumerInfoBo.getBody(), clz);
            } catch (Exception ex) {
                log.error("ConsumerService message convert error {}", consumerInfoBo, ex);
            }
            return null;
        }

        /**
         * 消息消费
         *
         * @param consumerInfoBo
         * @return true 消费成功
         * false 重新消费
         */
        public abstract boolean consumer(ConsumerInfoBo consumerInfoBo);

        public static final ConsumerService DEFAULT_CONSUMER_SERVICE = new ConsumerService(UUID.randomUUID().toString()) {
            @Override
            public boolean consumer(ConsumerInfoBo value) {
                return true;
            }
        };
    }

    private static class ConsumerFactory {
        private static final Map<String, ConsumerService> CONSUMER_SERVICE_MAP = new ConcurrentHashMap<>();

        public static void registerConsumer(String topic, ConsumerService consumerService) {
            log.info("consumer service register {}", topic);
            if (StringUtils.isBlank(topic)) {
                throw new RuntimeException("初始化失败, 消费订阅topic不可为空");
            }
            if (consumerService == null) {
                consumerService = ConsumerService.DEFAULT_CONSUMER_SERVICE;
            }
            ConsumerFactory.CONSUMER_SERVICE_MAP.put(topic, consumerService);
        }

        public static ConsumerService getConsumerInstance(String topic) {
            if (StringUtils.isBlank(topic)) {
                return ConsumerService.DEFAULT_CONSUMER_SERVICE;
            }
            ConsumerService consumerService = CONSUMER_SERVICE_MAP.get(topic);
            if (consumerService == null) {
                return ConsumerService.DEFAULT_CONSUMER_SERVICE;
            }
            return consumerService;
        }
    }
}

