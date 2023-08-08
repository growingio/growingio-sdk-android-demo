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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.demo.R
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.android.sdk.track.events.VisitEvent
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ComponentJsonFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<ComponentProtobufFragment>()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("数据格式")
                Truth.assertThat(baseEvent.path).isEqualTo("/ComponentProtobufFragment")
                return@runEventTest true
            }
            false
        }) {

        }
    }

    @Test
    fun jsonDataTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.json)).perform(click()).check(matches(isChecked()))
            onView(withId(R.id.testBtn)).perform(click())
        }
    }

}