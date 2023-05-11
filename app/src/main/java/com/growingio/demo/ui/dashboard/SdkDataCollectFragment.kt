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
import com.growingio.demo.databinding.FragmentDataCollectBinding
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
class SdkDataCollectFragment : PageFragment<FragmentDataCollectBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDataCollectBinding {
        return FragmentDataCollectBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_data_collect))

        initSdkDataCollectView()

        loadAssetCode(this)

        setDefaultLogFilter("level:debug DataCollection")
    }

    @SourceCode
    private fun initSdkDataCollectView() {
        pageBinding.collectSwitch.setOnCheckedChangeListener { _, isChecked ->
            GrowingAutotracker.get().setDataCollectionEnabled(isChecked)
        }

        pageBinding.collectTestBtn.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("DataCollection")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GrowingAutotracker.get().setDataCollectionEnabled(true)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 10,
                icon = SdkIcon.Api,
                title = "数据开关",
                desc = "数据开关可以控制SDK是否采集和发数",
                route = PageNav.SdkDataCollectPage.route(),
                fragmentClass = SdkDataCollectFragment::class
            )
        }
    }
}