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
class SdkPageFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        launchFragmentInHiltContainer<SdkAutotrackPageFragment>()
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("发送Page事件")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkAutotrackPageFragment")
                return@runEventTest true
            }
            false
        }) {

        }
    }

    @Test
    fun pageAliasTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.textValue).isEqualTo("设置别名")
                Truth.assertThat(baseEvent.path).isEqualTo("/cpacm")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/AndroidXFragment/ConstraintLayout/FrameLayout/ConstraintLayout/MaterialButton")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/apiLayout/0/aliasBtn")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.aliasEt)).perform(ViewActions.typeText("cpacm"))
            onView(withId(R.id.aliasBtn)).perform(click())

            onView(withId(R.id.aliasEt)).perform(ViewActions.clearText())
            onView(withId(R.id.aliasBtn)).perform(click())

        }
    }

    @Test
    fun onPageAttributesTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 3, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.textValue).isEqualTo("设置属性")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkAutotrackPageFragment")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/AndroidXFragment/ConstraintLayout/FrameLayout/ConstraintLayout/MaterialButton")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/apiLayout/0/attributeBtn")
                Truth.assertThat(baseEvent.attributes).hasSize(2)
                Truth.assertThat(baseEvent.attributes).containsEntry("key0", "value0")
                Truth.assertThat(baseEvent.attributes).containsEntry("key1", "value1")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.addBtn)).perform(click())
            onView(withId(R.id.attributeBtn)).perform(click())
            onView(withId(R.id.attributeBtn)).perform(click())
        }
    }
}