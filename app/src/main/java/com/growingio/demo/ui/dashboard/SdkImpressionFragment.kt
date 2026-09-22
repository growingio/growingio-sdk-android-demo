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
import com.growingio.android.sdk.autotrack.impression.ImpressionConfig
import com.growingio.android.sdk.autotrack.impression.SimpleImpressionListener
import com.growingio.android.sdk.track.log.Logger
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentImpressionBinding
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
class SdkImpressionFragment : PageFragment<FragmentImpressionBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImpressionBinding {
        return FragmentImpressionBinding.inflate(inflater, container, false)
    }

    private val impressionListener = object : SimpleImpressionListener() {
        override fun onImpressionTracked(view: View, eventName: String, identifier: String?) {
            Logger.d("ImpressionProvider", "impression tracked: $eventName, identifier: $identifier")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_impression))

        GrowingAutotracker.get().addViewImpressionListener(impressionListener)

        pageBinding.impressionSwitch.setOnCheckedChangeListener { _, isChecked ->
            pageBinding.impView.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        pageBinding.settleBtn.setOnClickListener {
            setViewImpression()
        }

        pageBinding.clearBtn.setOnClickListener {
            cleanViewImpression()
        }

        pageBinding.impScroll.setOnClickListener {
            updateViewImpression()
            pageBinding.scrollView.fullScroll(View.FOCUS_DOWN)
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug ImpressionProvider")
    }

    @SourceCode
    private fun setViewImpression() {
        // 露出即算曝光，离开可视区再进入会再次曝光
        GrowingAutotracker.get()
            .trackViewImpression(pageBinding.impView, "ImpressionProvider", mapOf("type" to "visible"))

        // identifier 填业务上能唯一标识这个元素的值，它是"只曝光一次"的判定口径，也是精确移除的 key；
        // config 只对该元素生效，优先于 AutotrackConfiguration 里的全局配置
        val config = ImpressionConfig.create(0.5f, 1000L, true)
        GrowingAutotracker.get()
            .trackViewImpression(
                pageBinding.impScrollView,
                "ImpressionProvider",
                mapOf("type" to "scroll"),
                "scroll_element",
                config,
            )
    }

    @SourceCode
    private fun updateViewImpression() {
        // 只替换属性，不影响曝光状态；重新标记才会重置曝光状态
        GrowingAutotracker.get()
            .updateViewImpressionAttributes(
                pageBinding.impScrollView,
                mapOf("type" to "scroll", "price" to "20"),
                "scroll_element",
            )
    }

    @SourceCode
    private fun cleanViewImpression() {
        // 移除该视图上的全部标记
        GrowingAutotracker.get().stopTrackViewImpression(pageBinding.impView)

        // 只移除 identifier 对应的那一个标记
        GrowingAutotracker.get().stopTrackViewImpression(pageBinding.impScrollView, "scroll_element")

        // 清除"只曝光一次"的记录，下拉刷新、切换账号等场景需要
        GrowingAutotracker.get().resetAllViewImpressionState()
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanViewImpression()
        GrowingAutotracker.get().removeViewImpressionListener(impressionListener)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 14,
                icon = SdkIcon.Api,
                title = "曝光事件",
                desc = "当被设置的View出现在屏幕内时将触发曝光事件",
                route = PageNav.SdkImpressionPage.route(),
                fragmentClass = SdkImpressionFragment::class,
            )
        }
    }
}
