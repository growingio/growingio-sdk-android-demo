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

import android.os.Looper
import android.os.Message
import com.growingio.android.sdk.track.log.BaseLogger
import com.growingio.demo.data.GrowingIOLoggerItem
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2023/4/25
 */
class GrowingLoggerManager @Inject constructor() : BaseLogger() {

    private val loggerObservers = arrayListOf<WeakReference<LoggerObserver>>()
    private val uiHandler = android.os.Handler(Looper.getMainLooper())

    private val loggerQueue = object : PriorityQueue<GrowingIOLoggerItem>(300) {
        override fun offer(e: GrowingIOLoggerItem): Boolean {
            if (size == 300) {
                poll()
            }
            observeLogger(e)
            return super.offer(e)
        }
    }

    override fun getType(): String {
        return "Logview"
    }

    override fun print(priority: Int, tag: String, message: String, t: Throwable?) {
        try {
            loggerQueue.add(GrowingIOLoggerItem(System.currentTimeMillis(), priority, tag, message))
        } catch (ignored: Exception) {
        }
    }

    fun clear() {
        loggerQueue.clear()
    }

    fun provideLoggerQueue(): PriorityQueue<GrowingIOLoggerItem> {
        return loggerQueue
    }

    fun observeLogger(item: GrowingIOLoggerItem) {
        // uiHandler.removeMessages(0)
        val message = Message.obtain(uiHandler) {
            loggerObservers.forEach {
                it.get()?.onLoggerChanged(item)
            }
        }
        uiHandler.sendMessage(message)
    }

    fun addLoggerObserver(observer: LoggerObserver) {
        loggerObservers.add(WeakReference(observer))
    }

    interface LoggerObserver {
        fun onLoggerChanged(item: GrowingIOLoggerItem)
    }
}
