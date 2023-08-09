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
import androidx.appcompat.app.AlertDialog
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialDialogBinding
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
class AppCompatDialogFragment : ViewBindingFragment<FragmentMaterialDialogBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMaterialDialogBinding {
        return FragmentMaterialDialogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.launchDialog.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("This is title")
                .setMessage("This is a loooooooooooooooong Message")
                .setPositiveButton("Accept") { _, _ -> }
                .setNegativeButton("Decline", null)
                .setNeutralButton("Cancel", null)
                .setCancelable(true)
                .show()
        }

        val choices = arrayOf("Choice1", "Choice2", "Choice3")
        binding.itemsDialog.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("This is title")
                .setPositiveButton("Accept") { _, _ -> }
                .setNegativeButton("Decline", null)
                .setNeutralButton("Cancel", null)
                .setSingleChoiceItems(choices, 1) { _, _ -> }
                .setCancelable(true)
                .show()
        }
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 6,
                icon = R.drawable.ic_dialog,
                title = "AlertDialog",
                route = PageNav.MaterialDialogPage.route(),
                fragmentClass = AppCompatDialogFragment::class,
            )
        }
    }
}
