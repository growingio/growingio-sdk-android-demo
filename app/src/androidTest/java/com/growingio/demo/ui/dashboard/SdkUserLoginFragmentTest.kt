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
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.LoginUserAttributesEvent
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.android.sdk.track.events.VisitEvent
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
class SdkUserLoginFragmentTest : AbstractGrowingTestUnit() {

    val scenario = launchFragmentInHiltContainer<SdkUserLoginFragment>()

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun onPageTest() {
        runEventTest(AutotrackEventType.PAGE, onEvent = { baseEvent ->
            if (baseEvent is PageEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.PAGE)
                Truth.assertThat(baseEvent.title).isEqualTo("用户登录")
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkUserLoginFragment")
                return@runEventTest true
            }
            false
        }) {
        }
    }

    @Test
    fun editViewChangeTest() {
        runEventTest(AutotrackEventType.VIEW_CHANGE, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(AutotrackEventType.VIEW_CHANGE)
                Truth.assertThat(baseEvent.textValue).isEmpty()
                Truth.assertThat(baseEvent.path).isEqualTo("/SdkUserLoginFragment")
                Truth.assertThat(baseEvent.xpath)
                    .isEqualTo("/HiltTestActivity/SdkUserLoginFragment/ConstraintLayout/FrameLayout/ConstraintLayout/TextInputLayout/FrameLayout/TextInputEditText")
                Truth.assertThat(baseEvent.xIndex).isAnyOf(
                    "/0/content/0/apiLayout/0/userKey/0/userKeyEditText",
                    "/0/content/0/apiLayout/0/userId/0/userIdEditText",
                )
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.userIdEditText)).perform(typeText("cpacm@gmail.com"))
            onView(withId(R.id.userKeyEditText)).perform(typeText("email"), closeSoftKeyboard())
        }
    }

    @Test
    fun userLoginTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                val userInfoProvider = GrowingAutotracker.get().context.userInfoProvider
                Truth.assertThat(userInfoProvider.loginUserKey).isEqualTo("email")
                Truth.assertThat(userInfoProvider.loginUserId).isEqualTo("cpacm@gmail.com")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.userIdEditText)).perform(typeText("cpacm@gmail.com"), closeSoftKeyboard())
            onView(withId(R.id.userKeyEditText)).perform(typeText("email"), closeSoftKeyboard())
            onView(withId(R.id.login)).perform(click())
        }
    }

    @Test
    fun switchUserTest() {
        runEventTest(TrackEventType.VISIT, timeout = 15, onEvent = { baseEvent ->
            if (baseEvent is VisitEvent) {
                if (baseEvent.userKey.isNullOrEmpty()) return@runEventTest false
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.VISIT)
                Truth.assertThat(baseEvent.androidId).isNotEmpty()
                Truth.assertThat(baseEvent.userKey).isEqualTo("phone")
                Truth.assertThat(baseEvent.userId).isEqualTo("110011")
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.userIdEditText)).perform(typeText("cpacm"), closeSoftKeyboard())
            onView(withId(R.id.login)).perform(click())
            onView(withId(R.id.userIdEditText)).perform(clearText())
            onView(withId(R.id.userIdEditText)).perform(typeText("110011"), closeSoftKeyboard())
            onView(withId(R.id.userKeyEditText)).perform(typeText("phone"), closeSoftKeyboard())
            onView(withId(R.id.login)).perform(click())
        }
    }

    @Test
    fun userAttributesTest() {
        runEventTest(TrackEventType.LOGIN_USER_ATTRIBUTES, timeout = 8, onEvent = { baseEvent ->
            if (baseEvent is LoginUserAttributesEvent) {
                Truth.assertThat(baseEvent.eventType).isEqualTo(TrackEventType.LOGIN_USER_ATTRIBUTES)
                Truth.assertThat(baseEvent.attributes).hasSize(3)
                Truth.assertThat(baseEvent.attributes).containsEntry("name", "kun")
                Truth.assertThat(baseEvent.attributes).containsEntry("age", "2.5")
                Truth.assertThat(baseEvent.attributes)
                    .containsEntry("hobby", "singing||dancing||rap||playing basketball")
                TrackEventType.LOGIN_USER_ATTRIBUTES
                return@runEventTest true
            }
            false
        }) {
            onView(withId(R.id.userAttrs)).perform(click())
        }
    }

    @Test
    fun userClearTest() {
        runEventTest(AutotrackEventType.VIEW_CLICK, timeout = 8, eventCount = 2, onEvent = { baseEvent ->
            if (baseEvent is ViewElementEvent) {
                val userInfoProvider = GrowingAutotracker.get().context.userInfoProvider
                Truth.assertThat(userInfoProvider.loginUserKey).isEmpty()
                Truth.assertThat(userInfoProvider.loginUserId).isEmpty()
                return@runEventTest true
            }
            false
        }, validateAtLast = true) {
            onView(withId(R.id.userIdEditText)).perform(typeText("cpacm@gmail.com"))
            onView(withId(R.id.userKeyEditText)).perform(typeText("email"), closeSoftKeyboard())
            onView(withId(R.id.login)).perform(click())

            onView(withId(R.id.userClear)).perform(click())
        }
    }
}
