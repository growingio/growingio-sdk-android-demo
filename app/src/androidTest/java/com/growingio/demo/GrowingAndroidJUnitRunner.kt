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

package com.growingio.demo

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.growingio.android.json.JsonLibraryModule
import com.growingio.android.sdk.autotrack.AutotrackConfiguration
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.util.GrowingIOManager
import kotlinx.coroutines.runBlocking


/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
class GrowingAndroidJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, className, context)
    }


    override fun callApplicationOnCreate(app: Application?) {
        super.callApplicationOnCreate(app)

        GrowingAutotracker.shutdown()
        GrowingAutotracker.startWithConfiguration(
            app, AutotrackConfiguration(PROJECT_ID, URL_SCHEME)
                .setDataCollectionServerHost(MOCK_SERVER_HOST)
                .setChannel(CHANNEL)
                .setDataSourceId(DATA_SOURCE_ID)
                .setDataUploadInterval(0)
                .setIdMappingEnabled(true)
                .setEventFilterInterceptor(GrowingIOManager.provideEventFilterInterceptor())
                .setDebugEnabled(true)
                //.addPreloadComponent(JsonLibraryModule())
        )

        runBlocking {
            context.settingsDataStore.updateData {
                it.toBuilder().setAgreePolicy(true).build()
            }
        }
    }

    companion object {
        const val PROJECT_ID = "projectId"
        const val URL_SCHEME = "urlScheme"
        const val DATA_SOURCE_ID = "dataSourceId"
        const val CHANNEL = "test"

        const val MOCK_SERVER_PORT = 8910
        const val MOCK_SERVER_HOST = "http://localhost:$MOCK_SERVER_PORT"

    }
}