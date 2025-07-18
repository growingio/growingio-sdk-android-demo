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
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComposeBinding
import com.growingio.demo.databinding.FragmentInitBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.util.MarkwonManager
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class ComponentComposeFragment : PageFragment<FragmentComposeBinding>() {

    @Inject
    lateinit var markwonManager: MarkwonManager

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComposeBinding {
        return FragmentComposeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_init_title))

        markwonManager.renderMarkdown(pageBinding.content, initContent())

        loadAssetCode(requireContext().applicationContext)
    }

    fun initComposeModule(){
        // 初始化时添加 ComposeLibraryGioModule 模块
        /**
         * GrowingAutotracker.startWithConfiguration(this,
         *     AutotrackConfiguration("accountId", "urlScheme")
         *         //...
         *         .addPreloadComponent(ComposeLibraryGioModule()))
         */
    }

    private fun initContent(): String {
        return """
          
            Growingio 无埋点 SDK 支持 Jetpack Compose 框架，包括页面事件，无埋点点击事件和圈选功能。
            --------

            ### 使用方式

            1. 初始化时添加 ComposeLibraryGioModule 模块

            ```kotlin
            GrowingAutotracker.startWithConfiguration(this,
                            AutotrackConfiguration("accountId", "urlScheme")
                            //...
                            .addPreloadComponent(ComposeLibraryGioModule()))
            ```

            2. 引入 Kotlin Compiler Plugin 插件

            在引入无埋点插件的前提下，添加新的KCP插件.

            ```groovy

            plugins {
                //id 'com.growingio.compose.plugin'
                alias(libs.plugins.growingio.compose.plugin)
            }

            ```

            > 该KCP插件主要为无埋点事件控件的路径`xpath`赋予可读性更高的名称。理论上不集成该插件也能正常发送无埋点事件。

            最后不要忘记在项目下的`build.gradle`中声明该插件
            ```groovy
            plugins {
                alias libs.plugins.growingio.compose.plugin apply false
            }
            ```

            3. 设置无埋点页面

            在 `@composable` 组件下将要声明的页面组件使用 `GrowingComposePage` 包裹。各个参数分别为：`alias`为页面唯一识别名称，`modifier`为需要的组件修饰符，`attributes`为页面属性，`content`则为作为Page的 composable 组件内容。

            ```kotlin
            GrowingComposePage(
                alias = "PageName",
                modifier: Modifier = Modifier,
                attributes: Map<String, String> = hashMapOf(),
                content = {
                    PageScreen(
                        modifier = modifier,
                        //...
                    )
                }
            )
            ```
            至此，compose 无埋点框架配置完成。

            :::warning Dialog
            直接包裹AlertDialog将只会发送Page事件，无埋点事件的path无法与page事件绑定。原因为Compose的AlertDialog是一个新的window渲染而来，无法直接穿透获取，所以无埋点事件无法向上遍历得到page节点的alias值。这边建议将page页面包裹AlertDialog的下一级组件。
            :::

            ### 无埋点的配置接口

            1. `Modifier.growingTag(tag: String)` 为设置的组件设置别名，反映在无埋点事件的`xpath`路径中;
            2. `Modifier.interruptClick()` 在GrowingIO Compose SDK 中获取点击组件是由上而下遍历的且取最后一个可点击组件的过程，所以当多个可点击组件重叠时会优先取到最后面的组件。使用该方法可以控制无埋点事件优先取到 `interruptClick` 装饰的组件；
            
        """.trimIndent()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 28,
                title = "Android Jetpack Compose",
                desc = "使用 Compose 插件，支持 Jetpack Compose 框架。",
                icon = SdkIcon.Component,
                route = PageNav.ComponentComposePage.route(),
                fragmentClass = ComponentComposeFragment::class,
            )
        }
    }
}
