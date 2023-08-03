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
        GrowingAutotracker.get().context.configurationProvider.core().isDebugEnabled = true
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentProtobufBinding {
        return FragmentComponentProtobufBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_protobuf))

        pageBinding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    pageBinding.protobuf.id -> {
                        registerProtobufComponent()
                        pageBinding.sdkDataHint.setText(R.string.sdk_data_protobuf_hint)
                    }

                    pageBinding.json.id -> {
                        registerProtobufComponent()
                        pageBinding.sdkDataHint.setText(R.string.sdk_data_json_hint)
                    }
                }
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
         *            AutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(ProtobufLibraryModule()))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(ProtobufLibraryModule())
    }

    @SourceCode
    fun registerJsonComponent() {
        // 可以选择在SDK初始化时先注册模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            AutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(JsonLibraryModule()))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(JsonLibraryModule())
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset default sdk state
        GrowingAutotracker.get().context.configurationProvider.core().isDebugEnabled = BuildConfig.DEBUG
        GrowingAutotracker.get().registerComponent(ProtobufLibraryModule())
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
                title = "数据格式",
                desc = "SDK目前可以支持 Protobuf 和 Json 数据格式上传。",
                route = PageNav.ComponentProtobufPage.route(),
                fragmentClass = ComponentProtobufFragment::class
            )
        }
    }
}