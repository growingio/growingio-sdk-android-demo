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
import androidx.activity.result.ActivityResultLauncher
import com.growingio.android.advert.AdvertConfig
import com.growingio.android.advert.AdvertLibraryGioModule
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.log.Logger
import com.growingio.android.sdk.track.middleware.advert.Activate
import com.growingio.android.sdk.track.middleware.advert.AdvertResult
import com.growingio.android.sdk.track.middleware.advert.DeepLinkCallback
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentAdvertBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.ui.camera.BarScanner
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
class ComponentAdvertFragment : PageFragment<FragmentComponentAdvertBinding>() {

    private lateinit var barScannerLauncher: ActivityResultLauncher<Void?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barScannerLauncher = registerForActivityResult(BarScanner(requireContext())) {
            pageBinding.deeplink.editText?.setText(it)
        }
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentAdvertBinding {
        return FragmentComponentAdvertBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_advert))

        pageBinding.advertSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                registerAdvertComponent()
            } else {
                unregisterAdvertComponent()
            }
        }

        pageBinding.deeplink.setEndIconOnClickListener {
            barScannerLauncher.launch(null)
        }

        pageBinding.defaultBtn.setOnClickListener {
            pageBinding.deeplink.editText?.setText(getString(R.string.component_advert_default_url))
        }

        pageBinding.parseBtn.setOnClickListener {
            val deeplinkUrl = pageBinding.deeplink.editText?.text
            if (deeplinkUrl == null || deeplinkUrl.toString().isEmpty()) {
                showMessage(R.string.component_advert_toast)
                return@setOnClickListener
            }
            parseDeepLinkManual(deeplinkUrl.toString())
        }

        loadAssetCode(this)

        // https://ads-uat.growingio.cn/k4budVa
        setDefaultLogFilter("level:debug advertModule")
    }

    @SourceCode
    fun registerAdvertComponent() {
        val config = AdvertConfig()
        config.deepLinkHost = "https://ads-uat.growingio.cn"
        config.deepLinkCallback = DeepLinkCallback { params, error, appAwakePassedTime ->
            // params: 深度链接参数
            // error: 错误信息
            // appAwakePassedTime: 从app唤醒到调用该回调的时间
            Logger.d(
                "AdvertModule",
                "DeepLinkCallback: params: $params, error: $error, appAwakePassedTime: $appAwakePassedTime",
            )
        }

        // 可以选择在SDK初始化时先注册广告模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            AutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(AdvertLibraryGioModule(), config))
         */

        // 也可以在运行时再注册
        GrowingAutotracker.get().registerComponent(AdvertLibraryGioModule(), config)
    }

    @SourceCode
    fun parseDeepLinkManual(url: String) {
        GrowingAutotracker.get()
            .doDeepLinkByUrl(url) { params, error, appAwakePassedTime ->
                // for test
                GrowingAutotracker.get().trackCustomEvent("DeepLinkCallback")
                Logger.d(
                    "AdvertModule",
                    "DeepLinkCallback: params: $params, error: $error, appAwakePassedTime: $appAwakePassedTime",
                )
            }
    }

    private fun unregisterAdvertComponent() {
        GrowingAutotracker.get().context.registry.unregister(Activate::class.java, AdvertResult::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset default sdk state
        unregisterAdvertComponent()
        barScannerLauncher.unregister()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 24,
                icon = SdkIcon.Component,
                title = "广告分析",
                desc = "广告模块包括激活事件和深度链接，激活事件是当用户第一次进入的事件，深度链接用于SDK监测广告推广效果",
                route = PageNav.ComponentAdvertPage.route(),
                fragmentClass = ComponentAdvertFragment::class,
            )
        }
    }
}
