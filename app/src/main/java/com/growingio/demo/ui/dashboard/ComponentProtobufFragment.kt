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
import com.growingio.android.json.JsonLibraryModule
import com.growingio.android.protobuf.ProtobufLibraryModule
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.BuildConfig
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentProtobufBinding
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
class ComponentProtobufFragment : PageFragment<FragmentComponentProtobufBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // open sdk debug log
        ConfigurationProvider.core().isDebugEnabled = true
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentProtobufBinding {
        return FragmentComponentProtobufBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_protobuf))

        pageBinding.protobufSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                registerProtobufComponent()
            } else {
                unregisterProtobufComponent()
            }
        }
        pageBinding.testBtn.setOnClickListener {
            // just send autotracker click event
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:verbose EventHttpSender")
    }

    @SourceCode
    fun registerProtobufComponent() {
        // 可以选择在SDK初始化时先注册模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            CdpAutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(ProtobufLibraryModule()))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(ProtobufLibraryModule())
    }

    private fun unregisterProtobufComponent() {
        GrowingAutotracker.get().registerComponent(JsonLibraryModule())
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset default sdk state
        unregisterProtobufComponent()
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
                title = "Protobuf 压缩",
                desc = "Protobuf 数据模块使用 Google Protobuf 格式压缩和上传事件数据。",
                route = PageNav.ComponentProtobufPage.route(),
                fragmentClass = ComponentProtobufFragment::class
            )
        }
    }
}