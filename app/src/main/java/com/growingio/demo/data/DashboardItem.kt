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

import com.growingio.demo.R

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
data class DashboardItem(
    val id: Int,
    val route: String,
    val icon: SdkIcon? = null,
    val title: String? = null,
    val desc: String? = null)

sealed interface SdkIcon {

    val icon: Int

    object Config : SdkIcon {
        override val icon: Int
            get() = R.drawable.ic_sdk_config
    }

    object Api : SdkIcon {
        override val icon: Int
            get() = R.drawable.ic_sdk_api
    }

    object Component : SdkIcon {
        override val icon: Int
            get() = R.drawable.ic_sdk_component
    }
}
