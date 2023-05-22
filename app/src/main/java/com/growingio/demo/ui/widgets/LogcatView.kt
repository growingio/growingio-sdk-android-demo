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

package com.growingio.demo.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.demo.R
import com.growingio.demo.data.GrowingIOLoggerItem
import com.growingio.demo.util.GrowingLoggerManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*

/**
 * <p>
 *
 * @author cpacm 2023/4/23
 */
class LogcatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val logcatList: RecyclerView
    private val logcatMaximize: ImageView
    private val logcatClear: ImageView
    private val logcatClean: ImageView
    private val logcatClose: ImageView
    private val filterTv: AutoCompleteTextView

    private var closeAction: (() -> Unit)? = null

    private val logcatAdapter: LoggerAdapter
    private var isMaximize = false

    init {

        LayoutInflater.from(context).inflate(R.layout.logcat_layout, this, true)

        logcatList = findViewById(R.id.logcatList)
        logcatMaximize = findViewById(R.id.logcatMaximize)
        logcatClear = findViewById(R.id.logcatClear)
        logcatClean = findViewById(R.id.logcatClean)
        logcatClose = findViewById(R.id.logcatClose)
        filterTv = findViewById(R.id.logcatFilter)

        filterTv.setOnItemClickListener { _, _, position, _ ->
            val value = filterTv.adapter.getItem(position) as String
            filterTv.setText("", false)
            filterTv.append("level:${value.lowercase()} ")
        }

        filterTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                filterLogcat(s)
            }
        })

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = true
        logcatList.layoutManager = linearLayoutManager

        logcatAdapter = LoggerAdapter(context) { _ ->
            logcatList.scrollToPosition(0)
        }
        logcatList.adapter = logcatAdapter

        logcatClear.setOnClickListener {
            filterTv.setText("", false)
        }
        logcatClose.setOnClickListener {
            closeAction?.invoke()
        }
        logcatClean.setOnClickListener {
            logcatAdapter.cleanLogcat()
        }

        logcatMaximize.setOnClickListener {
            switchHeight()
        }

    }

    fun setDefaultLogFilter(text: String) {
        filterTv.setText(text, false)
    }


    private fun switchHeight() {
        if (parent !is ConstraintLayout) return
        layoutParams = if (isMaximize) getMinimizeHeightLayoutParams()
        else getMaximizeHeightLayoutParams()
        isMaximize = !isMaximize
    }

    private fun getMinimizeHeightLayoutParams(): ConstraintLayout.LayoutParams {
        if (!isMaximize) return layoutParams as ConstraintLayout.LayoutParams
        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.logcat_minimize_height)
        layoutParams.bottomToTop = R.id.tabGroup
        layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET
        return layoutParams
    }

    private fun getMaximizeHeightLayoutParams(): ConstraintLayout.LayoutParams {
        if (isMaximize) return layoutParams as ConstraintLayout.LayoutParams
        val layoutParams = layoutParams as ConstraintLayout.LayoutParams
        layoutParams.height = 0
        layoutParams.bottomToTop = R.id.tabGroup
        layoutParams.topToBottom = R.id.appbarLayout
        return layoutParams
    }

    private fun filterLogcat(s: Editable) {
        val log = s.toString()
        val regex = """^(level:(verbose|debug|info|warn|error))($|( (.*))+)""".toRegex()
        val matchResult = regex.find(log)
        if (matchResult != null && matchResult.groupValues.size >= 6) {
            val levelSpanGroup = matchResult.groups[1]
            val level = matchResult.groups[2]
            if (levelSpanGroup != null && level != null) {
                s.setSpan(
                    BackgroundColorSpan(getFilterTextBackgroundColor(level.value)),
                    levelSpanGroup.range.first,
                    levelSpanGroup.range.count(),
                    0
                )

                s.setSpan(
                    ForegroundColorSpan(getFilterTextForegroundColor(level.value)),
                    levelSpanGroup.range.first,
                    levelSpanGroup.range.count(),
                    0
                )
            }
            val logStr = matchResult.groups[5]?.value ?: ""
            logcatAdapter.setFilter(log, getLogLevel(level?.value ?: "debug"), logStr.trim())

        } else {
            logcatAdapter.setFilter(log.trim(), Log.VERBOSE, log.trim())
        }
    }

    fun closeActionWith(closeAction: () -> Unit) {
        this.closeAction = closeAction
    }

    private fun getLogLevel(log: String): Int {
        return when (log.lowercase().first()) {
            'v' -> Log.VERBOSE
            'i' -> Log.INFO
            'd' -> Log.DEBUG
            'w' -> Log.WARN
            'e' -> Log.ERROR
            else -> Log.VERBOSE
        }
    }

    private fun getFilterTextBackgroundColor(log: String): Int {
        return when (getLogLevel(log)) {
            Log.VERBOSE -> context.getColor(R.color.logcat_verbose_background)
            Log.INFO -> context.getColor(R.color.logcat_info_background)
            Log.DEBUG -> context.getColor(R.color.logcat_debug_background)
            Log.WARN -> context.getColor(R.color.logcat_warn_background)
            Log.ERROR -> context.getColor(R.color.logcat_error_background)
            else -> context.getColor(R.color.transparent)
        }
    }

    private fun getFilterTextForegroundColor(log: String): Int {
        return when (getLogLevel(log)) {
            Log.VERBOSE -> context.getColor(R.color.logcat_verbose_foreground)
            Log.INFO -> context.getColor(R.color.logcat_info_foreground)
            Log.DEBUG -> context.getColor(R.color.logcat_debug_foreground)
            Log.WARN -> context.getColor(R.color.logcat_warn_foreground)
            Log.ERROR -> context.getColor(R.color.logcat_error_foreground)
            else -> context.getColor(R.color.logcat_debug_foreground)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    class LoggerAdapter(context: Context, private val scrollAction: (count: Int) -> Unit) :
        RecyclerView.Adapter<LoggerAdapter.LoggerViewHolder>(),
        GrowingLoggerManager.LoggerObserver {

        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface GrowingLoggerProvider {
            fun loggerManager(): GrowingLoggerManager
        }

        private val loggerManager: GrowingLoggerManager
        private var filterLevel = Log.VERBOSE
        private var filterTag = ""

        init {
            loggerManager =
                EntryPoints.get(context.applicationContext, GrowingLoggerProvider::class.java).loggerManager()
            loggerManager.addLoggerObserver(this)
        }

        private val loggerList: ArrayList<GrowingIOLoggerItem> by lazy {
            ArrayList(loggerManager.provideLoggerQueue().reversed())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoggerViewHolder {
            return LoggerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_logcat_item, parent, false)
            )
        }

        override fun onLoggerChanged(item: GrowingIOLoggerItem) {
            // don't use logger in here.
            if (item.level < filterLevel) return
            if (filterTag.isEmpty() || item.tag.contains(filterTag, true) || item.msg.contains(filterTag, true)) {
                loggerList.add(0, item)
                notifyItemInserted(0)
                scrollAction.invoke(itemCount)
            }
        }

        override fun getItemCount(): Int {
            return loggerList.size
        }

        override fun onBindViewHolder(holder: LoggerViewHolder, position: Int) {
            holder.bind(loggerList.elementAt(position))
        }

        fun cleanLogcat() {
            loggerManager.clear()
            loggerList.clear()
            notifyDataSetChanged()
        }

        fun setFilter(origin: String, level: Int, tag: String) {
            if (level == filterLevel && tag == filterTag) return
            filterLevel = level
            filterTag = tag
            val originData = loggerManager.provideLoggerQueue().reversed()
            val filterData = originData.filter {
                it.level >= level && (tag.isEmpty() || it.tag.contains(tag, true) || it.msg.contains(tag, true))
            }
            loggerList.clear()
            loggerList.addAll(filterData)
            notifyDataSetChanged()
            scrollAction.invoke(itemCount)
        }

        class LoggerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val logTime: TextView = itemView.findViewById(R.id.logTime)
            private val logLevel: TextView = itemView.findViewById(R.id.logLevel)
            private val logTag: TextView = itemView.findViewById(R.id.logTag)
            private val logMessage: TextView = itemView.findViewById(R.id.logMessage)

            @SuppressLint("SetTextI18n")
            fun bind(item: GrowingIOLoggerItem) {
                //2023-06-12 20:00:00.614
                logTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(item.time)
                logTag.text = "[${item.tag}]"
                logMessage.text = item.msg
                when (item.level) {
                    Log.VERBOSE -> {
                        logLevel.text = "V"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_verbose_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_verbose_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_verbose_background))
                    }
                    Log.INFO -> {
                        logLevel.text = "I"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_info_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_info_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_info_background))
                    }
                    Log.WARN -> {
                        logLevel.text = "W"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_warn_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_warn_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_warn_background))
                    }
                    Log.ERROR -> {
                        logLevel.text = "E"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_error_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_error_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_error_background))
                    }
                    Log.DEBUG -> {
                        logLevel.text = "D"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_debug_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_debug_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_debug_background))
                    }
                    else -> {
                        logLevel.text = "D"
                        logLevel.setTextColor(itemView.context.getColor(R.color.logcat_debug_foreground))
                        logLevel.setBackgroundColor(itemView.context.getColor(R.color.logcat_debug_background))
                        logTag.setTextColor(itemView.context.getColor(R.color.logcat_debug_background))
                    }
                }
            }
        }
    }
}