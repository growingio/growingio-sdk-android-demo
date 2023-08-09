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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
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
class DialogFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<AppCompatDialogFragment>()
    }

    @Test
    fun alertDialogTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("Cancel")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/DecorView/LinearLayout/FrameLayout/FitWindowsFrameLayout/ContentFrameLayout/AlertDialogLayout/ScrollView/ButtonBarLayout/MaterialButton")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/0/0/action_bar_root/0/parentPanel/buttonPanel/0/0")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.launchDialog)).perform(click())

            onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }

    @Test
    fun alertDialogChoiceTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("Choice3")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/DecorView/LinearLayout/FrameLayout/FitWindowsFrameLayout/ContentFrameLayout/AlertDialogLayout/FrameLayout/RecycleListView/AppCompatCheckedTextView")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/0/0/action_bar_root/0/parentPanel/contentPanel/select_dialog_listview/-")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.itemsDialog)).perform(click())

            onView(withText("Choice3"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }
}
