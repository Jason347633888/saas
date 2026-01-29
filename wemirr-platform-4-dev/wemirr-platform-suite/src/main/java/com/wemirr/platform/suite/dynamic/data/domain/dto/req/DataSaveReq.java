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

package com.wemirr.platform.suite.dynamic.data.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据保存请求
 *
 * @author Levin
 */
@Data
public class DataSaveReq {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "关联模板ID")
    private Long templateId;

    @Schema(description = "数据标题")
    private String dataTitle;

    /**
     * 数据状态 - 不允许用户设置，由系统自动管理
     * 禁止使用 @NotNull 注解，防止用户绕过状态机
     */
    @Schema(description = "数据状态（系统自动设置，用户不可修改）")
    private String dataStatus;

    @NotBlank(message = "数据内容不能为空")
    @Schema(description = "数据JSON")
    private String dataContent;

    @Schema(description = "附件JSON")
    private String attachments;

    @Schema(description = "扩展字段1")
    private String ext1;

    @Schema(description = "扩展字段2")
    private String ext2;

    @Schema(description = "扩展字段3")
    private String ext3;
}
