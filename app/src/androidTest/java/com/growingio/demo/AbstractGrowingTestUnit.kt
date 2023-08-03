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

import com.google.common.util.concurrent.Uninterruptibles
import com.growingio.android.sdk.track.events.base.BaseEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Condition

/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
abstract class AbstractGrowingTestUnit {

    private val apiServer by lazy { MockEventsApiServer(this::dispatchEvent) }

    val receivedHandler: ArrayList<AwaitHandler> = arrayListOf()

    @Before
    open fun setup() {
        apiServer.start()
    }

    @After
    open fun teardown() {
        apiServer.shutdown()
    }

    private fun dispatchEvent(baseEvent: BaseEvent) {
        // wait event
        receivedHandler.forEach { handler ->
            if (handler.eventType == baseEvent.eventType) {
                val result = handler.onEvent(baseEvent)
                if (result) {
                    handler.countDownLatch.countDown()
                }
            }
        }
        // return
    }

    open fun onEvent(baseEvent: BaseEvent): Boolean {
        return false
    }


    fun waitEvent(
        eventType: String,
        onEvent: (event: BaseEvent) -> Boolean = this::onEvent,
        eventCount: Int = 1,
        timeout: Long = 5, unit: TimeUnit = TimeUnit.SECONDS
    ) {
        val remainingNanos = unit.toNanos(timeout)
        val end = System.nanoTime() + remainingNanos
        val countDownLatch = CountDownLatch(eventCount)
        val eventHandler = AwaitHandler(eventType, countDownLatch, onEvent)
        receivedHandler.add(eventHandler)
        while (countDownLatch.count > 0) {
            if (System.nanoTime() > end) {
                throw TimeoutException("Already waited for the $eventType Event to pass 5s, timed out")
            }
            Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS)
        }
        receivedHandler.remove(eventHandler)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun runEventTest(
        eventType: String,
        onEvent: (event: BaseEvent) -> Boolean = this::onEvent,
        eventCount: Int = 1,
        timeout: Long = 5, unit: TimeUnit = TimeUnit.SECONDS,
        testBody: suspend () -> Unit
    ) {
        runTest {
            val remainingNanos = unit.toNanos(timeout)
            val end = System.nanoTime() + remainingNanos
            val countDownLatch = CountDownLatch(eventCount)
            val eventHandler = AwaitHandler(eventType, countDownLatch, onEvent)
            receivedHandler.add(eventHandler)
            testBody()
            //CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                while (countDownLatch.count > 0) {
                    if (System.nanoTime() > end) {
                        throw TimeoutException("Already waited for the $eventType Event to pass 5s, timed out")
                    }
                    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS)
                }
            }
            receivedHandler.remove(eventHandler)
        }
    }

    class AwaitHandler(
        val eventType: String,
        val countDownLatch: CountDownLatch,
        val onEvent: (event: BaseEvent) -> Boolean
    )
}