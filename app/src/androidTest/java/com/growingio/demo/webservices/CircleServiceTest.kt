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

package com.growingio.demo.webservices

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.Uninterruptibles
import com.growingio.android.sdk.track.TrackMainThread
import com.growingio.demo.GrowingAndroidJUnitRunner
import com.growingio.demo.MainActivity
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


/**
 * <p>
 *
 * @author cpacm 2023/8/9
 */
@RunWith(AndroidJUnit4::class)
class CircleServiceTest : WebServicesTest() {

    @Test
    fun webCircleTest() {
        val uri =
            "${GrowingAndroidJUnitRunner.URL_SCHEME}://growingio/webservice?serviceType=circle&wsUrl=" + Uri.encode(
                wsUrl,
            )
        val intent = Intent()
        intent.data = Uri.parse(uri)
        ActivityScenario.launch<MainActivity>(intent)

        setOnReceivedMessageListener(object : OnReceivedMessageListener {
            override fun onReceivedRefreshScreenshotMessage(message: JSONObject?) {
                message?.remove("screenshot")
                val jsonArray = message!!.getJSONArray("elements")
                for (i in 0 until jsonArray.length()) {
                    val element = jsonArray.getJSONObject(i).getString("xpath")
                    assertThat(element).startsWith("/MainActivity/DecorView/LinearLayout/FrameLayout/FitWindowsLinearLayout/ContentFrameLayout/ConstraintLayout/FragmentContainerView/FragmentContainerView/ConstraintLayout")
                }
            }
        })
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS)

        val activity = TrackMainThread.trackMain().foregroundActivity

        onView(withId(com.growingio.android.sdk.track.R.id.growing_webservices_tip_view))
            .inRoot(withDecorView(not(`is`(activity.window.decorView))))
            .check(matches(isDisplayed()))

        onView(withId(com.growingio.android.sdk.track.R.id.growing_webservices_tip_view))
            .inRoot(withDecorView(not(`is`(activity.window.decorView))))
            .perform(click())

        onView(ViewMatchers.withText(activity.getString(com.growingio.android.circler.R.string.growing_circler_exit)))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
    }
}
