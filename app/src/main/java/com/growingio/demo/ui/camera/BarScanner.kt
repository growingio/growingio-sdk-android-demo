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

package com.growingio.demo.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * <p>
 *
 * @author cpacm 2023/5/11
 */
class BarScanner(val context: Context) : ActivityResultContract<Void?, String?>() {
    override fun createIntent(context: Context, input: Void?): Intent {
        val intent = Intent(context, CameraXLivePreviewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.getStringExtra("barcode")
    }
}