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

package com.growingio.demo.util

import com.growingio.android.sdk.track.events.EventFilterInterceptor
import com.growingio.android.sdk.track.events.base.BaseField
import com.growingio.android.sdk.track.events.helper.DefaultEventFilterInterceptor
import com.growingio.android.sdk.track.log.Logger
import com.growingio.code.annotation.SourceCode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * <p>
 *
 * @author cpacm 2023/4/23
 */
@Module
@InstallIn(SingletonComponent::class)
object GrowingIOManager {

    private val loggerManager by lazy {
        GrowingLoggerManager()
    }

    init {
        Logger.addLogger(loggerManager)
    }

    private var defaultEventFilterInterceptor = DemoEventFilterInterceptor()

    @Provides
    fun provideEventFilterInterceptor(): EventFilterInterceptor {
        return defaultEventFilterInterceptor
    }

    @Provides
    fun provideLoggerManager(): GrowingLoggerManager {
        return loggerManager
    }

    @SourceCode
    fun configDemoEventFilterInterceptor() {
        defaultEventFilterInterceptor.innerFilterInterceptor = object : DefaultEventFilterInterceptor() {
            override fun filterEventType(eventType: String): Boolean {
                if (eventType == FilterEventType.LOGIN_USER_ATTRIBUTES) {
                    return false
                }
                return super.filterEventType(eventType)
            }

            override fun filterEventPath(path: String): Boolean {
                if (path.contains("SdkEventFilterFragment")) {
                    return false
                }
                return super.filterEventPath(path)
            }

            override fun filterEventName(eventName: String): Boolean {
                if (eventName == "filter") {
                    return false
                }
                return super.filterEventName(eventName)
            }

            override fun filterEventField(
                type: String,
                fieldArea: MutableMap<String, Boolean>,
            ): MutableMap<String, Boolean> {
                fieldArea[BaseField.LANGUAGE] = false
                return super.filterEventField(type, fieldArea)
            }
        }
    }

    fun resetEventFilterInterceptor() {
        defaultEventFilterInterceptor.innerFilterInterceptor = DefaultEventFilterInterceptor()
    }

    class DemoEventFilterInterceptor : EventFilterInterceptor {

        var innerFilterInterceptor: EventFilterInterceptor? = null

        override fun filterEventType(eventType: String): Boolean {
            return innerFilterInterceptor?.filterEventType(eventType) ?: true
        }

        override fun filterEventName(eventName: String): Boolean {
            return innerFilterInterceptor?.filterEventName(eventName) ?: true
        }

        override fun filterEventPath(path: String): Boolean {
            return innerFilterInterceptor?.filterEventPath(path) ?: true
        }

        override fun filterEventField(
            type: String,
            fieldArea: MutableMap<String, Boolean>,
        ): MutableMap<String, Boolean> {
            return innerFilterInterceptor?.filterEventField(type, fieldArea) ?: fieldArea
        }
    }
}
