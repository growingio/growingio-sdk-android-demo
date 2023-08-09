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
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.PageLevelCustomEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.R
import com.growingio.demo.launchFragmentInHiltContainer
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
class SdkImpressionFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        launchFragmentInHiltContainer<SdkImpressionFragment>()
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("视图曝光事件")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkImpressionFragment")
                return@runEventTest true
            }
            false
        }) {
        }
    }

    @Test
    fun viewImpressionTest() {
        runEventTest(TrackEventType.CUSTOM, onEvent = { baseEvent ->
            if (baseEvent is PageLevelCustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("ImpressionProvider")
                Truth.assertThat(baseEvent.pageShowTimestamp).isGreaterThan(0)
                Truth.assertThat(baseEvent.attributes).hasSize(1)
                Truth.assertThat(baseEvent.attributes).containsEntry("type", "visible")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.settleBtn)).perform(click())
            onView(withId(R.id.impressionSwitch)).perform(click())
        }
    }

    @Test
    fun viewScrollImpressionTest() {
        runEventTest(TrackEventType.CUSTOM, onEvent = { baseEvent ->
            if (baseEvent is PageLevelCustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("ImpressionProvider")
                Truth.assertThat(baseEvent.pageShowTimestamp).isGreaterThan(0)
                Truth.assertThat(baseEvent.attributes).hasSize(1)
                Truth.assertThat(baseEvent.attributes).containsEntry("type", "scroll")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.settleBtn)).perform(click())
            onView(withId(R.id.scrollView)).perform(swipeUp())
        }
    }
}
