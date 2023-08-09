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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialNavigationBinding
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
class NavigationFragment : ViewBindingFragment<FragmentMaterialNavigationBinding>(), MenuProvider {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMaterialNavigationBinding {
        return FragmentMaterialNavigationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).addMenuProvider(this)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        binding.drawer.addDrawerListener(
            ActionBarDrawerToggle(
                requireActivity(),
                binding.drawer,
                binding.toolbar,
                R.string.material_navigation_drawer_open,
                R.string.material_navigation_drawer_close,
            ),
        )

        binding.navigationView.setNavigationItemSelectedListener {
            binding.navigationView.setCheckedItem(it)
            binding.drawer.closeDrawer(GravityCompat.START)
            true
        }

        initNavigation(binding.navBar)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Hook Inject
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initNavigation(bottomNav: BottomNavigationView) {
        bottomNav.menu.apply {
            add(Menu.NONE, R.id.dashboard, 0, R.string.navi_dashboard_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_dashboard)
            }
            add(Menu.NONE, R.id.ui, 1, R.string.navi_material_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_ui)
            }
            add(Menu.NONE, R.id.template, 2, R.string.navi_template_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_template)
            }
        }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 8,
                icon = R.drawable.ic_bottomnavigation,
                title = "Navigation",
                route = PageNav.MaterialNavigationPage.route(),
                fragmentClass = NavigationFragment::class,
            )
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            binding.drawer.openDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).removeMenuProvider(this)
    }
}
