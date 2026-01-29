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

package com.wemirr.platform.suite.dynamic.field.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字段保存请求
 *
 * @author Levin
 */
@Data
public class FieldSaveReq {

    @NotBlank(message = "字段名称不能为空")
    @Schema(description = "字段名称")
    private String fieldName;

    @NotBlank(message = "字段标签不能为空")
    @Schema(description = "字段标签")
    private String fieldLabel;

    @NotBlank(message = "字段类型不能为空")
    @Schema(description = "字段类型")
    private String fieldType;

    @NotBlank(message = "插件类型不能为空")
    @Schema(description = "关联插件类型")
    private String pluginType;

    @Schema(description = "关联模板ID")
    private Long templateId;

    @Schema(description = "字段长度")
    private Integer fieldLength;

    @Schema(description = "小数精度")
    private Integer decimalPlaces;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "占位符")
    private String placeholder;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "是否唯一")
    private Boolean unique;

    @Schema(description = "是否可搜索")
    private Boolean searchable;

    @Schema(description = "是否可排序")
    private Boolean sortable;

    @Schema(description = "是否可编辑")
    private Boolean editable;

    @Schema(description = "是否隐藏")
    private Boolean hidden;

    @Schema(description = "是否在列表显示")
    private Boolean listDisplay;

    @Schema(description = "字段顺序")
    private Integer fieldOrder;

    @Schema(description = "字段配置(JSON)")
    private String fieldConfig;

    @Schema(description = "验证规则(JSON)")
    private String validationRules;

    @Schema(description = "组件属性(JSON)")
    private String componentProps;

    @Schema(description = "字段描述")
    private String description;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "公司ID")
    private Long companyId;
}
