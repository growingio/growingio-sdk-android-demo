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
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComposeBinding
import com.growingio.demo.databinding.FragmentInitBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.util.MarkwonManager
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class ComponentComposeFragment : PageFragment<FragmentComposeBinding>() {

    @Inject
    lateinit var markwonManager: MarkwonManager

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComposeBinding {
        return FragmentComposeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_init_title))

        markwonManager.renderMarkdown(pageBinding.content, initContent())

        loadAssetCode(requireContext().applicationContext)
    }

    private fun initContent(): String {
        return """
            
            
        """.trimIndent()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 28,
                title = "Android Jetpack Compose",
                desc = "使用 Compose 插件，支持 Jetpack Compose 框架。",
                icon = SdkIcon.Component,
                route = PageNav.ComponentComposePage.route(),
                fragmentClass = ComponentComposeFragment::class,
            )
        }
    }
}
