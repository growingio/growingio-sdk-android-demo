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
import com.growingio.android.encoder.EncoderLibraryGioModule
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.middleware.http.EventEncoder
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.BuildConfig
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentEncoderBinding
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
class ComponentEncoderFragment : PageFragment<FragmentComponentEncoderBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // open sdk debug log
        ConfigurationProvider.core().isDebugEnabled = true
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentEncoderBinding {
        return FragmentComponentEncoderBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_encoder))



        pageBinding.encoderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                registerEncoderComponent()
            } else {
                unregisterEncoderComponent()
            }
        }
        pageBinding.testBtn.setOnClickListener {
            // just send autotracker click event
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:verbose EventHttpSender")
    }

    @SourceCode
    fun registerEncoderComponent() {
        // 可以选择在SDK初始化时先注册加密模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            CdpTrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(EncoderLibraryGioModule()))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(EncoderLibraryGioModule())
    }

    private fun unregisterEncoderComponent() {
        TrackerContext.get().registry.unregister(EventEncoder::class.java, EventEncoder::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset default sdk state
        unregisterEncoderComponent()
        ConfigurationProvider.core().isDebugEnabled = BuildConfig.DEBUG
    }


    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 21,
                icon = SdkIcon.Component,
                title = "数据加密",
                desc = "SDK 加密模块默认使用 snappy 数据压缩和 xor 简单加密方式，作用于数据网络上传的阶段",
                route = PageNav.ComponentEncoderPage.route(),
                fragmentClass = ComponentEncoderFragment::class
            )
        }
    }
}