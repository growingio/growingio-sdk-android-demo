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
import com.growingio.android.oaid.OaidConfig
import com.growingio.android.oaid.OaidLibraryGioModule
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.TrackEventGenerator
import com.growingio.android.sdk.track.middleware.OaidHelper
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentOaidBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class ComponentOaidFragment : PageFragment<FragmentComponentOaidBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentOaidBinding {
        return FragmentComponentOaidBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_oaid))

        pageBinding.oaid.setEndIconOnClickListener {
            val randomOaid = UUID.randomUUID().toString()
            pageBinding.oaid.editText?.setText(randomOaid)
        }

        pageBinding.settleBtn.setOnClickListener {
            val loader = GrowingAutotracker.get().context.registry.getModelLoader(OaidHelper::class.java)
            if (loader != null) {
                showMessage(R.string.component_oaid_toast2)
                return@setOnClickListener
            }
            val oaid = pageBinding.oaid.editText?.text
            if (oaid == null || oaid.toString().isEmpty()) {
                showMessage(R.string.component_oaid_toast)
                return@setOnClickListener
            }
            registerOaidComponent(oaid.toString())
        }

        pageBinding.testBtn.setOnClickListener {
            // send visit event
            TrackEventGenerator.generateVisitEvent()
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug visit")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterOaidComponent()
    }

    @SourceCode
    private fun registerOaidComponent(oaid: String) {

        val oaidConfig = OaidConfig()
        oaidConfig.setProvideOaid(oaid)

        // 推荐在SDK初始化时先注册Oaid模块,能一开始就上传OAID设备标识符
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            AutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(OaidLibraryGioModule(), oaidConfig))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(OaidLibraryGioModule(), oaidConfig)
    }

    private fun unregisterOaidComponent() {
        // oaid will store in app's memory, so it always send to server until app exit
        GrowingAutotracker.get().context.registry.unregister(OaidHelper::class.java, String::class.java)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 22,
                icon = SdkIcon.Component,
                title = "OAID 标识符",
                desc = "移动智能终端补充设备标识符，由国内移动安全联盟MSA统一提供。",
                route = PageNav.ComponentOaidPage.route(),
                fragmentClass = ComponentOaidFragment::class
            )
        }
    }
}