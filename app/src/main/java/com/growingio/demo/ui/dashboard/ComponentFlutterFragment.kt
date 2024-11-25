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

package com.growingio.demo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import com.growingio.android.flutter.FlutterLibraryGioModule
import com.growingio.code.annotation.SourceCode
import com.growingio.demo.R
import com.growingio.demo.data.SdkIcon
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentComponentWebserviceBinding
import com.growingio.demo.databinding.FragmentFlutterBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.PageFragment
import com.growingio.demo.ui.camera.BarScanner
import com.growingio.demo.util.MarkwonManager
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.net.URLEncoder
import javax.inject.Inject

/**
 * <p>
 *
 * @author cpacm 2023/4/20
 */
@AndroidEntryPoint
class ComponentFlutterFragment : PageFragment<FragmentFlutterBinding>() {

    @Inject
    lateinit var markwonManager: MarkwonManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFlutterBinding {
        return FragmentFlutterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(getString(R.string.component_flutter))

        markwonManager.renderMarkdown(pageBinding.content, initContent())

        loadAssetCode(this)

        // https://ads-uat.growingio.cn/k4budVa
        setDefaultLogFilter("level:debug flutter")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SourceCode
    fun initFlutterModule(){
        // Flutter 模块可以在原生端初始化时直接注册，或者在 Flutter 端初始化时会自动加入
        /**
         * GrowingTracker.startWithConfiguration(this,
         *            TrackConfiguration("accountId", "urlScheme")
         *            //...
         *            .addPreloadComponent(FlutterLibraryGioModule()))
         */
    }

    private fun initContent(): String {
        return """
            ## Growingio Flutter SDK 插件集成
            ### 添加依赖

            在 Flutter 项目的`pubspec.yaml`文件中 dependencies 里面添加 `growingio_flutter_plugin` 依赖。

            ```c
            dependencies:
              growingio_flutter_plugin: '^4.2.0'
            ```
            
            然后执行 `flutter pub get` 指令安装插件。
            
            ### Flutter 插件初始化

            GrowingIO Flutter SDK 需要在 Flutter 端初始化 SDK。

            > 若您的 Flutter 应用是混编应用，在进入 Flutter 页面之前已经在原生进行了原生 SDK 的初始化，您还应当在 Flutter 端再进行一次 Flutter SDK 的初始化，以使得插件桥接生效。<br/>
            > 注意：原生端或 Flutter 端，以先初始化的配置项为主。<br/>
            
            > 如果是 Android 平台且需要无埋点功能支持，还应当在 Android 配置中添加 [GrowingIO Gradle 插件](/docs/android/AGP7) 才能使原生无埋点生效。 
            
            #### Flutter 初始化

            在 Flutter 端进行初始化，推荐将 SDK 的初始化代码放入 `main.dart` 的 `main` 中，代码示例如下：

            ```dart

            void main() async {
              /// 代码中的 <ProjectId>、<DataSourceId>、<UrlScheme>请自行替换为自己项目的对应值。
              var option = AutotrackerConfiguration("<ProjectId>", "<DataSourceId>", "<UrlScheme>");
              option.dataCollectServerHost = "https://napi.growingio.com";
              option.debugEnabled = false;
              option.dataCollectionEnable = true;
              option.androidConfig = AndroidConfiguration(
                  channel: "应用宝", androidIdEnabled: true, imeiEnabled: false, requireAppProcessesEnabled: false);
              
              /// 添加相应的模块
              option.addGioComponent(EncoderLibraryGioModule());

              /// 设置完配置后，调用该接口进行初始化
              GrowingAutotracker.startWithConfiguration(option);

              runApp(MyApp());
            }
            ```
            
            ## 初始化配置说明

            在 Flutter 初始化中会传入各式的参数，`AutotrackerConfiguration` 参数配置如下表所示：

            | 配置项                   | 参数类型  | 是否必填 | 默认值  | 说明  |
            | :-----------------------| :------: | :------: | :------: | :------ | 
            | projectId             | String           |    是    | null  | 项目ID，每个应用对应唯一值 | 
            | dataSourceId          | String           |    是    | null  | 应用的DataSourceId，唯一值        |
            | urlScheme             | String           |    是    | null  | 应用特有的URLScheme，用于外部应用拉起应用，如圈选   |
            | dataCollectionServerHost  | String       |    否    | null  | 服务端部署后的 ServerHost     |
            | debugEnabled              | bool         |    否    | false | 调试模式，会打印SDK log，抛出错误异常，在线上环境请关闭 |
            | cellularDataLimit         | int          |    否    | 10    | 每天发送数据的流量限制，单位MB        |
            | dataUploadInterval        | int          |    否    | 15    | 数据发送的间隔，单位秒                |
            | sessionInterval           | int          |    否    | 30   | 会话后台留存时长，单位秒  |
            | dataCollectionEnabled     | bool         |    否    | true  | 是否采集数据               |
            | requestTimeout            | int          |    否    | 30  | 设置数据上报请求的超时时间，单位秒               |
            | dataValidityPeriod        | int          |    否    | 7  | 设置为上报数据在数据库的缓存时间，单位天               |
            | idMappingEnabled          | bool         |    否    | false  | 是否开启多用户身份上报               |
            | autotrackAllRoutePage     | bool         |    否    | true  | 设置是否发送 Route 页面上的Page事件              |
            | autotrackEnabled          | bool         |    否    | true  | 设置原生端是否打开无埋点功能               |
            | autotrackAllNativePage    | bool         |    否    | false  | 设置原生端是否自动发送Page事件            |
            | modules  | `Set<LibraryGioModule>`       |    否    | empty | 模块集成，具体请阅读下方的模块说明 |
            | androidConfig  | `AndroidConfiguration`  |    否    | null | 用于配置Android设备上特有的一些属性 |
            | iosConfig      | `IosConfiguration`      |    否    | null | 用于配置iOS设备上特有的一些属性 |

            `AndroidConfiguration` 参数特有配置如下表所示
            
            | 配置项                   | 参数类型  | 是否必填 | 默认值  | 说明  |
            | :-----------------------| :------: | :------: | :------: | :------ |
            | channel                   | String       |    否    | null  | Android 应用的分发渠道     |
            | androidIdEnabled          | bool         |    否    | false  | 是否允许在 Android 设备上采集 AndroidId           |
            | imeiEnabled               | bool         |    否    | false  | 是否允许在 Android 设备上采集 imei          |
            | requireAppProcessesEnabled   | bool      |    否    | false  | 是否允许在 Android 设备上获取应用进程名称          |

            `IosConfiguration` 目前暂无特有配置。
            
            ### urlScheme 配置说明

            在使用 GrowingIO SDK 的 Mobile Debugger 和圈选功能时，用于外部浏览器通过扫描二维码来拉起应用。
            
            ## Flutter 无埋点
            新版本 Flutter 无埋点的功能不再通过修改 Flutter 源代码的方式进行，而是通过继承对应的功能来实现相应的功能。在无埋点中需要能够自动识别 Page 页面发送页面事件和获取点击事件发送点击事件，基于上述规则，GrowingIO Flutter Plugin 针对无埋点功能实现了 [基于 Route 的页面事件](#基于-route-的页面事件) 和 [基于页面的点击事件](#基于页面的无埋点点击事件)
            
            ### 基于 Route 的页面事件

            #### 设置页面监听器
            为了能够识别 Flutter 中页面的进入与退出，我们需要对 Flutter 系统的 `navigator` 进行监听。一般是在 `MaterialApp` 下的`navigatorObservers`添加。

            ```dart

            import 'package:growingio_flutter_plugin/growingio_flutter_plugin.dart';

            MaterialApp(
              title: 'Flutter Project',

              // 添加 Growingio 路由监听器 GrowingNavigatorObserver
              navigatorObservers: [GrowingNavigatorObserver()],

              builder: (context, child) {}
            )
            ```
            #### 设置要监听的页面
            目前 GrowingIO Flutter SDK 4.0 只对已经设置页面属性的组件发送页面事件，未设置的页面既不会发送页面事件也不会发送无埋点点击事件。

            如何设置监听页面，可以从下列方式中选择合适的方法：
            1. 扩展Route: 使用 GrowingIO Flutter SDK 提供的类扩展 Flutter 中的 Route，将其标识为要采集的页面，可以参考[示例一](#示例一route)，[示例二](#示例二route-name)，[示例三](#示例三自定义route);
            2. 使用代码将 Widget 标识为要采集的页面，参考[示例五](#示例五-非-route-页面)
            3. 将Dialog,BottomSheet等弹出框标识为页面，参考[示例四](#示例四面对dialogbottomsheet等弹出框)

            ### 基于页面的无埋点点击事件

            为了能够全局获取到点击事件的产生，需要为整个 App 添加事件监听器，如下所示：

            ```dart
            import 'package:growingio_flutter_plugin/growingio_flutter_plugin.dart';

            void main() async {
              runApp(const GrowingWidget(child:const MyApp()));
            }
            ```

            请注意以下几个问题：
            1. 当前 GrowingIO Flutter SDK 只监听 onTap 事件，未对滑动的动作进行监听，所以比如像 `Slider`,`ProgressBar`之类控件的滑动埋点事件无法自动生成，请客户手动进行埋点；
            2. 为了保证点击事件捕获的准确性，只会在已经声明为页面（即可发送页面事件）的界面做无埋点点击事件捕获，非页面的点击无法生成无埋点点击事件。


            ### 关于 Flutter Route 无埋点页面的说明

            为了获取Route中的页面信息，GrowingIO Flutter SDK扩展了Route的实现，方便SDK计算页面的属性和路径，所以需要在集成时对Route做额外处理，下面列举了几种比较常见且推荐的Route实现方式。

            #### 示例一：Route
            对应方法为 `Navigator.push(context, route)`。
            该示例常见做法是为每一个界面 Widget 创建一个 Route 对象，跳转时直接传入 Route 值即可。 

            ```dart
            class AppNavigator {
              static Route page1 = MaterialPageRoute(builder: (context) => Page1Screen(), settings: const RouteSettings(name: "page1"));
            }
            ```

            在SDK中需要将 `MaterialPageRoute` 替换为 `GrowingMaterialPageRoute`，来帮助 GrowingIO Flutter SDK获取到页面信息，如下所示

            ```dart
            import 'package:growingio_flutter_plugin/growingio_flutter_plugin.dart';

            class AppNavigator {
              static Route page1 = GrowingMaterialPageRoute(builder: (context) => Page1Screen(), settings: const RouteSettings(name: "page1"));
              static Route page2 = GrowingMaterialPageRoute(builder: (context) => Page2Screen(), settings: const RouteSettings(name: "page2"));
              // ...
            }
            ``` 

            同样可做替换的还有 `CupertinoPageRoute` => `GrowingCupertinoPageRoute`.

            最后路由跳转方法调用依旧保持不变。
            ```dart
            Navigator.of(context).push(AppNavigator.page1);
            ```

            > 注意，在声明 Route 的时候，请传入 `RouteSettings` 以方便我们定义页面的名称并作为页面事件的 `alias`。

            #### 示例二：Route Name
            对应方法为 `Navigator.pushNamed(context, '/page');`
            该示例通常会为每一个路由定义一个路径名称，然后跳转时直接调用名称即可。

            同样的可以使用 `GrowingMaterialPageRoute` 替换原来的 `MaterialPageRoute` 或者 `GrowingCupertinoPageRoute` 替换原来的 `CupertinoPageRoute`。

            举例说明
            ```dart
            import 'package:growingio_flutter_plugin/growingio_flutter_plugin.dart';

            class AppNavigator {
              static const String home = '/home';
              static const String page = '/home/page';
              static const String category = '/category';

              static Route onGenerateRoute(RouteSettings settings) {
                switch (settings.name) {
                  case category:
                    return GrowingMaterialPageRoute(
                        builder: (context) => CategoryScreen(),
                        settings: const RouteSettings(name: "category"));
                  case page:
                    return GrowingMaterialPageRoute(
                        builder: (context) => PageScreen(), settings: const RouteSettings(name: "page"));
                  default:
                    return GrowingMaterialPageRoute(
                        builder: (context) => HomeScreen(), settings: const RouteSettings(name: "home"));
                }
              }
            }
            ```

            RouteName的注册以及调用保持不变。

            ```dart
            /// 在 materialApp 中注册
            MaterialApp(
              title: 'Flutter',
              navigatorObservers: [GrowingioNavigatorObserver()],

              /// 注册 Route Name 声明
              onGenerateRoute: AppNavigator.onGenerateRoute,

              builder: (context, child) {}
            )


            /// 调用
            Navigator.pushNamed(context, AppNavigator.home);
            ```

            > 注意，在声明 Route 的时候，请传入 `RouteSettings` 以方便我们定义页面的名称并作为页面事件的 `alias`。

            ### 示例三：自定义Route
            若客户在应用中已经自定义了 Route 并扩展了其功能，无法直接替换，这时候可以使用 `GrowingPageRouteMixin` 类进行扩展。

            比如我们自定义一个 `CustomRoute` 类来增加 Route 的功能，其实现如下所示：
            ```dart
            import 'package:growingio_flutter_plugin/growingio_flutter_plugin.dart';

            class CustomRoute extends PageRouteBuilder with GrowingPageRouteMixin {
              MaterialPageRoute? route;
              CustomRoute({required this.page, settings})
                  : super(
                      settings: settings,
                      pageBuilder: (_, __, ___) => page,
                      transitionsBuilder: (_, animation, __, child) => FadeTransition(
                        opacity: animation,
                        child: child,
                      ),
                    );

              final Widget page;
            }

            ```

            > 请注意，目前 `GrowingPageRouteMixin` 只能对继承了 `PageRoute`类的自定义类进行扩展。

            #### 示例四：面对Dialog,BottomSheet等弹出框
            在 GrowingIO Flutter SDK 中也将Dialog和BottomSheet视为一个页面，通常我们打开一个弹出框会使用 `showDialog` 或 `showModalBottomSheet` 之类的快速方法。
            为了更好识别这些页面我们需要传入一个 `GrowingWidgetBuilder` 作为控件的最外层将该 Dialog或者 BottomSheet 视为一个页面。

            ```dart
            /// 比如显示一个 BottomSheet。
            showModalBottomSheet<void>(
              context: context,
              builder: (context) {
                return GrowingWidgetBuilder(
                    route: "bottomsheet",
                    child: <Your Widget>
                  );
              },
            );
            ```

            > 目前 GrowingIO Flutter SDK 不支持 DropDown，MenuAnchor下的弹窗，这部分的无埋点事件需要客户自行调用SDK `GrowingAutotracker.getContext().trackCustomEvent(eventName: "eventName");` 接口来进行手动埋点。

            #### 示例五: 非 Route 页面

            若是客户希望将一个 Widget 标记为页面，则可以通过 mixin 类 `GrowingPageStateMixin` 或者 `GrowingPageStatelessMixin` 来进行扩展实现。

            1. 在 `StatefulWidget` 中，可以将其 State 声明为 Page页面，如下：

            ```dart
            class _HomeScreenState extends State<HomeScreen> with GrowingPageStateMixin {
              @override
              String get alias => "HomeScreen";

              @override
              Map<String, dynamic>? get attributes => {};
            }
            ```

            2. 在 `StatelessWidget` 中，直接将自己声明为 Page 页面，如下：

            ```dart
            class SplashScreen extends StatelessWidget with GrowingPageStatelessMixin {
              @override
              String get alias => "SplashScreen";

              @override
              Map<String, dynamic>? get attributes => {};
            }
            ```

            alias 对应页面的名称，attributes为页面属性。

            > 若一个 Widget 既是 Route 界面又同时扩展了 `GrowingPageStateMixin` 或者 `GrowingPageStatelessMixin` ，该界面的 **alias** 将优先取扩展下返回的 alias 值。
            > 同样的，如果想要对一个页面添加页面属性，也可以通过该扩展实现。
        """.trimIndent()
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideSdkItem(): SdkIntroItem {
            return SdkIntroItem(
                id = 27,
                icon = SdkIcon.Component,
                title = "Flutter",
                desc = "使用 Flutter 插件，可以完成代码埋点的统计和上报。",
                route = PageNav.ComponentFlutterPage.route(),
                fragmentClass = ComponentFlutterFragment::class,
            )
        }
    }
}
