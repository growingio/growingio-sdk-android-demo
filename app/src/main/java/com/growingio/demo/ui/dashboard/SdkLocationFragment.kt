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
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentLocationBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class SdkLocationFragment : PageFragment<FragmentLocationBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLocationBinding {
        return FragmentLocationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_location))

        pageBinding.settle.setOnClickListener {

            val latitude = pageBinding.latitude.editText?.text
            if (latitude == null || latitude.toString().isEmpty()) {
                showMessage(R.string.sdk_location_toast)
                return@setOnClickListener
            }

            val longitude = pageBinding.longitude.editText?.text
            if (longitude == null || longitude.toString().isEmpty()) {
                showMessage(R.string.sdk_location_toast)
                return@setOnClickListener
            }

            try {
                setLocation(latitude.toString().toDouble(), longitude.toString().toDouble())
            } catch (e: Exception) {
                showMessage(R.string.sdk_location_toast)
            }
        }
        pageBinding.clean.setOnClickListener {
            clearLocation()
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug visit")
    }

    @SourceCode
    private fun setLocation(latitude: Double, longitude: Double) {
        GrowingAutotracker.get().setLocation(latitude, longitude)
    }

    @SourceCode
    private fun clearLocation() {
        GrowingAutotracker.get().cleanLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearLocation()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 5,
                icon = SdkIcon.Api,
                title = "位置信息",
                desc = "为事件设置经纬度信息",
                route = PageNav.SdkLocationPage.route(),
                fragmentClass = SdkLocationFragment::class
            )
        }
    }
}