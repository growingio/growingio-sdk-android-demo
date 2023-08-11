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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.CustomEvent
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.providers.TimingEventProvider.ATTR_EVENT_DURATION
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.R
import com.growingio.demo.launchFragmentInHiltContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * <p>
 *
 * @author cpacm 2023/8/8
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SdkTimerFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        launchFragmentInHiltContainer<SdkEventTimerFragment>()
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("事件计时器")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkEventTimerFragment")
                return@runEventTest true
            }
            false
        }) {
        }
    }

    @Test
    fun timerTest() {
        runEventTest(TrackEventType.CUSTOM, timeout = 15, onEvent = { baseEvent ->
            if (baseEvent is CustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("cpacm")
                Truth.assertThat(baseEvent.attributes).hasSize(3)
                Truth.assertThat(baseEvent.attributes).containsEntry("app", "demo")
                Truth.assertThat(baseEvent.attributes).containsEntry("tag", "TimerCenter")
                Truth.assertThat(baseEvent.attributes).containsKey(ATTR_EVENT_DURATION)
                return@runEventTest true
            }
            false
        }) {
            withContext(Dispatchers.IO) {
                onView(withId(R.id.timerEt)).perform(ViewActions.typeText("cpacm"), closeSoftKeyboard())
                onView(withId(R.id.start)).perform(click())
                delay(1000)
                onView(withId(R.id.pause)).perform(click())
                delay(500)
                onView(withId(R.id.resume)).perform(click())
                delay(500)
                onView(withId(R.id.end)).perform(click())
            }
        }
    }
}
