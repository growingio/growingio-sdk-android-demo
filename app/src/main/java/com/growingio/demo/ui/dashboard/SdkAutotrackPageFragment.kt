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
import com.growingio.demo.databinding.FragmentAutotrackPageBinding
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
class SdkAutotrackPageFragment : PageFragment<FragmentAutotrackPageBinding>() {

    @SourceCode
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置当前页面可以发送Page页面事件
        GrowingAutotracker.get().autotrackPage(this, this.javaClass.simpleName)
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAutotrackPageBinding {
        return FragmentAutotrackPageBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_autotrack_page_title))

        pageBinding.aliasBtn.setOnClickListener {
            val alias = pageBinding.alias.editText?.text
            if (alias == null || alias.toString().isEmpty()) {
                showMessage(R.string.sdk_autotrack_page_alias_toast)
                return@setOnClickListener
            }
            sendPageAlias(alias.toString())
        }

        pageBinding.addBtn.setOnClickListener {
            pageBinding.controlBtn.clearChecked()
            if (pageBinding.attributeList.attributeSize() > 8) {
                showMessage(R.string.sdk_autotrack_page_size_toast)
                return@setOnClickListener
            }
            pageBinding.attributeList.addAttribute()
        }

        pageBinding.subBtn.setOnClickListener {
            pageBinding.controlBtn.clearChecked()
            if (pageBinding.attributeList.attributeSize() <= 0) {
                return@setOnClickListener
            }
            pageBinding.attributeList.subAttribute()
        }

        pageBinding.attributeBtn.setOnClickListener {
            if (pageBinding.attributeList.validAttributes()) {
                sendPageAttributes(pageBinding.attributeList.getAttributes())
            } else {
                showMessage(R.string.sdk_autotrack_page_attribute_toast)
            }
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug page")
    }

    @SourceCode
    private fun sendPageAttributes(attributes: Map<String, String>) {
        // 多次设置当前页面属性，不会多次发送Page事件
        // 页面属性将会合并至无埋点事件中
        GrowingAutotracker.get().setPageAttributes(this, attributes)
    }

    @SourceCode
    private fun sendPageAlias(alias: String) {
        // 多次设置当前页面别名，不会多次发送Page事件
        GrowingAutotracker.get().autotrackPage(this, alias)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 13,
                icon = SdkIcon.Api,
                title = "页面事件",
                desc = "设置发送Page事件的API",
                route = PageNav.SdkAutotrackerPage.route(),
                fragmentClass = SdkAutotrackPageFragment::class,
            )
        }
    }
}
