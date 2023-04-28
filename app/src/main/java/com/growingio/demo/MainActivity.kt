package com.growingio.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import androidx.navigation.navOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.navgraph.FragmentNav
import com.growingio.demo.navgraph.NavGraph.MAIN_GRAPH
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.dashboard.SdkEventFilterFragment
import com.growingio.demo.ui.dashboard.SdkInitFragment
import com.growingio.demo.ui.dashboard.SdkUserLoginFragment
import com.growingio.demo.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)

        settingsDataStore.data.map {
            if (it.agreePolicy) {
                initAppAfterAgreePolicy()
            } else {
                showPolicyDialog()
            }
        }.launchIn(lifecycleScope)
    }

    private fun initAppAfterAgreePolicy() {
        GrowingAutotracker.get().setDataCollectionEnabled(true)
        openHomeFragment()
    }

    private fun openHomeFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            graph = createGraph(FragmentNav.Home.route, MAIN_GRAPH) {
                fragment<HomeFragment>(FragmentNav.Home.route) {
                }
                fragment<SdkInitFragment>(PageNav.SdkInitPage.route()) {
                }
                fragment<SdkEventFilterFragment>(PageNav.SdkEventFilterPage.route()) {
                }
                fragment<SdkUserLoginFragment>(PageNav.SdkUserLoginPage.route()) {
                }

            }
        }
    }

    private fun showPolicyDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.PolicyMaterial3Dialog).setTitle(R.string.dialog_policy_title)
                .setView(R.layout.dialog_policy_view)
                .setPositiveButton(R.string.dialog_policy_agree) { _, _ ->
                    lifecycleScope.launch {
                        settingsDataStore.updateData {
                            it.toBuilder().setAgreePolicy(true).build()
                        }
                        initAppAfterAgreePolicy()
                    }
                }
                .setNegativeButton(R.string.dialog_policy_disagree) { _, _ ->
                    finish()
                }
                .create()

        dialog.show()

        dialog.findViewById<TextView>(R.id.policyLink)?.setOnClickListener {
            //open policy link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dialog_policy_link)))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
