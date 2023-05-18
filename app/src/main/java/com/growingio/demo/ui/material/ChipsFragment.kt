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
import com.google.android.material.chip.Chip
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialButtonBinding
import com.growingio.demo.databinding.FragmentMaterialCheckboxBinding
import com.growingio.demo.databinding.FragmentMaterialChipsBinding
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
class ChipsFragment : ViewBindingFragment<FragmentMaterialChipsBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaterialChipsBinding {
        return FragmentMaterialChipsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textArray = resources.getStringArray(R.array.material_chip_group)
        textArray.forEach {
            val chip = layoutInflater.inflate(R.layout.fragment_material_chips_item, binding.chipGroup, false) as Chip
            chip.text = it
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                binding.chipGroup.removeView(it)
            }
            binding.chipGroup.addView(chip)
        }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 5,
                icon = R.drawable.ic_chips,
                title = "Chips",
                route = PageNav.MaterialChipsPage.route(),
                fragmentClass = ChipsFragment::class
            )
        }
    }
}