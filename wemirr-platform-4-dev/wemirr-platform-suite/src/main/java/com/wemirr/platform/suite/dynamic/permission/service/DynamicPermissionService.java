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

package com.wemirr.platform.suite.dynamic.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.mybatisplus.ext.SuperService;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.req.PermissionSaveReq;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.resp.PermissionResp;
import com.wemirr.platform.suite.dynamic.permission.domain.entity.DynamicPermission;

import java.util.List;

/**
 * 数据权限服务接口
 *
 * @author Levin
 */
public interface DynamicPermissionService extends SuperService<DynamicPermission> {

    /**
     * 分页查询权限
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    IPage<PermissionResp> pageList(PermissionPageReq pageReq);

    /**
     * 获取权限详情
     *
     * @param id ID
     * @return 权限详情
     */
    PermissionResp detail(Long id);

    /**
     * 创建权限
     *
     * @param req 保存请求
     */
    void add(PermissionSaveReq req);

    /**
     * 修改权限
     *
     * @param id  ID
     * @param req 保存请求
     */
    void edit(Long id, PermissionSaveReq req);

    /**
     * 删除权限
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 根据模板ID获取权限列表
     *
     * @param templateId 模板ID
     * @return 权限列表
     */
    List<PermissionResp> getPermissionsByTemplateId(Long templateId);

    /**
     * 检查用户是否有权限访问指定数据
     *
     * @param userId     用户ID
     * @param templateId 模板ID
     * @param dataId     数据ID
     * @return 是否有权限
     */
    boolean checkPermission(Long userId, Long templateId, Long dataId);

    /**
     * 获取用户可访问的数据范围
     *
     * @param userId     用户ID
     * @param templateId 模板ID
     * @return 可访问的数据ID列表
     */
    List<Long> getAccessibleDataIds(Long userId, Long templateId);
}
