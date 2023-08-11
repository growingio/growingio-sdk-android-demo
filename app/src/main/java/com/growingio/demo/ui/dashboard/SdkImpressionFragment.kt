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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_impression))

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
            pageBinding.scrollView.fullScroll(View.FOCUS_DOWN)
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug ImpressionProvider")
    }

    @SourceCode
    private fun setViewImpression() {
        GrowingAutotracker.get()
            .trackViewImpression(pageBinding.impView, "ImpressionProvider", mapOf("type" to "visible"))

        GrowingAutotracker.get()
            .trackViewImpression(pageBinding.impScrollView, "ImpressionProvider", mapOf("type" to "scroll"))
    }

    @SourceCode
    private fun cleanViewImpression() {
        GrowingAutotracker.get().stopTrackViewImpression(pageBinding.impView)

        GrowingAutotracker.get().stopTrackViewImpression(pageBinding.impScrollView)
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanViewImpression()
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
