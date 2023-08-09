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
import com.growingio.demo.databinding.FragmentUniqueTagBinding
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
class SdkUniqueTagFragment : PageFragment<FragmentUniqueTagBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUniqueTagBinding {
        return FragmentUniqueTagBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_unique_tag))

        pageBinding.settleBtn.setOnClickListener {
            val uniqueTag = pageBinding.uniqueTag.editText?.text
            if (uniqueTag == null || uniqueTag.toString().isEmpty()) {
                showMessage(R.string.sdk_unique_tag_toast)
                return@setOnClickListener
            }
            setTestBtnUniqueTag(uniqueTag.toString())
        }

        pageBinding.testBtn.setOnClickListener {
            // nothing
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug unique")
    }

    @SourceCode
    private fun setTestBtnUniqueTag(tag: String) {
        GrowingAutotracker.get().setUniqueTag(pageBinding.testBtn, tag)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 15,
                icon = SdkIcon.Api,
                title = "唯一路径",
                desc = "给View设置唯一的Tag，方便点击等事件确定唯一的View路径，一般用于动态布局的场景",
                route = PageNav.SdkUniqueTagPage.route(),
                fragmentClass = SdkUniqueTagFragment::class,
            )
        }
    }
}
