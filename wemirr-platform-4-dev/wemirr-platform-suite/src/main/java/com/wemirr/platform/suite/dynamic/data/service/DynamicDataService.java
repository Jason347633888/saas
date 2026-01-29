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

package com.wemirr.platform.suite.dynamic.data.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.mybatisplus.ext.SuperService;
import com.wemirr.platform.suite.dynamic.data.domain.dto.req.DataSaveReq;
import com.wemirr.platform.suite.dynamic.data.domain.dto.resp.DataResp;
import com.wemirr.platform.suite.dynamic.data.domain.entity.DynamicData;

import java.util.Map;

/**
 * 动态数据服务接口
 *
 * @author Levin
 */
public interface DynamicDataService extends SuperService<DynamicData> {

    /**
     * 分页查询数据
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    IPage<DataResp> pageList(DataPageReq pageReq);

    /**
     * 获取数据详情
     *
     * @param id ID
     * @return 数据详情
     */
    DataResp detail(Long id);

    /**
     * 创建数据
     *
     * @param req 保存请求
     * @return 创建的数据ID
     */
    Long add(DataSaveReq req);

    /**
     * 修改数据
     *
     * @param id  ID
     * @param req 保存请求
     */
    void edit(Long id, DataSaveReq req);

    /**
     * 删除数据
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 提交数据
     *
     * @param id ID
     */
    void submit(Long id);

    /**
     * 审批数据
     *
     * @param id      ID
     * @param approved 是否通过
     * @param comment 审批意见
     */
    void approve(Long id, boolean approved, String comment);

    /**
     * 撤回数据
     *
     * @param id ID
     */
    void withdraw(Long id);

    /**
     * 获取数据内容（解析后的Map）
     *
     * @param id ID
     * @return 数据内容Map
     */
    Map<String, Object> getDataContent(Long id);

    /**
     * 更新流程状态
     *
     * @param id              ID
     * @param processInstanceId 流程实例ID
     * @param processStatus    流程状态
     */
    void updateProcessStatus(Long id, String processInstanceId, String processStatus);
}
