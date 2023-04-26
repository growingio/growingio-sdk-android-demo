/*
 *   Copyright (c) - 2023 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.growingio.demo.data

import com.growingio.demo.navgraph.PageNav

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
val dashboardItems = listOf(
    DashboardItem(
        id = 1,
        route = PageNav.SdkInitPage.route(),
        icon = SdkIcon.Config,
        title = "SDK 初始化配置",
        desc = "初始化配置相关默认值"
    ),
    DashboardItem(
        id = 2,
        route = PageNav.SdkEventFilterPage.route(),
        icon = SdkIcon.Config,
        title = "SDK 事件过滤",
        desc = "如何在初始化中设置事件过滤"
    ),
    DashboardItem(
        id = 3,
        route = "api",
        icon = SdkIcon.Api,
        title = "SDK API",
        desc = "SDK API 相关"
    ),
    DashboardItem(
        id = 4,
        route = "component",
        icon = SdkIcon.Component,
        title = "SDK 组件",
        desc = "SDK 组件相关"
    )
)