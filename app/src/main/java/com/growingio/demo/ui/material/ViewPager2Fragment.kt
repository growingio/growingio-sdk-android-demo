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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialViewpager2Binding
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
class ViewPager2Fragment : ViewBindingFragment<FragmentMaterialViewpager2Binding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMaterialViewpager2Binding {
        return FragmentMaterialViewpager2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Hook Inject
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        val adapter = ViewPager2Adapter(childFragmentManager, lifecycle)
        binding.viewPager2.adapter = adapter

        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = getString(PAGE_ARRAY[position])
        }
        mediator.attach()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 14,
                icon = R.drawable.ic_viewpager,
                title = "ViewPager",
                route = PageNav.MaterialViewPagerPage.route(),
                fragmentClass = ViewPager2Fragment::class,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    inner class ViewPager2Adapter(fragmentManager: androidx.fragment.app.FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            val resId = PAGE_ARRAY[position]
            val content = getString(resId)
            return TestFragment.newInstance(content, position)
        }

        override fun getItemCount(): Int {
            return PAGE_ARRAY.size
        }
    }

    companion object {
        val PAGE_ARRAY = arrayOf(
            R.string.material_navigation_tablayout_all,
            R.string.material_navigation_tablayout_shopping,
            R.string.material_navigation_tablayout_maps,
            R.string.material_navigation_tablayout_images,
            R.string.material_navigation_tablayout_updates,
        )
    }
}
