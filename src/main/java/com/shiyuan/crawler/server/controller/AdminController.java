/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.controller;

import com.shiyuan.crawler.server.config.ConsumerServiceBean;
import com.shiyuanfunc.common.api.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author MUSI
 * @Date 2024/1/23 9:14 PM
 * @Description
 * @Version
 **/

@RestController
@RequestMapping(path = "/test")
public class AdminController {

    @Autowired
    private ConsumerServiceBean consumerServiceBean;

    @RequestMapping(path = "/stop")
    private BaseResponse<?> stopConsumer() {

        try {
            consumerServiceBean.destroy();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return BaseResponse.success();
    }

    @RequestMapping(path = "/start")
    private BaseResponse<?> startConsumer() {

        try {
            consumerServiceBean.afterPropertiesSet();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return BaseResponse.success();
    }
}
