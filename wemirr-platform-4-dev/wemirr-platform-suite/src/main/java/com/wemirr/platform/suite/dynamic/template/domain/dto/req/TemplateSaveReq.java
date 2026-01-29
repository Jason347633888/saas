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

package com.wemirr.platform.suite.dynamic.template.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 模板保存请求
 *
 * @author Levin
 */
@Data
public class TemplateSaveReq {

    @NotBlank(message = "模板编码不能为空")
    @Schema(description = "模板编码")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称")
    private String templateName;

    @NotBlank(message = "模板类型不能为空")
    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "关联表单模型ID")
    private Long modelId;

    @Schema(description = "模板分类")
    private String category;

    @NotNull(message = "状态不能为空")
    @Schema(description = "是否为草稿")
    private Boolean isDraft;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "扩展配置(JSON)")
    private String extConfig;

    @Schema(description = "公司ID")
    private Long companyId;
}
