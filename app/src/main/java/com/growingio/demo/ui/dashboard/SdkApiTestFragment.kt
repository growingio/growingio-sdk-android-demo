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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.growingio.android.sdk.autotrack.AutotrackConfiguration
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.AttributesBuilder
import com.growingio.demo.BuildConfig
import com.growingio.demo.GrowingioInitializer
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.databinding.FragmentSdkTestBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class SdkApiTestFragment : PageFragment<FragmentSdkTestBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSdkTestBinding {
        return FragmentSdkTestBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_api_test))

        pageBinding.autotrackBtn.setOnClickListener {
            closeAutotrackAndReCreateSdk()
            showMessage(R.string.sdk_api_autotrack_hint)
        }

        pageBinding.dataLimitBtn.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("largeData", get2MData())
        }

        loadAssetCode(this)

        setDefaultLogFilter("level:debug")
    }

    private fun get2MData(): Map<String, String> {
        var dataSize: Long = 0
        val maxDataSize = (2 * 1000 * 1024).toLong()
        var key = 1
        val attrBuilder = AttributesBuilder()
        val data =
            """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam in scelerisque sem. Mauris volutpat, dolor id interdum ullamcorper, risus dolor egestas lectus, sit amet mattis purus dui nec risus. Maecenas non sodales nisi, vel dictum dolor. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse blandit eleifend diam, vel rutrum tellus vulputate quis. Aliquam eget libero aliquet, imperdiet nisl a, ornare ex. Sed rhoncus est ut libero porta lobortis. Fusce in dictum tellus.
            Suspendisse interdum ornare ante. Aliquam nec cursus lorem. Morbi id magna felis. Vivamus egestas, est a condimentum egestas, turpis nisl iaculis ipsum, in dictum tellus dolor sed neque. Morbi tellus erat, dapibus ut sem a, iaculis tincidunt dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Curabitur et eros porttitor, ultricies urna vitae, molestie nibh. Phasellus at commodo eros, non aliquet metus. Sed maximus nisl nec dolor bibendum, vel congue leo egestas.
            Sed interdum tortor nibh, in sagittis risus mollis quis. Curabitur mi odio, condimentum sit amet auctor at, mollis non turpis. Nullam pretium libero vestibulum, finibus orci vel, molestie quam. Fusce blandit tincidunt nulla, quis sollicitudin libero facilisis et. Integer interdum nunc ligula, et fermentum metus hendrerit id. Vestibulum lectus felis, dictum at lacinia sit amet, tristique id quam. Cras eu consequat dui. Suspendisse sodales nunc ligula, in lobortis sem porta sed. Integer id ultrices magna, in luctus elit. Sed a pellentesque est.
            Aenean nunc velit, lacinia sed dolor sed, ultrices viverra nulla. Etiam a venenatis nibh. Morbi laoreet, tortor sed facilisis varius, nibh orci rhoncus nulla, id elementum leo dui non lorem. Nam mollis ipsum quis auctor varius. Quisque elementum eu libero sed commodo. In eros nisl, imperdiet vel imperdiet et, scelerisque a mauris. Pellentesque varius ex nunc, quis imperdiet eros placerat ac. Duis finibus orci et est auctor tincidunt. Sed non viverra ipsum. Nunc quis augue egestas, cursus lorem at, molestie sem. Morbi a consectetur ipsum, a placerat diam. Etiam vulputate dignissim convallis. Integer faucibus mauris sit amet finibus convallis.
            Phasellus in aliquet mi. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. In volutpat arcu ut felis sagittis, in finibus massa gravida. Pellentesque id tellus orci. Integer dictum, lorem sed efficitur ullamcorper, libero justo consectetur ipsum, in mollis nisl ex sed nisl. Donec maximus ullamcorper sodales. Praesent bibendum rhoncus tellus nec feugiat. In a ornare nulla. Donec rhoncus libero vel nunc consequat, quis tincidunt nisl eleifend. Cras bibendum enim a justo luctus vestibulum. Fusce dictum libero quis erat maximus, vitae volutpat diam dignissim.
            """
        while (dataSize < maxDataSize) {
            attrBuilder.addAttribute(key.toString(), data)
            key++
            dataSize += (data.toByteArray().size + key.toString().toByteArray().size).toLong()
        }
        return attrBuilder.build()
    }

    private fun closeAutotrackAndReCreateSdk() {
        GrowingAutotracker.shutdown()
        val autotrackConfiguration =
            AutotrackConfiguration(GrowingioInitializer.GROWINGIO_PROJECT_ID, GrowingioInitializer.GROWINGIO_URL_SCHEME)
                .setDataSourceId(GrowingioInitializer.GROWINGIO_DATASOURCE_ID)
                .setDataCollectionServerHost(GrowingioInitializer.GROWINGIO_SERVER_HOST)
                .setChannel("demo")
                .setDebugEnabled(BuildConfig.DEBUG)
                .setAndroidIdEnabled(true)
                .setPageRuleXml(R.xml.growingio_setting)
                .setRequireAppProcessesEnabled(true)
                .setDataCollectionEnabled(true)
                .setIdMappingEnabled(true)
                .setAutotrack(false)

        configWithDataStore(requireContext(), autotrackConfiguration)
        GrowingAutotracker.startWithConfiguration(requireActivity(), autotrackConfiguration)
    }

    private fun configWithDataStore(context: Context, configuration: AutotrackConfiguration) {
        runBlocking {
            val projectId = context.settingsDataStore.data.first().projectId.default(GrowingioInitializer.GROWINGIO_PROJECT_ID)
            val urlScheme = context.settingsDataStore.data.first().urlScheme.default(GrowingioInitializer.GROWINGIO_URL_SCHEME)
            val dataSourceId = context.settingsDataStore.data.first().datasourceId.default(GrowingioInitializer.GROWINGIO_DATASOURCE_ID)
            val serverHost = context.settingsDataStore.data.first().serverHost.default(GrowingioInitializer.GROWINGIO_SERVER_HOST)
            configuration.setProject(projectId, urlScheme)
                .setDataSourceId(dataSourceId)
                .setDataCollectionServerHost(serverHost)
        }
    }

    fun String.default(default: String): String {
        if (this.isEmpty()) return default
        return this
    }


    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 18,
                icon = SdkIcon.Api,
                title = "其他SDK功能",
                desc = "用于SDK的一些功能测试，事实上它们经常被忽略",
                route = PageNav.SdkApiTestPage.route(),
                fragmentClass = SdkApiTestFragment::class,
            )
        }
    }
}
