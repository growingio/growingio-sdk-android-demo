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

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.R
import com.growingio.demo.clickOnViewChild
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
class RecyclerViewFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<RecyclerViewFragment>()
    }

    @Test
    fun onRecyclerItemTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.path).isEqualTo("/RecyclerViewFragment")
                Truth.assertThat(baseEvent.index).isEqualTo(5)
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/RecyclerViewFragment/ConstraintLayout/RecyclerView/MaterialCardView/ConstraintLayout/MaterialButton")
                Truth.assertThat(baseEvent.xIndex).isEqualTo("/0/content/0/recycler/-/0/action")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.recycler)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(5, clickOnViewChild(R.id.action)),
            )
        }
    }
}
