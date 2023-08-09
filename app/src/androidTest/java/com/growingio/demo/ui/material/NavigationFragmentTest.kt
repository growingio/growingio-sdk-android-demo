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

import androidx.core.view.GravityCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import com.growingio.demo.openDrawer
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
class NavigationFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<NavigationFragment>()
    }

    @Test
    fun bottomNavigationTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEqualTo("/NavigationFragment")
                Truth.assertThat(baseEvent.textValue).isEqualTo("控件")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/NavigationFragment/DrawerLayout/CoordinatorLayout/ConstraintLayout/BottomNavigationView/BottomNavigationMenuView/BottomNavigationItemView")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/0/0/navBar/0/ui")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.ui)).perform(click())
        }
    }

    @Test
    fun navigationViewTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEqualTo("/NavigationFragment")
                Truth.assertThat(baseEvent.textValue).isEqualTo("功能")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/NavigationFragment/DrawerLayout/NavigationView/NavigationMenuView/NavigationMenuItemView")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/navigationView/design_navigation_view/-")
                Truth.assertThat(baseEvent.index).isEqualTo(2)
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.drawer)).perform(openDrawer(GravityCompat.START))

            onView(withId(R.id.search_item)).perform(click())
        }
    }

    @Test
    fun tabLayoutTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEqualTo("/NavigationFragment")
                Truth.assertThat(baseEvent.textValue).isEqualTo("Maps")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/NavigationFragment/DrawerLayout/CoordinatorLayout/AppBarLayout/TabLayout/SlidingTabIndicator/TabView")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/0/appbarLayout/tabLayout/0/2")
                Truth.assertThat(baseEvent.index).isEqualTo(-1)
                return@runEventTest true
            }
            false
        }) {
            onView(withText("Maps")).perform(click())
        }
    }
}
