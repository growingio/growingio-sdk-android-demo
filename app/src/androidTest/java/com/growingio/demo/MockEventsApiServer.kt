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

import android.os.Build
import android.text.TextUtils
import com.google.common.truth.Truth
import com.google.protobuf.ByteString
import com.growingio.android.protobuf.EventV3Protocol
import com.growingio.android.protobuf.ProtobufDataLoader
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.android.sdk.track.events.ActivateEvent
import com.growingio.android.sdk.track.events.AppClosedEvent
import com.growingio.android.sdk.track.events.AutotrackEventType
import com.growingio.android.sdk.track.events.ConversionVariablesEvent
import com.growingio.android.sdk.track.events.CustomEvent
import com.growingio.android.sdk.track.events.LoginUserAttributesEvent
import com.growingio.android.sdk.track.events.PageEvent
import com.growingio.android.sdk.track.events.PageLevelCustomEvent
import com.growingio.android.sdk.track.events.TrackEventType
import com.growingio.android.sdk.track.events.ViewElementEvent
import com.growingio.android.sdk.track.events.VisitEvent
import com.growingio.android.sdk.track.events.VisitorAttributesEvent
import com.growingio.android.sdk.track.events.base.BaseEvent
import com.growingio.android.sdk.track.events.hybrid.HybridCustomEvent
import com.growingio.android.sdk.track.events.hybrid.HybridPageEvent
import com.growingio.android.sdk.track.events.hybrid.HybridViewElementEvent
import com.growingio.android.sdk.track.middleware.format.EventFormatData
import com.growingio.android.sdk.track.providers.EventBuilderProvider
import com.growingio.demo.GrowingAndroidJUnitRunner.Companion.MOCK_SERVER_PORT
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder

/**
 * <p>
 *
 * @author cpacm 2023/7/24
 */
class MockEventsApiServer(private val eventDispatcher: (event: BaseEvent) -> Unit) {

    private val mockWebServer = MockWebServer()

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val uri = request.requestUrl
                checkPath(uri)

                val byteArray = request.body.readByteArray()
                dispatchEvent(byteArray)

