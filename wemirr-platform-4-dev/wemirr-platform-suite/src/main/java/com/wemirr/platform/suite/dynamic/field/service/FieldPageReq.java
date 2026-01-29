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

package com.wemirr.platform.suite.dynamic.field.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段分页查询请求
 *
 * @author Levin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldPageReq extends com.wemirr.framework.db.mybatisplus.entity.PageRequest {

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "字段标签")
    private String fieldLabel;

    @Schema(description = "字段类型")
    private String fieldType;

    @Schema(description = "关联模板ID")
    private Long templateId;

    @Schema(description = "插件类型")
    private String pluginType;

    @Schema(description = "状态")
    private Boolean status;
}
