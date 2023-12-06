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

package com.growingio.demo.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.growingio.android.sdk.autotrack.GrowingAutotracker

/**
 * <p>
 *
 * @author cpacm 2023/5/08
 */
class GioHybridWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    @SuppressLint("SetJavaScriptEnabled")
    fun initGioHybridHtml() {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        loadDataWithBaseURL(
            ANDROID_ASSETS_PATH,
            loadGioHybridHtmlFromAsset("gioHybridUat.html"),
            "text/html",
            "utf-8",
            "data:text/html;charset=utf-8;base64,",
        )
        setWebContentsDebuggingEnabled(true)
    }

    private fun loadGioHybridHtmlFromAsset(path: String): String {
        val gioHybridHtmlContent = context.assets.open(path).bufferedReader().use { it.readText() }

        val projectId = GrowingAutotracker.get().context.configurationProvider.core().projectId
        val dataSourceId = GrowingAutotracker.get().context.configurationProvider.core().dataSourceId
        val dataServerHost = GrowingAutotracker.get().context.configurationProvider.core().dataCollectionServerHost
        val newHtmlContent = gioHybridHtmlContent.replace("<ProjectId>", projectId).replace("<DataSourceId>", dataSourceId)
            .replace("<DataServerHost>", dataServerHost)

        return newHtmlContent
    }

    companion object {
        private const val ANDROID_ASSETS_PATH = "file:///android_asset/"
    }
}
