package com.growingio.demo.navgraph

/**
 * <p>
 *
 * @author cpacm 2022/10/26
 */
object NavGraph {
    const val MAIN_GRAPH = "main"
    const val HOME_GRAPH = "home"
}

internal sealed class FragmentNav(val route: String) {
    object Home : FragmentNav("home") // 主页

    // home page,use extra nav controller
    object DashBoard : FragmentNav("dashboard")
    object UI : FragmentNav("ui")
    object Template : FragmentNav("template")

    object Widgets : FragmentNav("widgets")
}

internal sealed class PageNav(val root: FragmentNav, val path: String? = null, val params: List<String>? = null) {
    fun route(): String {
        val route = StringBuilder(root.route)
        path?.let { route.append("/").append(path) }
        params?.forEach { param ->
            route.append("/").append("{").append(param).append("}")
        }
        return route.toString()
    }

    fun paramName(index: Int = 0): String {
        if (params != null && params.size > index) {
            return params[index]
        }
        assert(true) {
            "make sure navigation params index is correct"
        }
        return ""
    }

    object SdkInitPage : PageNav(FragmentNav.DashBoard, "init")
    object SdkAutoTrackOptionsPage : PageNav(FragmentNav.DashBoard, "autotrackOption")
    object SdkEventFilterPage : PageNav(FragmentNav.DashBoard, "eventFilter")
    object SdkUserLoginPage : PageNav(FragmentNav.DashBoard, "userLogin")
    object SdkDataCollectPage : PageNav(FragmentNav.DashBoard, "dataCollect")
    object SdkLocationPage : PageNav(FragmentNav.DashBoard, "location")
    object SdkImpressionPage : PageNav(FragmentNav.DashBoard, "impression")
    object SdkUniqueTagPage : PageNav(FragmentNav.DashBoard, "uniqueTag")
    object SdkEventTimerPage : PageNav(FragmentNav.DashBoard, "eventTimer")
    object SdkAutotrackerPage : PageNav(FragmentNav.DashBoard, "autoPage")
    object SdkGeneralPopsPage : PageNav(FragmentNav.DashBoard, "generalPops")
    object SdkApiTestPage : PageNav(FragmentNav.DashBoard, "otherApi")

    object ComponentHybridPage : PageNav(FragmentNav.DashBoard, "hybrid")
    object ComponentEncoderPage : PageNav(FragmentNav.DashBoard, "encoder")
    object ComponentProtobufPage : PageNav(FragmentNav.DashBoard, "protobuf")
    object ComponentOaidPage : PageNav(FragmentNav.DashBoard, "oaid")
    object ComponentAdvertPage : PageNav(FragmentNav.DashBoard, "advert")
    object ComponentABTestPage : PageNav(FragmentNav.DashBoard, "abTest")
    object ComponentWebServicePage : PageNav(FragmentNav.DashBoard, "webService")

    object MaterialRecyclerViewPage : PageNav(FragmentNav.UI, "recyclerview")
    object MaterialBottomAppBarPage : PageNav(FragmentNav.UI, "bottomappbar")
    object MaterialButtonPage : PageNav(FragmentNav.UI, "button")
    object MaterialCheckBoxPage : PageNav(FragmentNav.UI, "checkbox")
    object MaterialChipsPage : PageNav(FragmentNav.UI, "chips")
    object MaterialDialogPage : PageNav(FragmentNav.UI, "dialog")
    object MaterialMenuPage : PageNav(FragmentNav.UI, "menu")
    object MaterialNavigationPage : PageNav(FragmentNav.UI, "navigation")
    object MaterialSliderPage : PageNav(FragmentNav.UI, "slider")
    object MaterialSwitchPage : PageNav(FragmentNav.UI, "switch")
    object MaterialTextFieldPage : PageNav(FragmentNav.UI, "textfield")
    object MaterialWebViewPage : PageNav(FragmentNav.UI, "webview")
    object MaterialExpandablePage : PageNav(FragmentNav.UI, "expandable")

    object WidgetAndroidH5Page : PageNav(FragmentNav.Widgets, "androidH5", params = arrayListOf("url")) {
        fun toUrl(url: String): String {
            return route().replace("{${paramName()}}", url)
        }
    }

    object WidgetAndroidX5Page : PageNav(FragmentNav.Widgets, "androidX5", params = arrayListOf("url")) {
        fun toUrl(url: String): String {
            return route().replace("{${paramName()}}", url)
        }
    }
}