                return MockResponse().setResponseCode(203)
            }
        }
    }

    fun start() {
        mockWebServer.start(MOCK_SERVER_PORT)
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    private fun dispatchEvent(byteArray: ByteArray) {
        val dataModel = GrowingAutotracker.get().context.registry.getModelLoader(EventFormatData::class.java)
        val eventList = if (dataModel is ProtobufDataLoader) {
            dispatchProtobufData(byteArray)
        } else {
            dispatchJsonData(String(byteArray))
        }
        eventList.forEach {
            checkBaseEvent(it)
            eventDispatcher.invoke(it)
        }
    }

    private fun dispatchProtobufData(byteArray: ByteArray): List<BaseEvent> {
        val eventList = arrayListOf<BaseEvent>()
        try {
            val listBuilder = EventV3Protocol.EventV3List.parseFrom(byteArray)

            for (dto in listBuilder.valuesList) {
                val json = transferPb2Json(dto)
                val event = parseJsonToEvent(json)
                if (event != null) eventList.add(event)
            }
        } catch (ignored: Exception) {
        }
        return eventList
    }

    private fun dispatchJsonData(json: String): List<BaseEvent> {
        val eventList = arrayListOf<BaseEvent>()
        try {
            val jsonArray = JSONArray(json)
            for (index in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(index)
                val event = parseJsonToEvent(jsonObject)
                if (event != null) eventList.add(event)
            }
        } catch (ignored: Exception) {
        }
        return eventList
    }

    private fun parseJsonToEvent(json: JSONObject): BaseEvent? {
        val eventType = json.getString(BaseEvent.EVENT_TYPE)

        val builder = when (eventType) {
            TrackEventType.VISIT -> VisitEvent.Builder()
            TrackEventType.CUSTOM -> {
                if (json.has("query")) {
                    HybridCustomEvent.Builder()
                } else if (json.has("path")) {
                    PageLevelCustomEvent.Builder()
                } else {
                    CustomEvent.Builder()
                }
            }

            TrackEventType.VISITOR_ATTRIBUTES -> VisitorAttributesEvent.Builder()
            TrackEventType.LOGIN_USER_ATTRIBUTES -> LoginUserAttributesEvent.Builder()
            TrackEventType.CONVERSION_VARIABLES -> ConversionVariablesEvent.Builder()
            TrackEventType.APP_CLOSED -> AppClosedEvent.Builder()
            AutotrackEventType.PAGE -> {
                if (!TextUtils.isEmpty(json.optString("query"))) {
                    HybridPageEvent.Builder()
                } else {
                    PageEvent.Builder()
                }
            }

            AutotrackEventType.VIEW_CLICK, AutotrackEventType.VIEW_CHANGE, TrackEventType.FORM_SUBMIT -> {
                if (!TextUtils.isEmpty(json.optString("query"))) {
                    HybridViewElementEvent.Builder(eventType)
                } else {
                    ViewElementEvent.Builder(eventType)
                }
            }

            TrackEventType.ACTIVATE -> ActivateEvent.Builder()
            else -> null
        } ?: return null
        EventBuilderProvider.parseFrom(builder, json)
        return builder.build()
    }

    private var globalEventId: String? = null

    private fun checkBaseEvent(baseEvent: BaseEvent) {
        val configurationProvider = GrowingAutotracker.get().context.configurationProvider
        Truth.assertThat(baseEvent.platform).isEqualTo("Android")
        Truth.assertThat(baseEvent.platformVersion).isEqualTo(Build.VERSION.RELEASE ?: "UNKNOWN")
        Truth.assertThat(baseEvent.deviceId).isNotEmpty()
        globalEventId?.apply { Truth.assertThat(this).isEqualTo(baseEvent.deviceId) }
        globalEventId = baseEvent.deviceId

        if (baseEvent.domain.isNotEmpty()) {
            Truth.assertThat(baseEvent.domain).isEqualTo("com.growingio.demo")
        }

        Truth.assertThat(baseEvent.urlScheme).isEqualTo(configurationProvider.core().getUrlScheme())
        Truth.assertThat(baseEvent.appState).isIn(listOf("FOREGROUND", "BACKGROUND"))
        Truth.assertThat(baseEvent.eventSequenceId).isAtLeast(0)
    }

    private fun checkPath(uri: HttpUrl?) {
        val testPath = "/v3/projects/${GrowingAndroidJUnitRunner.PROJECT_ID}/collect"

        Truth.assertThat(uri).isNotNull()
        Truth.assertThat(uri!!.encodedPath).isEqualTo(testPath)

        val tm = uri.queryParameter("stm")?.toLong() ?: 0L
        Truth.assertThat(System.currentTimeMillis() - tm).isAtMost(5000)
    }

    private fun transferPb2Json(dto: EventV3Protocol.EventV3Dto): JSONObject {
        val json = JSONObject()
        dto.javaClass.methods.forEach {
            val name = it.name
            val jsonName = StringBuilder()
            val delName = it.name.removePrefix("get")
            delName.forEach {
                if (delName.indexOf(it) == 0) {
                    jsonName.append(it.lowercaseChar())
                } else {
                    jsonName.append(it)
                }
            }
            if (name.startsWith("get") &&
                it.returnType != ByteString::class.java &&
                it.parameterCount == 0
            ) {
                val value = it.invoke(dto)
                value?.apply {
                    when (it.returnType) {
                        String::class.java -> {
                            json.put(jsonName.toString(), this.toString())
                        }

                        Int::class.java -> {
                            json.put(jsonName.toString(), this as Int)
                        }

                        Long::class.java -> {
                            json.put(jsonName.toString(), this as Long)
                        }

                        Double::class.java -> {
                            json.put(jsonName.toString(), this as Double)
                        }

                        EventV3Protocol.EventType::class.java -> {
                            json.put(jsonName.toString(), this.toString())
                        }

                        Map::class.java -> {
                            val map = this as Map<String, String>
                            val jsonObject = JSONObject()
                            map.forEach { key, value ->
                                jsonObject.put(key, value)
                            }
                            json.put(jsonName.toString(), jsonObject)
                        }
                    }
                }
            }
        }
        return json
    }
}
