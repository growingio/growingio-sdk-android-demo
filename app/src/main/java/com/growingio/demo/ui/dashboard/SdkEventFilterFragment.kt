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

package com.growingio.demo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentEventFilterBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.util.GrowingIOManager
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class SdkEventFilterFragment : PageFragment<FragmentEventFilterBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GrowingIOManager.configDemoEventFilterInterceptor()
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEventFilterBinding {
        return FragmentEventFilterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_filter_title))

        loadAssetCode(GrowingIOManager)

        pageBinding.typeFilterButton.setOnClickListener {
            GrowingAutotracker.get().setLoginUserAttributes(hashMapOf("userName" to "cpacm"))
        }

        pageBinding.pathFilterButton.setOnClickListener { }

        pageBinding.customFilterButton.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("filter")
        }

        pageBinding.fieldFilterButton.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("filter_field")
        }

        setDefaultLogFilter("level:debug filter")
    }

    override fun onDestroy() {
        super.onDestroy()
        GrowingIOManager.resetEventFilterInterceptor()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 3,
                icon = SdkIcon.Config,
                title = "SDK 事件过滤",
                desc = "如何在初始化中设置事件过滤",
                route = PageNav.SdkEventFilterPage.route(),
                fragmentClass = SdkEventFilterFragment::class,
            )
        }
    }
}
