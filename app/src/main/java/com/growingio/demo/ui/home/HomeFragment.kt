package com.growingio.demo.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.growingio.demo.R
import com.growingio.demo.databinding.FragmentHomeBinding
import com.growingio.demo.navgraph.FragmentNav
import com.growingio.demo.navgraph.NavGraph.HOME_GRAPH
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.demo.ui.dashboard.DashboardFragment
import com.growingio.demo.ui.material.MaterialFragment
import com.growingio.demo.ui.template.TemplateFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostFragment.navController.apply {
            graph = createGraph(FragmentNav.DashBoard.route, HOME_GRAPH) {
                fragment<DashboardFragment>(FragmentNav.DashBoard.route) {
                }
                fragment<MaterialFragment>(FragmentNav.UI.route) {
                }
                fragment<TemplateFragment>(FragmentNav.Template.route) {
                }
            }
        }

        initNavigation(binding.navBar)
        setupWithNavController(binding.navBar, navHostFragment.navController)
    }

    private fun initNavigation(bottomNav: BottomNavigationView) {
        bottomNav.menu.apply {
            add(Menu.NONE, R.id.dashboard, 0, R.string.navi_dashboard_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_dashboard)
            }
            add(Menu.NONE, R.id.ui, 1, R.string.navi_material_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_ui)
            }
            add(Menu.NONE, R.id.template, 2, R.string.navi_template_title).apply {
                icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_template)
            }
        }

    }

    private fun setupGiokit(item: MenuItem) {
        if (item.itemId == R.id.ui) {
            //GioKit.attach(requireActivity())
        } else {
            //GioKit.detach(requireActivity())
        }
    }

    override fun onDetach() {
        super.onDetach()
        //GioKit.detach(requireActivity())
    }

    private fun setupWithNavController(navBar: BottomNavigationView, navController: NavController) {
        navBar.setOnItemSelectedListener { item ->
            setupGiokit(item)
            onNavDestinationSelected(item, navController)
        }

        val weakReference = WeakReference(navBar)
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    view.menu.forEach { item ->
                        val route = HOME_ROUTE_MAPPING.find { pair ->
                            item.itemId == pair.first
                        }?.second
                        if (destination.route == route) {
                            item.isChecked = true
                        }
                    }
                }
            })
    }

    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        val route = HOME_ROUTE_MAPPING.find { pair ->
            item.itemId == pair.first
        }?.second
        if (route.isNullOrBlank()) return false
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().route,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        return try {
            navController.navigate(route, options)
            navController.currentDestination?.route == route
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater, container, false)
    }

    companion object {
        val HOME_ROUTE_MAPPING = listOf(
            R.id.dashboard to FragmentNav.DashBoard.route,
            R.id.ui to FragmentNav.UI.route,
            R.id.template to FragmentNav.Template.route,
        )
    }

}