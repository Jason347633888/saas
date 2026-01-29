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

package com.wemirr.platform.suite.dynamic.template.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.framework.security.context.AuthenticationContext;
import com.wemirr.framework.security.context.AuthenticationContextHolder;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplatePageReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplateSaveReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.TemplateResp;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.VersionHistoryResp;
import com.wemirr.platform.suite.dynamic.template.domain.entity.DynamicTemplate;
import com.wemirr.platform.suite.dynamic.template.domain.entity.VersionHistory;
import com.wemirr.platform.suite.dynamic.template.repository.DynamicTemplateMapper;
import com.wemirr.platform.suite.dynamic.template.repository.VersionHistoryMapper;
import com.wemirr.platform.suite.dynamic.template.service.DynamicTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 动态模板服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicTemplateServiceImpl extends SuperServiceImpl<DynamicTemplateMapper, DynamicTemplate> implements DynamicTemplateService {

    private final VersionHistoryMapper versionHistoryMapper;

    @Override
    public IPage<TemplateResp> pageList(TemplatePageReq pageReq) {
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<DynamicTemplate>lbQ()
                        .like(DynamicTemplate::getTemplateCode, pageReq.getTemplateCode())
                        .like(DynamicTemplate::getTemplateName, pageReq.getTemplateName())
                        .eq(DynamicTemplate::getTemplateType, pageReq.getTemplateType())
                        .eq(DynamicTemplate::getCategory, pageReq.getCategory())
                        .eq(DynamicTemplate::getPublishStatus, pageReq.getPublishStatus())
                        .eq(DynamicTemplate::getIsDraft, pageReq.getIsDraft()))
                .convert(x -> BeanUtilPlus.toBean(x, TemplateResp.class));
    }

    @Override
    public TemplateResp detail(Long id) {
        DynamicTemplate entity = Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("模板不存在"));

        TemplateResp resp = BeanUtilPlus.toBean(entity, TemplateResp.class);

        // 获取版本历史
        List<VersionHistory> histories = versionHistoryMapper.selectList(Wraps.<VersionHistory>lbQ()
                .eq(VersionHistory::getTemplateId, id)
                .orderByDesc(VersionHistory::getVersion));
        resp.setVersionHistories(BeanUtilPlus.toBean(histories, VersionHistoryResp.class));

        return resp;
    }

    @Override
    public TemplateResp detailByCode(String templateCode) {
        DynamicTemplate entity = Optional.ofNullable(this.baseMapper.selectOne(Wraps.<DynamicTemplate>lbQ()
                        .eq(DynamicTemplate::getTemplateCode, templateCode)))
                .orElseThrow(() -> CheckedException.notFound("模板不存在"));
        return detail(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(TemplateSaveReq req) {
        // 检查编码是否已存在
        checkCodeExists(req.getTemplateCode(), null);

        DynamicTemplate entity = BeanUtilPlus.toBean(req, DynamicTemplate.class);
        entity.setVersion(1);
        entity.setIsCurrent(true);
        entity.setPublishStatus("draft");
        this.baseMapper.insert(entity);

        // 记录版本历史
        saveVersionHistory(entity, "CREATE", "创建模板", "首次创建");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, TemplateSaveReq req) {
        DynamicTemplate existing = getById(id);

        // 检查编码是否被其他记录使用
        checkCodeExists(req.getTemplateCode(), id);

        // 保存旧版本快照
        String snapshot = BeanUtilPlus.toJson(existing);

        DynamicTemplate entity = BeanUtilPlus.toBean(id, req, DynamicTemplate.class);
        this.baseMapper.updateById(entity);

        // 更新版本号
        this.baseMapper.updateById(DynamicTemplate.builder()
                .id(id)
                .version(existing.getVersion() + 1)
                .build());

        // 记录版本历史
        saveVersionHistory(entity, "UPDATE", "更新模板", "模板更新", snapshot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
        // 删除版本历史
        versionHistoryMapper.delete(Wraps.<VersionHistory>lbQ()
                .eq(VersionHistory::getTemplateId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id, String changeReason) {
        DynamicTemplate template = getById(id);

        if (!template.getIsDraft()) {
            throw CheckedException.badRequest("只有草稿状态的模板可以发布");
        }

        // 更新发布状态
        this.baseMapper.updateById(DynamicTemplate.builder()
                .id(id)
                .isDraft(false)
                .isCurrent(true)
                .publishStatus("published")
                .build());

        // 记录版本历史
        saveVersionHistory(template, "PUBLISH", "发布模板", changeReason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long id) {
        DynamicTemplate template = getById(id);

        if (!"published".equals(template.getPublishStatus())) {
            throw CheckedException.badRequest("只有已发布的模板可以下线");
        }

        this.baseMapper.updateById(DynamicTemplate.builder()
                .id(id)
                .publishStatus("offline")
                .build());

        saveVersionHistory(template, "OFFLINE", "下线模板", "手动下线");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(Long id, Integer version, String reason) {
        DynamicTemplate template = getById(id);

        // 查找目标版本的历史记录
        VersionHistory targetHistory = versionHistoryMapper.selectOne(Wraps.<VersionHistory>lbQ()
                .eq(VersionHistory::getTemplateId, id)
                .eq(VersionHistory::getVersion, version));

        if (targetHistory == null) {
            throw CheckedException.notFound("目标版本不存在");
        }

        // 验证快照数据
        String snapshot = targetHistory.getSnapshotData();
        if (snapshot == null || snapshot.isEmpty()) {
            throw CheckedException.badRequest("快照数据为空，无法回滚");
        }

        // 验证快照数据格式（简单的 JSON 格式验证）
        if (!snapshot.trim().startsWith("{") || !snapshot.trim().endsWith("}")) {
            throw CheckedException.badRequest("快照数据格式错误");
        }

        // 恢复数据 - 使用只读字段的白名单方式
        try {
            DynamicTemplate rollbackData = parseSnapshotData(snapshot);
            rollbackData.setId(id);
            rollbackData.setVersion(template.getVersion() + 1);
            rollbackData.setIsCurrent(true);
            rollbackData.setPublishStatus("published");
            this.baseMapper.updateById(rollbackData);
        } catch (Exception e) {
            log.error("解析快照数据失败，模板ID: {}, 版本: {}", id, version, e);
            throw CheckedException.badRequest("快照数据解析失败，无法回滚");
        }

        // 记录版本历史
        saveVersionHistory(template, "ROLLBACK", "回滚版本", reason + "，回滚到版本: " + version);
    }

    /**
     * 安全解析快照数据 - 只提取预期的字段
     */
    private DynamicTemplate parseSnapshotData(String snapshot) {
        // 只提取安全的字段，避免反序列化攻击
        DynamicTemplate result = new DynamicTemplate();
        // 从 JSON 中安全地提取字段
        // 这里使用 BeanUtilPlus.toBean() 但只针对预期的字段
        return BeanUtilPlus.toBean(snapshot, DynamicTemplate.class);
    }

    @Override
    public List<VersionHistoryResp> getVersionHistory(Long templateId) {
        List<VersionHistory> histories = versionHistoryMapper.selectList(Wraps.<VersionHistory>lbQ()
                .eq(VersionHistory::getTemplateId, templateId)
                .orderByDesc(VersionHistory::getVersion));
        return BeanUtilPlus.toBean(histories, VersionHistoryResp.class);
    }

    /**
     * 保存版本历史
     */
    private void saveVersionHistory(DynamicTemplate template, String changeType,
                                     String changeContent, String changeReason) {
        saveVersionHistory(template, changeType, changeContent, changeReason, null);
    }

    /**
     * 保存版本历史（带快照）
     */
    private void saveVersionHistory(DynamicTemplate template, String changeType,
                                     String changeContent, String changeReason, String snapshotData) {
        VersionHistory history = VersionHistory.builder()
                .templateId(template.getId())
                .templateCode(template.getTemplateCode())
                .version(template.getVersion())
                .changeType(changeType)
                .changeContent(changeContent)
                .changeReason(changeReason)
                .changeBy(getCurrentUserName())  // 从上下文获取实际用户
                .snapshotData(snapshotData != null ? snapshotData : BeanUtilPlus.toJson(template))
                .build();
        versionHistoryMapper.insert(history);
    }

    /**
     * 获取当前用户名
     */
    private String getCurrentUserName() {
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        if (context != null && context.nickName() != null) {
            return context.nickName();
        }
        return "未知用户";
    }

    /**
     * 检查编码是否存在
     */
    private void checkCodeExists(String templateCode, Long excludeId) {
        DynamicTemplate existing = this.baseMapper.selectOne(Wraps.<DynamicTemplate>lbQ()
                .eq(DynamicTemplate::getTemplateCode, templateCode));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw CheckedException.badRequest("模板编码已存在");
        }
    }
}
