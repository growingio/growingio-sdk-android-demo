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

package com.growingio.demo.ui.material

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentNotificationsBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.giokit.GioKit
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/5/11
 */
class RecyclerViewFragment : ViewBindingFragment<FragmentNotificationsBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNotificationsBinding {
        return FragmentNotificationsBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        GioKit.attach(requireActivity())
    }

    override fun onDetach() {
        super.onDetach()
        GioKit.detach(requireActivity())
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 1,
                icon = R.drawable.ic_sdk_component,
                title = "RecyclerView",
                route = PageNav.MaterialRecyclerViewPage.route(),
                fragmentClass = RecyclerViewFragment::class
            )
        }
    }
}