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

import android.os.Bundle
import android.view.*
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialButtonBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.ViewBindingFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/5/15
 */
class ButtonFragment : ViewBindingFragment<FragmentMaterialButtonBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaterialButtonBinding {
        return FragmentMaterialButtonBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.materialButton.setOnClickListener {}

        binding.toggleGroup.addOnButtonCheckedListener { _, _, _ -> }

        binding.fab.setOnClickListener { }
        binding.expanedFab.setOnClickListener { }
        binding.radioGroup.setOnCheckedChangeListener { _, _ ->  }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 3,
                icon = R.drawable.ic_button,
                title = "Material Button",
                route = PageNav.MaterialButtonPage.route(),
                fragmentClass = ButtonFragment::class
            )
        }
    }
}