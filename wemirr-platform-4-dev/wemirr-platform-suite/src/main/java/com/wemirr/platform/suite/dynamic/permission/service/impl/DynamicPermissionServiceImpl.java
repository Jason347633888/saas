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

package com.wemirr.platform.suite.dynamic.permission.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.framework.security.context.AuthenticationContext;
import com.wemirr.framework.security.context.AuthenticationContextHolder;
import com.wemirr.platform.suite.dynamic.data.domain.entity.DynamicData;
import com.wemirr.platform.suite.dynamic.data.repository.DynamicDataMapper;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.req.PermissionSaveReq;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.resp.PermissionResp;
import com.wemirr.platform.suite.dynamic.permission.domain.entity.DynamicPermission;
import com.wemirr.platform.suite.dynamic.permission.repository.DynamicPermissionMapper;
import com.wemirr.platform.suite.dynamic.permission.service.DynamicPermissionService;
import com.wemirr.platform.suite.dynamic.permission.service.PermissionPageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 数据权限服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicPermissionServiceImpl extends SuperServiceImpl<DynamicPermissionMapper, DynamicPermission> implements DynamicPermissionService {

    private final DynamicDataMapper dynamicDataMapper;

    @Override
    public IPage<PermissionResp> pageList(PermissionPageReq pageReq) {
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<DynamicPermission>lbQ()
                        .like(DynamicPermission::getPermissionCode, pageReq.getPermissionCode())
                        .like(DynamicPermission::getPermissionName, pageReq.getPermissionName())
                        .eq(DynamicPermission::getTemplateId, pageReq.getTemplateId())
                        .eq(DynamicPermission::getPermissionType, pageReq.getPermissionType())
                        .eq(DynamicPermission::getStatus, pageReq.getStatus()))
                .convert(x -> BeanUtilPlus.toBean(x, PermissionResp.class));
    }

    @Override
    public PermissionResp detail(Long id) {
        DynamicPermission entity = Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("权限配置不存在"));
        return BeanUtilPlus.toBean(entity, PermissionResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PermissionSaveReq req) {
        // 检查编码是否已存在
        checkCodeExists(req.getPermissionCode(), null);

        DynamicPermission entity = BeanUtilPlus.toBean(req, DynamicPermission.class);
        // 设置默认优先级
        if (entity.getPriority() == null) {
            entity.setPriority(0);
        }
        this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, PermissionSaveReq req) {
        // 检查是否存在
        getById(id);

        // 检查编码是否被其他记录使用
        checkCodeExists(req.getPermissionCode(), id);

        DynamicPermission entity = BeanUtilPlus.toBean(id, req, DynamicPermission.class);
        this.baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<PermissionResp> getPermissionsByTemplateId(Long templateId) {
        List<DynamicPermission> permissions = this.baseMapper.selectList(Wraps.<DynamicPermission>lbQ()
                .eq(DynamicPermission::getTemplateId, templateId)
                .eq(DynamicPermission::getStatus, true)
                .orderByAsc(DynamicPermission::getPriority));
        return BeanUtilPlus.toBean(permissions, PermissionResp.class);
    }

    @Override
    public boolean checkPermission(Long userId, Long templateId, Long dataId) {
        // 1. 获取当前用户和租户信息
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        if (context == null) {
            return false;
        }
        Long currentUserId = context.userId();
        Long tenantId = context.tenantId();

        // 2. 如果请求的 userId 与当前用户不匹配，拒绝访问（防止越权）
        if (userId != null && !userId.equals(currentUserId)) {
            log.warn("越权访问尝试：用户 {} 试图访问用户 {} 的数据", currentUserId, userId);
            return false;
        }

        // 3. 如果没有指定数据ID，检查是否有该模板的任何权限
        if (dataId == null) {
            return hasAnyPermissionForTemplate(templateId, currentUserId, tenantId);
        }

        // 4. 检查数据是否存在且属于同一租户
        DynamicData data = dynamicDataMapper.selectById(dataId);
        if (data == null || !tenantId.equals(data.getTenantId())) {
            return false;
        }

        // 5. 检查用户是否有该模板的访问权限
        if (!hasAnyPermissionForTemplate(templateId, currentUserId, tenantId)) {
            return false;
        }

        // 6. 如果有管理权限，允许访问
        if (hasPermissionType(templateId, currentUserId, tenantId, "manage")) {
            return true;
        }

        // 7. 检查是否有查看、使用或创建权限
        return hasPermissionType(templateId, currentUserId, tenantId, "view")
                || hasPermissionType(templateId, currentUserId, tenantId, "use")
                || hasPermissionType(templateId, currentUserId, tenantId, "create");
    }

    @Override
    public List<Long> getAccessibleDataIds(Long userId, Long templateId) {
        // 1. 获取当前用户和租户信息
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        if (context == null) {
            return List.of();
        }
        Long currentUserId = context.userId();
        Long tenantId = context.tenantId();

        // 2. 如果请求的 userId 与当前用户不匹配，返回空
        if (userId != null && !userId.equals(currentUserId)) {
            return List.of();
        }

        // 3. 检查用户是否有该模板的任何权限
        if (!hasAnyPermissionForTemplate(templateId, currentUserId, tenantId)) {
            return List.of();
        }

        // 4. 如果有管理权限，返回该模板的所有数据ID
        if (hasPermissionType(templateId, currentUserId, tenantId, "manage")) {
            return dynamicDataMapper.selectList(
                    Wraps.<DynamicData>lbQ()
                            .eq(DynamicData::getTemplateId, templateId)
                            .eq(DynamicData::getTenantId, tenantId)
            ).stream().map(DynamicData::getId).toList();
        }

        // 5. 否则返回用户创建的数据ID
        return dynamicDataMapper.selectList(
                Wraps.<DynamicData>lbQ()
                        .eq(DynamicData::getTemplateId, templateId)
                        .eq(DynamicData::getTenantId, tenantId)
                        .eq(DynamicData::getSubmitterId, currentUserId)
        ).stream().map(DynamicData::getId).toList();
    }

    /**
     * 检查用户是否有该模板的任何权限
     */
    private boolean hasAnyPermissionForTemplate(Long templateId, Long userId, Long tenantId) {
        // 这里应该查询用户所属部门的权限配置
        // 简化处理：检查是否有该模板的权限记录
        Long count = this.baseMapper.selectCount(Wraps.<DynamicPermission>lbQ()
                .eq(DynamicPermission::getTemplateId, templateId)
                .eq(DynamicPermission::getStatus, true));
        return count > 0;
    }

    /**
     * 检查用户是否有指定类型的权限
     */
    private boolean hasPermissionType(Long templateId, Long userId, Long tenantId, String permissionType) {
        // 这里应该根据用户所属部门查询权限
        // 简化处理：检查是否有该类型的权限记录
        Long count = this.baseMapper.selectCount(Wraps.<DynamicPermission>lbQ()
                .eq(DynamicPermission::getTemplateId, templateId)
                .eq(DynamicPermission::getPermissionType, permissionType)
                .eq(DynamicPermission::getStatus, true));
        return count > 0;
    }

    /**
     * 检查编码是否存在
     */
    private void checkCodeExists(String permissionCode, Long excludeId) {
        DynamicPermission existing = this.baseMapper.selectOne(Wraps.<DynamicPermission>lbQ()
                .eq(DynamicPermission::getPermissionCode, permissionCode));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw CheckedException.badRequest("权限编码已存在");
        }
    }
}
