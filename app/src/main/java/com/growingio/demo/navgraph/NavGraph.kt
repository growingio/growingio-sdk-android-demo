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
    object Home : FragmentNav("home") //主页

    //home page,use extra nav controller
    object DashBoard : FragmentNav("dashboard")
    object UI : FragmentNav("ui")
    object Template : FragmentNav("template")

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
    object SdkEventFilterPage : PageNav(FragmentNav.DashBoard, "eventFilter")
    object SdkUserLoginPage : PageNav(FragmentNav.DashBoard, "userLogin")
    object SdkDataCollectPage : PageNav(FragmentNav.DashBoard, "dataCollect")
    object SdkLocationPage : PageNav(FragmentNav.DashBoard, "location")
    object SdkImpressionPage : PageNav(FragmentNav.DashBoard, "impression")
    object SdkEventTimerPage : PageNav(FragmentNav.DashBoard, "eventTimer")

    object ComponentHybridPage : PageNav(FragmentNav.DashBoard, "hybrid")

    object LinkArticlePage : PageNav(FragmentNav.Home, params = arrayListOf("noteId")) {
        fun toNoteUrl(noteId: Long): String {
            return route().replace("{${paramName()}}", noteId.toString())
        }
    }

}


