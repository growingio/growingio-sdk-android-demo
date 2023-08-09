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

package com.growingio.demo.ui.material

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.R
import com.growingio.demo.launchFragmentInHiltContainer
import com.growingio.demo.setValue
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
class SliderFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<SliderFragment>()
    }

    @Test
    fun onSliderTest() {
        runEventTest(AutotrackEventType.VIEW_CHANGE, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CHANGE)
                Truth.assertThat(baseEvent.path).isEqualTo("/SliderFragment")
                Truth.assertThat(baseEvent.textValue).isNotEmpty()
                Truth.assertThat(baseEvent.xpath)
                    .isAnyOf("/HiltTestActivity/SliderFragment/LinearLayout/Slider", "/HiltTestActivity/SliderFragment/LinearLayout/RangeSlider")
                Truth.assertThat(baseEvent.xIndex).isAnyOf("/0/content/0/slider", "/0/content/0/rangeSlider")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.slider)).perform(
                swipeRight(),
            ).perform(setValue(10f))

            onView(withId(R.id.rangeSlider)).perform(
                swipeRight(),
            )
        }
    }

    @Test
    fun onSeekBarTest() {
        runEventTest(AutotrackEventType.VIEW_CHANGE, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CHANGE)
                Truth.assertThat(baseEvent.path).isEqualTo("/SliderFragment")
                Truth.assertThat(baseEvent.textValue).isNotEmpty()
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/SliderFragment/LinearLayout/AppCompatSeekBar")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/seekbar")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.seekbar)).perform(
                swipeRight(),
            )
        }
    }

    @Test
    fun onRatingBarTest() {
        runEventTest(AutotrackEventType.VIEW_CHANGE, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CHANGE)
                Truth.assertThat(baseEvent.path).isEqualTo("/SliderFragment")
                Truth.assertThat(baseEvent.textValue).isNotEmpty()
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/SliderFragment/LinearLayout/AppCompatRatingBar")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/ratingBar")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.ratingBar)).perform(
                swipeRight(),
            )
        }
    }
}
