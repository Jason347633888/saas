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

package com.wemirr.platform.suite.dynamic.template.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.wemirr.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 动态模板实体
 *
 * @author Levin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_dynamic_template")
public class DynamicTemplate extends SuperEntity<Long> {

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "关联表单模型ID")
    private Long modelId;

    @Schema(description = "模板分类")
    private String category;

    @Schema(description = "版本号")
    @Version
    private Integer version;

    @Schema(description = "是否为草稿")
    private Boolean isDraft;

    @Schema(description = "是否为当前版本")
    private Boolean isCurrent;

    @Schema(description = "发布状态")
    private String publishStatus;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "扩展配置(JSON)")
    private String extConfig;

    @Schema(description = "公司ID")
    private Long companyId;
}
