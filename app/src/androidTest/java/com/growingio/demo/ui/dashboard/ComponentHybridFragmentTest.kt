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

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.android.sdk.track.events.hybrid.HybridCustomEvent
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
class ComponentHybridFragmentTest : AbstractGrowingTestUnit() {

    @Before
    override fun setup() {
        super.setup()
        launchFragmentInHiltContainer<ComponentHybridFragment>()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).startsWith("SDKAutoCheck中文测试")
                Truth.assertThat(baseEvent.path).isEqualTo("/android_asset/")
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
        }
    }

    @Test
    fun hybridTrackTest() {
        runEventTest(TrackEventType.CUSTOM, timeout = 10, onEvent = { baseEvent ->
            if (baseEvent is HybridCustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("test")
                Truth.assertThat(baseEvent.path).isEqualTo("/android_asset/")
                return@runEventTest true
            }
            false
        }) {
            onWebView(ViewMatchers.withId(R.id.hybridWebView))
                .withElement(findElement(Locator.ID, "track"))
                .perform(webClick())
        }
    }

    @Test
    fun hybridClickTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, timeout = 10, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CLICK)
                Truth.assertThat(baseEvent.xpath).isEqualTo("/div.main/div.title/div/div.title/button#track")
                Truth.assertThat(baseEvent.path).isEqualTo("/android_asset/")
                Truth.assertThat(baseEvent.platform).isEqualTo("android")
                return@runEventTest true
            }
            false
        }) {
            onWebView(ViewMatchers.withId(R.id.hybridWebView))
                .withElement(findElement(Locator.ID, "track"))
                .perform(webClick())
        }
    }

    @Test
    fun hybridAttrsTest() {
        runEventTest(TrackEventType.CUSTOM, timeout = 10, onEvent = { baseEvent ->
            if (baseEvent is HybridCustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("test")
                Truth.assertThat(baseEvent.path).isEqualTo("/android_asset/")
                Truth.assertThat(baseEvent.attributes).hasSize(1)
                Truth.assertThat(baseEvent.attributes).containsEntry("type", "hjh")
                return@runEventTest true
            }
            false
        }) {
            onWebView(ViewMatchers.withId(R.id.hybridWebView))
                .withElement(findElement(Locator.ID, "pro"))
                .perform(webClick())
        }
    }

    @Test
    fun hybridUserTest() {
        runEventTest(TrackEventType.CUSTOM, timeout = 10, onEvent = { baseEvent ->
            if (baseEvent is HybridCustomEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.CUSTOM)
                Truth.assertThat(baseEvent.eventName).isEqualTo("test")
                Truth.assertThat(baseEvent.path).isEqualTo("/android_asset/")
                Truth.assertThat(baseEvent.userId).isEqualTo("cpacm@gmail.com")
                Truth.assertThat(baseEvent.userKey).isEqualTo("email")
                return@runEventTest true
            }
            false
        }) {
            onWebView(ViewMatchers.withId(R.id.hybridWebView))
                .withElement(findElement(Locator.ID, "user"))
                .perform(webClick())

            onWebView(ViewMatchers.withId(R.id.hybridWebView))
                .withElement(findElement(Locator.ID, "track"))
                .perform(webClick())
        }
    }
}
