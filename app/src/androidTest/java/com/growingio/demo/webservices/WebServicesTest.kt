/*
 * Copyright (C) 2023 Beijing Yishu Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.growingio.demo.webservices

import com.google.common.truth.Truth
import com.growingio.demo.GrowingAndroidJUnitRunner
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONException
import org.json.JSONObject
import org.junit.Before

abstract class WebServicesTest {
    private var mMockWebServer: MockWebServer? = null
    private var mWebSocket: WebSocket? = null
    private var mOnReceivedMessageListener: OnReceivedMessageListener? = null

    @Before
    fun setUp() {
        mMockWebServer = MockWebServer()
        mMockWebServer!!.enqueue(
            MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    mWebSocket = webSocket
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val message = JSONObject(text)
                        val msgType = message.getString("msgType")
                        when (msgType) {
                            "ready" -> handleReadyMessage(message)
                            "client_info" -> handleClientInfoMessage(message)
                            "refreshScreenshot" -> if (mOnReceivedMessageListener != null) {
                                mOnReceivedMessageListener!!.onReceivedRefreshScreenshotMessage(message)
                            }
                            "debugger_data" -> sendMessage(JSONObject().put("msgType", "logger_open").toString())
                            "logger_data" -> sendMessage(JSONObject().put("msgType", "logger_close").toString())
                            "quit" -> {}
                            else -> Truth.assertWithMessage("Unknown msgType = $msgType").fail()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Truth.assertWithMessage(e.message).fail()
                    }
                }
            }),
        )
        mMockWebServer?.start()
    }

    protected fun setOnReceivedMessageListener(onReceivedMessageListener: OnReceivedMessageListener?) {
        mOnReceivedMessageListener = onReceivedMessageListener
    }

    protected val wsUrl: String
        get() {
            val hostName = mMockWebServer!!.hostName
            val port = mMockWebServer!!.port
            return "ws://$hostName:$port/"
        }

    private fun sendMessage(text: String) {
        Truth.assertThat(mWebSocket != null).isTrue()
        mWebSocket!!.send(text)
    }

    /**
     * {
     * "msgType": "ready",
     * "projectId": "0a1b4118dd954ec3bcc69da5138bdb96",
     * "timestamp": 1595318916686,
     * "domain": "com.cliff.release11demo",
     * "sdkVersion": "3.0.9",
     * "sdkVersionCode": "9",
     * "os": "Android",
     * "screenWidth": 720,
     * "screenHeight": 1520
     * }
     */
    @Throws(JSONException::class)
    private fun handleReadyMessage(message: JSONObject) {
        Truth.assertThat(message.getString("msgType")).isEqualTo("ready")
        Truth.assertThat(message.getString("projectId")).isEqualTo(GrowingAndroidJUnitRunner.PROJECT_ID)
        Truth.assertThat(message.getLong("timestamp") > 0).isTrue()
        Truth.assertThat(message.getString("domain")).isEqualTo("com.growingio.demo")
        Truth.assertThat(message.getString("sdkVersion")).isNotEmpty()
        Truth.assertThat(message.getInt("sdkVersionCode") > 0).isTrue()
        Truth.assertThat(message.getString("os")).isEqualTo("Android")
        Truth.assertThat(message.getInt("screenWidth") > 0).isTrue()
        Truth.assertThat(message.getInt("screenHeight") > 0).isTrue()
        sendMessage(JSONObject().put("msgType", "ready").toString())
    }

    /**
     * {
     * "msgType": "client_info",
     * "sdkVersion": "3.0.0",
     * "data": {
     * "os": "iOS",
     * "appVersion": "1.0",
     * "appChannel": "App Store",
     * "osVersion": "9.0",
     * "deviceType": "iPhone",
     * "deviceBrand": "Apple",
     * "deviceModel": "iPhone7,2"
     * }
     * }
     */
    @Throws(JSONException::class)
    private fun handleClientInfoMessage(message: JSONObject) {
        Truth.assertThat(message.getString("msgType")).isEqualTo("client_info")
        Truth.assertThat(message.getString("sdkVersion")).isNotEmpty()
        val data = message.getJSONObject("data")
        Truth.assertThat(data.getString("os")).isEqualTo("Android")
        Truth.assertThat(data.getString("appVersion")).isNotEmpty()
        //        Truth.assertThat(data.getString("appChannel")).isNotEmpty();
        Truth.assertThat(data.getString("deviceType")).isNotEmpty()
        Truth.assertThat(data.getString("deviceBrand")).isNotEmpty()
        Truth.assertThat(data.getString("deviceModel")).isNotEmpty()
    }

    interface OnReceivedMessageListener {
        @Throws(JSONException::class)
        fun onReceivedRefreshScreenshotMessage(message: JSONObject?)
    }
}
