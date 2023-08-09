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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

@SuppressLint("SetJavaScriptEnabled")
class SdkX5WebView : WebView {

    private var onWebViewChangedListener: OnWebViewChangedListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        val webSettings = settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true // 自适应屏幕
        webSettings.mediaPlaybackRequiresUserGesture = true
        webSettings.mixedContentMode = WebSettings.LOAD_DEFAULT
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.databaseEnabled = true
        webSettings.loadsImagesAutomatically = true

        this.setDownloadListener { url, _, _, _, _ ->
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        webViewClient = BaseWebClient()
        webChromeClient = DefaultWebChromeClient()
    }

    private inner class BaseWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            if (!settings.loadsImagesAutomatically) {
                settings.loadsImagesAutomatically = true
            }
            onWebViewChangedListener?.onLoadingProgress(0)
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onWebViewChangedListener?.onLoadingProgress(100)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            onWebViewChangedListener?.onResourceLoad(url)
            super.onLoadResource(view, url)
        }
    }

    private inner class DefaultWebChromeClient : WebChromeClient() {

        override fun onReceivedTitle(view: WebView?, title: String?) {
            onWebViewChangedListener?.onTitleChanged(title)
            super.onReceivedTitle(view, title)
        }
    }

    fun setOnWebViewChangedListener(onWebViewChangedListener: OnWebViewChangedListener) {
        this.onWebViewChangedListener = onWebViewChangedListener
    }

    fun removeWebViewChangedListener() {
        this.onWebViewChangedListener = null
    }

    /**
     * Implement in the activity/fragment/view that you want to listen to the webview
     */
    interface OnWebViewChangedListener {

        fun onLoadingProgress(progress: Int)

        fun onResourceLoad(url: String?) {}

        fun onTitleChanged(title: String?) {}
    }
}
