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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
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
class MenuFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<MenuFragment>()
    }

    @Test
    fun popupMenuTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("One")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/MenuView/MenuItem")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/one")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.menuShow)).perform(click())

            onView(withText("One"))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }

    @Test
    fun listPopupMenuTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("Item 4")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/PopupDecorView/PopupBackgroundView/DropDownListView/MaterialTextView")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/0/0/-")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.menuShowPopup)).perform(click())

            onView(withText("Item 4"))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }

    @Test
    fun contextMenuTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("Copy")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/MenuView/MenuItem")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/0")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.menuShowContext)).perform(longClick())

            onView(withText("Copy"))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }

    @Test
    fun toolbarMenuTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEmpty()
                Truth.assertThat(baseEvent.textValue).isEqualTo("One")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/MenuView/MenuItem")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/0/one")
                return@runEventTest true
            }
            false
        }) {
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
            onView(withText("One"))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()))
                .perform(click())
        }
    }
}
