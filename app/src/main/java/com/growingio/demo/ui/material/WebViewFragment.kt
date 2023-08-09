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
import androidx.activity.result.ActivityResultLauncher
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialWebviewBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.demo.ui.camera.BarScanner
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.net.URLEncoder

/**
 * <p>
 *
 * @author cpacm 2023/7/20
 */
class WebViewFragment : ViewBindingFragment<FragmentMaterialWebviewBinding>() {

    private lateinit var barScannerLauncher: ActivityResultLauncher<Void?>
    private var webViewType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barScannerLauncher = registerForActivityResult(BarScanner(requireContext())) {
            binding.url.editText?.setText(it)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMaterialWebviewBinding {
        return FragmentMaterialWebviewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.url.setEndIconOnClickListener {
            barScannerLauncher.launch(null)
        }

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.androidWeb.id -> webViewType = 0
                    binding.x5Web.id -> webViewType = 1
                    binding.ucWeb.id -> webViewType = 2
                }
            }
        }

        binding.toggleGroup.check(binding.androidWeb.id)

        binding.jumpBtn.setOnClickListener {
            val linkUrl = binding.url.editText?.text
            if (linkUrl == null || linkUrl.toString().isEmpty()) {
                showMessage(R.string.material_webview_input_hint)
                return@setOnClickListener
            }

            val url = URLEncoder.encode(linkUrl.toString())

            when (webViewType) {
                0 -> findParentNavController()?.navigate(PageNav.WidgetAndroidH5Page.toUrl(url))
                1 -> findParentNavController()?.navigate(PageNav.WidgetAndroidX5Page.toUrl(url))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        barScannerLauncher.unregister()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 12,
                icon = R.drawable.ic_placeholder,
                title = "WebView",
                route = PageNav.MaterialWebViewPage.route(),
                fragmentClass = WebViewFragment::class,
            )
        }
    }
}
