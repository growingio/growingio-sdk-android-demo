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

/**
 * <p>
 *
 * @author cpacm 2023/4/21
 */
class SyntaxHighlighterWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    @SuppressLint("SetJavaScriptEnabled")
    fun bindSyntaxHighlighter(
        formattedSourceCode: String,
        language: String,
        showLineNumbers: Boolean = false,
    ) {
        settings.javaScriptEnabled = true
        loadDataWithBaseURL(
            ANDROID_ASSETS_PATH,
            prismJsHtmlContent(
                formattedSourceCode,
                language,
                showLineNumbers,
            ),
            "text/html",
            "utf-8",
            "",
        )
    }

    private fun prismJsHtmlContent(
        formattedSourceCode: String,
        language: String,
        showLineNumbers: Boolean = true,
    ): String {
        return """<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="prism.css" rel="stylesheet"/>
    <script src="prism.js"></script>
    <script type="text/javascript">
        // Optional
        Prism.plugins.NormalizeWhitespace.setDefaults({
            'remove-trailing': true,
            'remove-indent': true,
             'left-trim': true,
             'right-trim': true,
        });
    </script>
</head>
<body>
<pre class="${if (showLineNumbers) "line-numbers" else ""}">
<code class="language-$language">$formattedSourceCode</code>
</pre>
</body>
</html>
"""
    }

    companion object {
        private const val ANDROID_ASSETS_PATH = "file:///android_asset/"
    }
}
