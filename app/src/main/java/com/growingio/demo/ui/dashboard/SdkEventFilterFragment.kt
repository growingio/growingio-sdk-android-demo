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
import com.growingio.demo.databinding.FragmentInitBinding
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.util.MarkwonManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class SdkEventFilterFragment : PageFragment<FragmentInitBinding>() {

    @Inject
    lateinit var markwonManager: MarkwonManager

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentInitBinding {
        return FragmentInitBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_title_init))

        markwonManager.renderMarkdown(pageBinding.content, initContent())

        loadAssetCode(requireContext().applicationContext)
    }

    private fun initContent(): String {
        return """
            Android SDK 提供了 **无埋点SDK** 和 **埋点SDK** 两个版本：
            * 埋点SDK 只自动采集用户访问事件，需要开发同学调用相应埋点 API 采集埋点事件;
            * 无埋点SDK 具备 埋点SDK 的所有功能，同时具备自动采集基本用户行为事件，如页面访问，点击事件等。
            
            > 本Demo中一律使用的是无埋点SDK.
            
            | 参数名称                      | 默认值  | 说明                 | 
            | :----------------------------| :----------------- | :------ | 
            | accountId                    |  手动传入  | 项目ID，每个应用对应唯一值  |
            | urlScheme                    | 手动传入  | 应用的URLScheme，唯一值    |
            | setDataSourceId              | 手动传入| 应用的DataSourceId，唯一值   | 
            | setDataCollectionServerHost  | 可自由设置| 服务端部署后的 ServerHost    |
            | setChannel                   | null  | 应用的分发渠道             |
            | setDebugEnabled              | false | 调试模式，会打印SDK Log    | 
            | setCellularDataLimit         | 10   | 每天发送数据的流量限制，单位MB |
            | setDataUploadInterval        | 15    | 数据发送的间隔，单位秒       |
            | setSessionInterval           | 30    | 会话后台留存时长，单位秒     | 
            | setDataCollectionEnabled     | true  | 是否采集数据               | 
            | setEventFilterInterceptor    | DefaultEventFilterInterceptor | 设置事件过滤  |
            | setIdMappingEnabled          | false | 是否开启多用户身份上报         | 
            | setImpressionScale           | 0f     | 元素曝光事件中的比例因子,范围 [0-1] | 
            | setRequireAppProcessesEnabled | true  | SDK 是否能获取应用多进程ID     | 
            | addPreloadComponent          | LibraryGioModule,Configurable  | 预注册自定义/预定义模块及其配置文件 |
            
            
        """.trimIndent()
    }
}