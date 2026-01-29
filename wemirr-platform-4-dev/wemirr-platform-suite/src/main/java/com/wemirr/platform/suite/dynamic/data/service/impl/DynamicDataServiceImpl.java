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

package com.wemirr.platform.suite.dynamic.data.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.JacksonUtils;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.framework.security.context.AuthenticationContext;
import com.wemirr.framework.security.context.AuthenticationContextHolder;
import com.wemirr.platform.suite.dynamic.data.domain.dto.req.DataSaveReq;
import com.wemirr.platform.suite.dynamic.data.domain.dto.resp.DataResp;
import com.wemirr.platform.suite.dynamic.data.domain.entity.DynamicData;
import com.wemirr.platform.suite.dynamic.data.repository.DynamicDataMapper;
import com.wemirr.platform.suite.dynamic.data.service.DataPageReq;
import com.wemirr.platform.suite.dynamic.data.service.DynamicDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * 动态数据服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicDataServiceImpl extends SuperServiceImpl<DynamicDataMapper, DynamicData> implements DynamicDataService {

    private final AuthenticationContext authenticationContext;

    @Override
    public IPage<DataResp> pageList(DataPageReq pageReq) {
        // 获取当前用户的租户ID，确保数据隔离
        Long tenantId = getCurrentTenantId();
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<DynamicData>lbQ()
                        .eq(DynamicData::getTenantId, tenantId)  // 添加租户隔离
                        .eq(DynamicData::getTemplateId, pageReq.getTemplateId())
                        .like(DynamicData::getBusinessNo, pageReq.getBusinessNo())
                        .like(DynamicData::getDataTitle, pageReq.getDataTitle())
                        .eq(DynamicData::getDataStatus, pageReq.getDataStatus())
                        .eq(DynamicData::getProcessStatus, pageReq.getProcessStatus())
                        .eq(DynamicData::getSubmitterId, pageReq.getSubmitterId()))
                .convert(x -> BeanUtilPlus.toBean(x, DataResp.class));
    }

    /**
     * 获取当前用户的租户ID
     */
    private Long getCurrentTenantId() {
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        if (context != null && context.tenantId() != null) {
            return context.tenantId();
        }
        throw CheckedException.unauthorized("用户未登录或租户信息缺失");
    }

    /**
     * 根据ID获取数据（带租户隔离）
     */
    private DynamicData getDataById(Long id) {
        Long tenantId = getCurrentTenantId();
        DynamicData data = this.baseMapper.selectById(id);
        if (data == null || !tenantId.equals(data.getTenantId())) {
            throw CheckedException.notFound("数据不存在或无权限访问");
        }
        return data;
    }

    @Override
    public DataResp detail(Long id) {
        DynamicData entity = getDataById(id);
        return BeanUtilPlus.toBean(entity, DataResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(DataSaveReq req) {
        // 获取当前用户信息
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        Long tenantId = getCurrentTenantId();
        Long userId = context != null ? context.userId() : null;
        String userName = context != null ? context.nickName() : "未知用户";

        DynamicData entity = BeanUtilPlus.toBean(req, DynamicData.class);
        // 设置租户ID
        entity.setTenantId(tenantId);
        // 设置默认状态
        if (entity.getDataStatus() == null) {
            entity.setDataStatus("draft");
        }
        // 设置提交人信息
        entity.setSubmitterId(userId);
        entity.setSubmitterName(userName);
        this.baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, DataSaveReq req) {
        getDataById(id);

        DynamicData entity = BeanUtilPlus.toBean(id, req, DynamicData.class);
        this.baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DynamicData data = getDataById(id);
        if ("submitted".equals(data.getDataStatus())) {
            throw CheckedException.badRequest("已提交的数据不能删除");
        }
        this.baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        DynamicData data = getDataById(id);
        if (!"draft".equals(data.getDataStatus())) {
            throw CheckedException.badRequest("只有草稿状态的数据可以提交");
        }

        this.baseMapper.updateById(DynamicData.builder()
                .id(id)
                .dataStatus("submitted")
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String comment) {
        DynamicData data = getDataById(id);
        if (!"submitted".equals(data.getDataStatus())) {
            throw CheckedException.badRequest("只有已提交的数据可以审批");
        }

        String newStatus = approved ? "approved" : "rejected";
        // 保存审批意见
        this.baseMapper.updateById(DynamicData.builder()
                .id(id)
                .dataStatus(newStatus)
                .approveComment(comment)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(Long id) {
        DynamicData data = getDataById(id);
        if (!"submitted".equals(data.getDataStatus())) {
            throw CheckedException.badRequest("只有已提交的数据可以撤回");
        }

        this.baseMapper.updateById(DynamicData.builder()
                .id(id)
                .dataStatus("draft")
                .build());
    }

    @Override
    public Map<String, Object> getDataContent(Long id) {
        DynamicData data = getDataById(id);

        if (data.getDataContent() == null || data.getDataContent().isEmpty()) {
            return Map.of();
        }

        try {
            return JacksonUtils.readValue(data.getDataContent(), Map.class);
        } catch (Exception e) {
            log.error("解析数据内容失败，数据ID: {}", id, e);
            throw CheckedException.badRequest("数据格式错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(Long id, String processInstanceId, String processStatus) {
        this.baseMapper.updateById(DynamicData.builder()
                .id(id)
                .processInstanceId(processInstanceId)
                .processStatus(processStatus)
                .build());
    }
}
