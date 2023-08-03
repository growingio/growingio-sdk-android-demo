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

package com.growingio.demo

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.AppClosedEvent
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.providers.EventBuilderProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : AbstractGrowingTestUnit() {

    @Rule
    @JvmField
    val activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)


    @Before
    override fun setup() {
        super.setup()

        activityScenarioRule.scenario.onActivity {
            GrowingAutotracker.get().autotrackPage(it, "MainActivity")
        }
    }

    @Test
    fun openMainActivity() {
        waitEvent(AutotrackEventType.PAGE)
        //waitEvent(TrackEventType.APP_CLOSED)

        activityScenarioRule.scenario.close()
    }

    override fun onEvent(baseEvent: BaseEvent): Boolean {
        println(EventBuilderProvider.toJson(baseEvent))
        if (baseEvent is PageEvent) {
            return true
        }

        if (baseEvent is AppClosedEvent) {
            Truth.assertThat(baseEvent.eventSequenceId).isEqualTo(0)
            return true
        }

        return false
    }
}