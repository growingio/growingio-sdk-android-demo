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

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.demo.R
import com.growingio.android.sdk.track.events.AppClosedEvent
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.providers.EventBuilderProvider
import com.growingio.demo.AbstractGrowingTestUnit
import com.growingio.demo.clickOnViewChild
import com.growingio.demo.launchFragmentInHiltContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
class DashboardFragmentTest : AbstractGrowingTestUnit() {

    val scenario = launchFragmentInHiltContainer<DashboardFragment>()

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun onDocumentButtonTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.textValue).isEqualTo("查看文档 >>")
                Truth.assertThat(baseEvent.path).isEqualTo("/DashboardFragment")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/AndroidXFragment/CoordinatorLayout/RecyclerView/ConstraintLayout/MaterialCardView/MaterialButton")
                Truth.assertThat(baseEvent.xIndex).contains("/0/content/0/dashboardRv/-/sdkBanner/docLink")
            }
            true
        }) {
            onView(withId(R.id.dashboardRv)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.docLink))
            )
        }

    }

}