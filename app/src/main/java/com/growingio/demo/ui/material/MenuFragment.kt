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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialMenuBinding
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
class MenuFragment : ViewBindingFragment<FragmentMaterialMenuBinding>(), MenuProvider {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaterialMenuBinding {
        return FragmentMaterialMenuBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).addMenuProvider(this)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        binding.toolbar.setTitle(R.string.material_menu)

        binding.menuShow.setOnClickListener {
            showMenu()
        }
        binding.menuShowPopup.setOnClickListener {
            showListPopupMenu()
        }

        registerForContextMenu(binding.menuShowContext)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_popup, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        showMessage(menuItem.title.toString(), binding.root)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).removeMenuProvider(this)
    }

    private fun showMenu() {
        val popup = PopupMenu(requireContext(), binding.menuShow)
        popup.menuInflater.inflate(R.menu.menu_popup, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            showMessage(menuItem.title.toString(), binding.root)
            true
        }
        popup.show()
    }

    private fun showListPopupMenu() {
        val popup = ListPopupWindow(requireContext())
        val adapter = ArrayAdapter<CharSequence>(
            requireContext(),
            R.layout.fragment_material_menu_item,
            resources.getStringArray(R.array.material_menu_list)
        )
        popup.setAdapter(adapter)
        popup.anchorView = binding.menuShowPopup
        popup.setOnItemClickListener { parent, view, position, id ->
            showMessage(adapter.getItem(position).toString(), binding.root)
            popup.dismiss()
        }
        popup.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(android.R.string.copy).setOnMenuItemClickListener {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(
                ClipData.newPlainText(
                    "Menu Copy",
                    (v as TextView).text
                )
            )
            true
        }
        menu.add(R.string.material_test_btn).setOnMenuItemClickListener { menuItem ->
            (v as TextView).setText(R.string.material_test_btn)
            true
        }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 7,
                icon = R.drawable.ic_menu_list,
                title = "Menus",
                route = PageNav.MaterialMenuPage.route(),
                fragmentClass = MenuFragment::class
            )
        }
    }
}