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

package com.wemirr.platform.suite.dynamic.permission.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wemirr.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据权限实体
 *
 * @author Levin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_dynamic_permission")
public class DynamicPermission extends SuperEntity<Long> {

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "关联模板ID")
    private Long templateId;

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

    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "公司ID")
    private Long companyId;
}
