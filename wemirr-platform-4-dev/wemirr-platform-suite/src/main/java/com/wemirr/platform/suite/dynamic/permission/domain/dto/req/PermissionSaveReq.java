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

package com.wemirr.platform.suite.dynamic.permission.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 权限保存请求
 *
 * @author Levin
 */
@Data
public class PermissionSaveReq {

    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "关联模板ID")
    private Long templateId;

    @NotBlank(message = "权限类型不能为空")
    @Schema(description = "权限类型")
    private String permissionType;

    @Schema(description = "授权对象类型")
    private String grantType;

    @Schema(description = "授权对象ID(JSON数组)")
    private String grantIds;

    @Schema(description = "数据范围类型")
    private String dataScopeType;

    @Schema(description = "数据范围条件(JSON)")
    private String dataScopeCondition;

    @Schema(description = "是否继承子部门")
    private Boolean includeSubDept;

    @Schema(description = "字段权限(JSON)")
    private String fieldPermissions;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "权限描述")
    private String description;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "公司ID")
    private Long companyId;
}
