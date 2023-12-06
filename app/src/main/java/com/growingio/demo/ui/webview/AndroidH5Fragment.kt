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

package com.growingio.demo.ui.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.growingio.demo.databinding.FragmentAndroidH5Binding
import com.growingio.demo.ui.base.ViewBindingFragment
import java.net.URLDecoder

/**
 * <p>
 *
 * @author cpacm 2023/7/20
 */
class AndroidH5Fragment : ViewBindingFragment<FragmentAndroidH5Binding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAndroidH5Binding {
        return FragmentAndroidH5Binding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.toolbar.title = "Android WebView"
        binding.h5Web.setOnWebViewChangedListener(object : SdkH5WebView.OnWebViewChangedListener {
            override fun onLoadingProgress(progress: Int) {
            }

            override fun onTitleChanged(title: String?) {
                binding.toolbar.title = title
            }
        })

        val url = arguments?.getString("url") ?: "about:blank"
        binding.h5Web.loadUrl(URLDecoder.decode(url))
    }

    override fun onBackPressed(): Boolean {
        if (binding.h5Web.canGoBack()) {
            binding.h5Web.goBack()
            return true
        }
        return super.onBackPressed()
    }

    override fun onDestroyView() {
        binding.h5Web.removeWebViewChangedListener()
        super.onDestroyView()
    }
}
