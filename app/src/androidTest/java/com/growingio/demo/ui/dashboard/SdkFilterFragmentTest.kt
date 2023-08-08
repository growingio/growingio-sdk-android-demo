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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.demo.R
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.CustomEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException


/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SdkFilterFragmentTest : AbstractGrowingTestUnit() {

    val scenario = launchFragmentInHiltContainer<SdkEventFilterFragment>()

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun onPageTest() {
        try {
            runEventTest(AutotrackEventType.PAGE, timeout = 3, onEvent = { baseEvent ->
                throw Exception("Page path Filter Error.")
            }) {
            }
        } catch (e: Exception) {
            Truth.assertThat(e).isInstanceOf(TimeoutException::class.java)
        }
    }

    @Test
    fun onFieldFilterTest() {
        runEventTest(TrackEventType.CUSTOM, onEvent = { baseEvent ->
            if (baseEvent is CustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.language).isEmpty()
                Truth.assertThat(baseEvent.eventName).isEqualTo("filter_field")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.fieldFilterButton)).perform(click())
        }
    }

    @Test
    fun onCustomFilterTest() {
        try {
            runEventTest(TrackEventType.CUSTOM, timeout = 3, onEvent = { baseEvent ->
                throw Exception("Custom Filter Error.")
            }) {
                onView(withId(R.id.customFilterButton)).perform(click())
            }
        } catch (e: Exception) {
            Truth.assertThat(e).isInstanceOf(TimeoutException::class.java)
        }
    }

    @Test
    fun onEventTypeFilterTest() {
        try {
            runEventTest(TrackEventType.LOGIN_USER_ATTRIBUTES, timeout = 3, onEvent = { baseEvent ->
                throw Exception("EventType Filter Error.")
            }) {
                onView(withId(R.id.typeFilterButton)).perform(click())
            }
        } catch (e: Exception) {
            Truth.assertThat(e).isInstanceOf(TimeoutException::class.java)
        }
    }


}