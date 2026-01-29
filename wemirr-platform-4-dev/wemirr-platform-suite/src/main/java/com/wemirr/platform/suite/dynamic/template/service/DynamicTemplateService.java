/*
 * Copyright (c) 2023 WEMIRR-PLATFORM Authors. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wemirr.platform.suite.dynamic.template.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.mybatisplus.ext.SuperService;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplatePageReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplateSaveReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.TemplateResp;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.VersionHistoryResp;
import com.wemirr.platform.suite.dynamic.template.domain.entity.DynamicTemplate;

import java.util.List;

/**
 * 动态模板服务接口
 *
 * @author Levin
 */
public interface DynamicTemplateService extends SuperService<DynamicTemplate> {

    /**
     * 分页查询模板
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    IPage<TemplateResp> pageList(TemplatePageReq pageReq);

    /**
     * 获取模板详情
     *
     * @param id ID
     * @return 模板详情
     */
    TemplateResp detail(Long id);

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板详情
     */
    TemplateResp detailByCode(String templateCode);

    /**
     * 创建模板
     *
     * @param req 保存请求
     */
    void add(TemplateSaveReq req);

    /**
     * 修改模板
     *
     * @param id  ID
     * @param req 保存请求
     */
    void edit(Long id, TemplateSaveReq req);

    /**
     * 删除模板
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 发布模板
     *
     * @param id          ID
     * @param changeReason 发布原因
     */
    void publish(Long id, String changeReason);

    /**
     * 下线模板
     *
     * @param id ID
     */
    void offline(Long id);

    /**
     * 回滚版本
     *
     * @param id        ID
     * @param version   目标版本号
     * @param reason    回滚原因
     */
    void rollback(Long id, Integer version, String reason);

    /**
     * 获取版本历史
     *
     * @param templateId 模板ID
     * @return 版本历史列表
     */
    List<VersionHistoryResp> getVersionHistory(Long templateId);
}
