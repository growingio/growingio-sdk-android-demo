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
import com.growingio.android.sdk.track.events.helper.DefaultEventFilterInterceptor
import com.growingio.android.sdk.track.log.BaseLogger
import com.growingio.android.sdk.track.log.ILogger
import com.growingio.android.sdk.track.log.Logger
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.data.GrowingIOLoggerItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.ref.WeakReference
import java.util.PriorityQueue

/**
 * <p>
 *
 * @author cpacm 2023/4/23
 */
@Module
@InstallIn(SingletonComponent::class)
object GrowingIOManager {

    private var defaultEventFilterInterceptor: DefaultEventFilterInterceptor = DefaultEventFilterInterceptor()

    @Provides
    fun provideEventFilterInterceptor(): EventFilterInterceptor {
        return DemoEventFilterInterceptor(defaultEventFilterInterceptor)
    }

    @Provides
    fun provideLoggerManager(): GrowingLoggerManager {
        return GrowingLoggerManager()
    }

    @SourceCode
    fun configDemoEventFilterInterceptor() {
        defaultEventFilterInterceptor = object : DefaultEventFilterInterceptor() {

        }
    }

    class DemoEventFilterInterceptor(private val filterInterceptor: EventFilterInterceptor) :
        EventFilterInterceptor by filterInterceptor
}

