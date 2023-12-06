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
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentHybridBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
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
class ComponentHybridFragment : PageFragment<FragmentComponentHybridBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentHybridBinding {
        return FragmentComponentHybridBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_hybrid))

        pageBinding.hybridWebView.initGioHybridHtml()

        loadAssetCode(this)

        setDefaultLogFilter("level:debug HybridBridge")
    }

    @SourceCode
    fun initWithTracker() {
        // 在无埋点SDK中，SDK 会自动注入桥接代码，实现数据打通。

        // 若是在埋点SDK中可以在初始化SDK时，可以提前注册hybrid模块
        /**
         * GrowingTracker.startWithConfiguration(this,
         *            TrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(HybridLibraryGioModule()))
         */

        // 或者在运行时再注册
        // GrowingTracker.get().registerComponent(HybridLibraryGioModule())

        // 最后需要在 WebView 初始化之后调用桥接代码，实现访问用户数据打通:
        // GrowingTracker.get().bridgeWebView(pageBinding.hybridWebView)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 20,
                icon = SdkIcon.Component,
                title = "内嵌H5",
                desc = "应用内嵌H5页面进行数据采集时将数据传输至SDK发送",
                route = PageNav.ComponentHybridPage.route(),
                fragmentClass = ComponentHybridFragment::class,
            )
        }
    }
}
