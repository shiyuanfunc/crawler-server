/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package com.shiyuan.crawler.server.controller;

import com.shiyuan.crawler.server.utils.ExecShellUtils;
import com.shiyuanfunc.common.api.BaseResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author MUSI
 * @Date 2024/1/18 9:59 AM
 * @Description
 * @Version
 **/

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @RequestMapping(path = "/exec")
    public BaseResponse<?> execTask(String url, String name) {

        ExecShellUtils.execShell(url, name);
        return BaseResponse.success();
    }
}
