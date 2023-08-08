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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentEventTimerBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2023/5/04
 */
@AndroidEntryPoint
class SdkEventTimerFragment : PageFragment<FragmentEventTimerBinding>() {

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEventTimerBinding {
        return FragmentEventTimerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.sdk_event_timer))

        defaultState()

        initTimerView()

        loadAssetCode(this)

        setDefaultLogFilter("level:debug TimerCenter")
    }


    private var isTimerRunning = false
    private var timerStartMill = 0L
    private var timerId: String? = null

    @SourceCode
    private fun initTimerView(){
        pageBinding.start.setOnClickListener {

            val timer = pageBinding.timerInput.editText?.text
            if (timer == null || timer.toString().isEmpty()) {
                showMessage(R.string.sdk_event_timer_toast)
                return@setOnClickListener
            }
            timerId = GrowingAutotracker.get().trackTimerStart(timer.toString())

            startState()
        }

        pageBinding.pause.setOnClickListener {
            GrowingAutotracker.get().trackTimerPause(timerId)
            pauseState()
        }

        pageBinding.resume.setOnClickListener {
            GrowingAutotracker.get().trackTimerResume(timerId)
            startState()
        }

        pageBinding.end.setOnClickListener {
            GrowingAutotracker.get().trackTimerEnd(timerId, mapOf("app" to "demo", "tag" to "TimerCenter"))
            defaultState()
        }

        pageBinding.delete.setOnClickListener {
            GrowingAutotracker.get().removeTimer(timerId)
            defaultState()
        }

        pageBinding.clean.setOnClickListener {
            GrowingAutotracker.get().clearTrackTimer()
            defaultState()
        }
    }

    private fun defaultState() {
        isTimerRunning = false
        timerStartMill = 0L
        timerId = null
        pageBinding.start.isEnabled = true
        pageBinding.pause.isEnabled = false
        pageBinding.resume.isEnabled = false
        pageBinding.end.isEnabled = false
        pageBinding.delete.isEnabled = false
        pageBinding.clean.isEnabled = false
    }

    private fun startState() {
        isTimerRunning = true
        pageBinding.start.isEnabled = false
        pageBinding.pause.isEnabled = true
        pageBinding.resume.isEnabled = false
        pageBinding.end.isEnabled = true
        pageBinding.delete.isEnabled = true
        pageBinding.clean.isEnabled = true

        startTimerMill()
    }

    private fun pauseState() {
        isTimerRunning = false
        pageBinding.start.isEnabled = false

        pageBinding.pause.isEnabled = false
        pageBinding.resume.isEnabled = true
        pageBinding.end.isEnabled = true

        pageBinding.delete.isEnabled = true
        pageBinding.clean.isEnabled = true
    }

    private fun startTimerMill() {
        lifecycleScope.launch {
            while (isTimerRunning && timerId != null) {
                pageBinding.timer.text = SimpleDateFormat("mm:ss.SS", Locale.getDefault()).format(timerStartMill)
                delay(100)
                timerStartMill += 100
            }
        }
    }

    override fun onStart() {
        super.onStart()
        isTimerRunning = true
        startTimerMill()
    }

    override fun onStop() {
        super.onStop()
        isTimerRunning = false
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 16,
                icon = SdkIcon.Api,
                title = "事件计时器",
                desc = "用来计算自定义事件从产生到结束经过的时间",
                route = PageNav.SdkEventTimerPage.route(),
                fragmentClass = SdkEventTimerFragment::class
            )
        }
    }
}