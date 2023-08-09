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
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.R
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
class SdkLocationFragmentTest : AbstractGrowingTestUnit() {

    val scenario = launchFragmentInHiltContainer<SdkLocationFragment>()

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("位置信息")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkLocationFragment")
                return@runEventTest true
            }
            false
        }) {
        }
    }

    @Test
    fun editViewChangeTest() {
        runEventTest(AutotrackEventType.VIEW_CHANGE, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CHANGE)
                Truth.assertThat(baseEvent.textValue).isEmpty()
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkLocationFragment")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/SdkLocationFragment/ConstraintLayout/FrameLayout/ConstraintLayout/TextInputLayout/FrameLayout/TextInputEditText")
                Truth.assertThat(baseEvent.xIndex).isAnyOf(
                    "/0/content/0/apiLayout/0/latitude/0/latitudeEt",
                    "/0/content/0/apiLayout/0/longitude/0/longitudeEt",
                )
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.latitudeEt)).perform(typeText("25"))
            onView(withId(R.id.longitudeEt)).perform(typeText("25"))
        }
    }

    @Test
    fun locationSettleTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                val deviceInfoProvider = GrowingAutotracker.get().context.deviceInfoProvider
                Truth.assertThat(deviceInfoProvider.latitude).isEqualTo(25)
                Truth.assertThat(deviceInfoProvider.longitude).isEqualTo(90)
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.latitudeEt)).perform(typeText("25"))
            onView(withId(R.id.longitudeEt)).perform(typeText("90"))
            onView(withId(R.id.settle)).perform(click())
        }
    }

    @Test
    fun locationClearTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, timeout = 8, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                val deviceInfoProvider = GrowingAutotracker.get().context.deviceInfoProvider
                Truth.assertThat(deviceInfoProvider.latitude).isEqualTo(0)
                Truth.assertThat(deviceInfoProvider.longitude).isEqualTo(0)
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.latitudeEt)).perform(typeText("25"))
            onView(withId(R.id.longitudeEt)).perform(typeText("90"))
            onView(withId(R.id.settle)).perform(click())

            onView(withId(R.id.clean)).perform(click())
        }
    }
}
