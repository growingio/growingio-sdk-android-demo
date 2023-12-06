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
import com.growingio.demo.databinding.FragmentGeneralPropsBinding
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
class SdkGeneralPropsFragment : PageFragment<FragmentGeneralPropsBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGeneralPropsBinding {
        return FragmentGeneralPropsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_general_props_title))

        pageBinding.addBtn.setOnClickListener {
            pageBinding.controlBtn.clearChecked()
            if (pageBinding.attributeList.attributeSize() > 10) {
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
            clearGeneralProps()
            if (pageBinding.attributeList.validAttributes()) {
                setGeneralProps(pageBinding.attributeList.getAttributes())
            } else {
                showMessage(R.string.sdk_general_props_attribute_toast)
            }
        }

        pageBinding.testBtn.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("EventBuilderProvider_Props")
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug EventBuilderProvider")
    }

    @SourceCode
    private fun setGeneralProps(properties: Map<String, String>) {
        // 设置动态通用属性，这些属性将会合并至自定义事件中
        // 在运行期间多次设置会合并通用属性，相同属性则会覆盖旧的属性值
        GrowingAutotracker.get().setGeneralProps(properties)
    }

    @SourceCode
    private fun clearGeneralProps() {
        // 清除运行期间设置的通用属性
        GrowingAutotracker.get().clearGeneralProps()

        // 清除特定 key 值的通用属性
        // GrowingAutotracker.get().removeGeneralProps(key1,key2)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearGeneralProps()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 17,
                icon = SdkIcon.Api,
                title = "通用属性",
                desc = "为自定义事件设置通用属性",
                route = PageNav.SdkGeneralPopsPage.route(),
                fragmentClass = SdkGeneralPropsFragment::class,
            )
        }
    }
}
