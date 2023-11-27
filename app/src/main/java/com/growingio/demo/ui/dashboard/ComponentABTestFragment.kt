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
import com.growingio.android.abtest.ABTestConfig
import com.growingio.android.abtest.ABTestLibraryGioModule
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.middleware.abtest.ABExperiment
import com.growingio.android.sdk.track.middleware.abtest.ABTest
import com.growingio.android.sdk.track.middleware.abtest.ABTestCallback
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentAbtestBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * <p>
 *
 * @author cpacm 2023/11/27
 */
@AndroidEntryPoint
class ComponentABTestFragment : PageFragment<FragmentComponentAbtestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAbTestComponent()
    }

    @SourceCode
    fun registerAbTestComponent() {
        val config = ABTestConfig()
        config.abTestServerHost = "http://117.50.84.75:10060"
        config.setAbTestExpired(10, TimeUnit.MINUTES)
        // 可以在运行时再注册
        GrowingAutotracker.get().registerComponent(ABTestLibraryGioModule(), config)

        // 也可以选择在SDK初始化时先注册AB测试模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *            AutotrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(ABTestLibraryGioModule(), config))
         */
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentAbtestBinding {
        return FragmentComponentAbtestBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_abtest))

        pageBinding.goBtn.setOnClickListener {
            val deeplinkUrl = pageBinding.abTestFetch.editText?.text
            if (deeplinkUrl == null || deeplinkUrl.toString().isEmpty()) {
                showMessage(R.string.component_abtest_toast)
                return@setOnClickListener
            }
            pageBinding.progressBar.visibility = View.VISIBLE
            pageBinding.progressBar.isIndeterminate = true
            fetchABTest(deeplinkUrl.toString())
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug abtest")
    }

    @SourceCode
    fun fetchABTest(layerId: String) {
        // 通过接口获取 A/B 测试分组
        GrowingAutotracker.get().getAbTest(
            layerId,
            object : ABTestCallback {
                override fun onABExperimentReceived(experiment: ABExperiment, dataType: Int) {
                    pageBinding.progressBar.visibility = View.GONE
                    pageBinding.resultLabel.text = loadExperimentData(experiment, dataType)
                }

                override fun onABExperimentFailed(error: Exception) {
                    pageBinding.progressBar.visibility = View.GONE
                    pageBinding.resultLabel.text = error.message
                }
            },
        )
    }

    private fun loadExperimentData(experiment: ABExperiment, dataType: Int): String {
        val sb = StringBuilder("数据来自：")
        when (dataType) {
            ABTestCallback.ABTEST_CACHE -> sb.append("缓存")
            ABTestCallback.ABTEST_HTTP -> sb.append("网络")
        }
        sb.append("\n")
        sb.append("实验层ID：").append(experiment.layerId).append("\n")
        sb.append("实验ID：").append(experiment.experimentId).append("\n")
        sb.append("分组ID：").append(experiment.strategyId).append("\n")
        sb.append("实现变量：").append(experiment.variables)
        return sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        GrowingAutotracker.get().context.registry.unregister(ABTest::class.java, ABExperiment::class.java)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 25,
                icon = SdkIcon.Component,
                title = "A/B 测试",
                desc = "能够通过接口获取A/B 测试分组",
                route = PageNav.ComponentABTestPage.route(),
                fragmentClass = ComponentABTestFragment::class,
            )
        }
    }
}
