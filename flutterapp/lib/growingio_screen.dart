/*
 *   Copyright (c) - 2024 Beijing Yishu Technology Co., Ltd.
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
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class GrowingSdkScreen extends StatefulWidget {
  GrowingSdkScreen({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _GrowingSdkScreenState();
  }
}

class _GrowingSdkScreenState extends State<GrowingSdkScreen> {
  @override
  Widget build(BuildContext context) {
    return Container(
      child: ListView(
        padding: const EdgeInsets.all(20.0),
        children: [
          ElevatedButton(
              child: const Text("startWithConfiguration"),
              onPressed: () {}),
          ElevatedButton(
              child: const Text("enableDataCollect"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("disableDataCollect"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("setLoginUserId"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("setLoginUserIdAndUserKey"),
              onPressed: () {

              }),
          ElevatedButton(
              child: const Text("cleanLoginUserId"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("setLocation"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("cleanLocation"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("setLoginUserAttributes"),
              onPressed: () {
                const l1 = ['a', 'b', 'c'];
                const l2 = [1234569807, -123456];
                const l3 = [null, '', 0];
                Map<String, List> mapUser = {
                  "col1 row1": l1,
                  "col1 row2": l2,
                  "col1 row3": l3
                };

              }),
          const GetDeviceIdRow(),
          ElevatedButton(
              child: const Text("trackCustomEvent"),
              onPressed: () {
                GrowingAutotracker.get()
                    .trackCustomEvent(eventName: "CSTM");
              }),
          ElevatedButton(
              child: const Text("trackCustomEventWithAttributes"),
              onPressed: () {
                const l1 = ['a2', 'b2', 'c2'];
                const l2 = [1234569807, -123456];
                const l3 = [null, '', 0];
                Map<String, List> mapTrack = {
                  "col1 row1": l1,
                  "col1 row2": l2,
                  "col1 row3": l3
                };

              }),
          ElevatedButton(
              child: const Text("setGeneralProps"),
              onPressed: () {
                Map<String, dynamic> props = {
                  "col1 row1": 100,
                  "key1": "value1",
                  "key2": "value2"
                };
              }),
          ElevatedButton(
              child: const Text("removeGeneralProps"),
              onPressed: () {
                List<String> keys = ["col1 row1", "key1"];
              }),
          ElevatedButton(
              child: const Text("clearGeneralProps"),
              onPressed: () {
              }),
          ElevatedButton(
              child: const Text("doDeeplinkByUrl"),
              onPressed: () {
                if (Platform.isAndroid) {
                  GrowingAutotracker.get().doDeepLinkByUrl(
                      "https://ads-uat.growingio.cn/k4vudaJY",
                          (params, error, appAwakePassedTime) {
                        print(
                            "doDeepLinkByUrl params: $params aaa: ${params['aaa']}");
                      });
                } else if (Platform.isIOS) {
                  GrowingAutotracker.get().doDeepLinkByUrl(
                      "https://ads-uat.growingio.cn/k4osdxyw",
                          (params, error, appAwakePassedTime) {
                        print("doDeepLinkByUrl params: $params");
                      });
                }
              }),
          Builder(
            builder: (context) => ElevatedButton(
                child: const Text("trackTimer"),
                onPressed: () {
                  Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => TrackTimerPage()));
                }),
          ),
          ElevatedButton(
            child: const Text("跳转webview"),
            onPressed: _jumpToNativeMethod,
          ),
        ],
      )
    );
  }
}
