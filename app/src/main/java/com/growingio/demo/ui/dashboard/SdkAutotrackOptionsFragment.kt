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
import com.growingio.android.sdk.autotrack.AutotrackConfig
import com.growingio.android.sdk.autotrack.AutotrackOptions
import com.growingio.android.sdk.track.providers.ConfigurationProvider
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentAutotrackOptionsBinding
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
class SdkAutotrackOptionsFragment : PageFragment<FragmentAutotrackOptionsBinding>() {

    @Inject
    lateinit var markwonManager: MarkwonManager

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAutotrackOptionsBinding {
        return FragmentAutotrackOptionsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_autotrack_option))

        markwonManager.renderMarkdown(pageBinding.content, initContent())

        loadAssetCode(this)
    }

    @SourceCode
    private fun initAutotrackerOptions() {
        val autotrackerOptions = AutotrackOptions()
        autotrackerOptions.apply {
            isActivityPageEnabled = true
            isFragmentPageEnabled = true
            isActivityMenuItemClickEnabled = false
            isToolbarMenuItemClickEnabled = true
            isActionMenuItemClickEnabled = true
            isPopupMenuItemClickEnabled = true
            isContextMenuItemClickEnabled = true
            isDialogClickEnabled = true
            isEditTextChangeEnabled = true
            isSeekbarChangeEnabled = true
            isSpinnerItemClickSelectEnabled = true
            isAdapterViewItemClickEnabled = true
            isExpandableListGroupClickEnabled = true
            isExpandableListChildClickEnabled = true
            isCompoundButtonCheckEnabled = true
            isRadioGroupCheckEnabled = true
            isViewClickEnabled = true
        }
        /**
         * // 可以在初始化时设置无埋点配置
         * GrowingAutotracker.startWithConfiguration(requireContext(),
         * CdpAutotrackConfiguration("accountId", "urlScheme")
         * //...
         * .setAutotrackOptions(autotrackerOptions)
         * )
         */
        // 也可以在运行时修改
        val autotrackConfig = ConfigurationProvider.get().getConfiguration<AutotrackConfig>(AutotrackConfig::class.java)
        autotrackConfig?.autotrackOptions = autotrackerOptions
    }

    private fun initContent(): String {
        return """
            在无埋点SDK中，开发者可以通过设置无埋点配置来精细化控制无埋点注入范围。
            
            | 参数名称                      | 默认值  | 说明                 | 
            | :----------------------------| :----------------- | :------ | 
            | fragmentPageEnabled          | true  | Fragment 页面事件   |
            | activityPageEnabled          | true | Activity 页面时间    | 
            | activityMenuItemClickEnabled  |  false  | Activity上的MenuItem点击事件  |
            | toolbarMenuItemClickEnabled    | true  | Toolbar上的MenuItem点击事件    |
            | actionMenuItemClickEnabled     | true | ActionMenu上的MenuItem点击事件   | 
            | popupMenuItemClickEnabled    | true | PopupMenu上的MenuItem点击事件    |
            | contextMenuItemClickEnabled     | true |ContextMenu上的MenuItem点击事件   | 
            | dialogClickEnabled         | true   | Dialog上的选项点击事件 |
            | editTextChangeEnabled        | true    | EditText上的焦点变化事件       |
            | seekbarChangeEnabled           | true    | SeekBar上的滑动事件     | 
            | spinnerItemClickSelectEnabled     | true  | Spinner上的Item点击事件               | 
            | adapterViewItemClickEnabled    | true | DefaultAdapter上的Item点击事件  |
            | expandableListGroupClickEnabled  | true |  ExpandableListGroup上的父级选项点击事件       | 
            | expandableListChildClickEnabled  | true |  ExpandableListGroup上的子级选项点击事件       | 
            | compoundButtonCheckEnabled       | true     | CompoundButton上的选中事件 | 
            | ratingBarChangeEnabled    | true  | RatingBar上的选中变化事件    | 
            | viewClickEnabled          | true  | 默认的OnClickListener监听事件 |
            
        """.trimIndent()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 2,
                title = "无埋点配置",
                desc = "设置无埋点配置来精细化控制无埋点注入范围",
                icon = SdkIcon.Config,
                route = PageNav.SdkAutoTrackOptionsPage.route(),
                fragmentClass = SdkAutotrackOptionsFragment::class
            )
        }

    }
}
