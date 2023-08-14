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
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
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
 * @author cpacm 2023/8/8
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SdkUniqueTagFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        launchFragmentInHiltContainer<SdkUniqueTagFragment>()
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("唯一路径")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkUniqueTagFragment")
                return@runEventTest true
            }
            false
        }) {
        }
    }

    @Test
    fun uniqueTagTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.textValue).isEqualTo("测试按钮")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkUniqueTagFragment")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/unique")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/testBtn")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.uniqueTagEt)).perform(ViewActions.typeText("unique"))
            onView(withId(R.id.settleBtn)).perform(click())

            onView(withId(R.id.testBtn)).perform(click())
        }
    }
}
