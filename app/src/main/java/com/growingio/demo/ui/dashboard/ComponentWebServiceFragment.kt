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
import androidx.activity.result.ActivityResultLauncher
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentWebserviceBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.ui.camera.BarScanner
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.net.URLEncoder

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class ComponentWebServiceFragment : PageFragment<FragmentComponentWebserviceBinding>() {

    private lateinit var barScannerLauncher: ActivityResultLauncher<Void?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barScannerLauncher = registerForActivityResult(BarScanner(requireContext())) {
            pageBinding.deeplink.editText?.setText(it)
        }
    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComponentWebserviceBinding {
        return FragmentComponentWebserviceBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_webservice))

        pageBinding.deeplink.setEndIconOnClickListener {
            barScannerLauncher.launch(null)
        }

        pageBinding.goBtn.setOnClickListener {
            val deeplinkUrl = pageBinding.deeplink.editText?.text
            if (deeplinkUrl == null || deeplinkUrl.toString().isEmpty()) {
                showMessage(R.string.component_webservice_toast)
                return@setOnClickListener
            }
            val url = URLEncoder.encode(deeplinkUrl.toString())
            findParentNavController()?.navigate(PageNav.WidgetAndroidH5Page.toUrl(url))
        }

        loadAssetCode(this)

        // https://ads-uat.growingio.cn/k4budVa
        setDefaultLogFilter("level:debug circle")
    }

    override fun onDestroy() {
        super.onDestroy()
        // reset default sdk state
        barScannerLauncher.unregister()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 26,
                icon = SdkIcon.Component,
                title = "圈选",
                desc = "用于快速进入圈选或者数据校验状态，方便用户调试",
                route = PageNav.ComponentWebServicePage.route(),
                fragmentClass = ComponentWebServiceFragment::class,
            )
        }
    }
}
