package com.growingio.demo.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.growingio.demo.data.SdkIntroItem
import com.growingio.demo.databinding.FragmentDashboardBinding
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.demo.ui.home.HomeSettingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : ViewBindingFragment<FragmentDashboardBinding>() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleStateBinding()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dashboardRv.layoutManager = LinearLayoutManager(requireContext())
        binding.dashboardRv.adapter = DashboardAdapter(object : DashboardAdapter.DashboardAdapterListener {
            override fun onLinkClick(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            override fun onSdkItemClick(view: View, item: SdkIntroItem) {
                // val extras = FragmentNavigatorExtras(view to "dashboard_item_${item.id}")
                findParentNavController()?.navigate(item.route)
            }
        })

        binding.accountIv.setOnClickListener {
            HomeSettingFragment().show(childFragmentManager, "project_setting")
        }

        viewModel.refreshData()
    }

    private fun lifecycleStateBinding() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.text.observe(viewLifecycleOwner) {
                    binding.versionName.text = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.sdkItemState.collect {
                when (it) {
                    is SdkItemState.SdkItemSet -> {
                        (binding.dashboardRv.adapter as DashboardAdapter).loadData(it.set)
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * fix: navigation with bottom navigation view, fragment view will be destroyed, but viewmodel will not be destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clear()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDashboardBinding {
        return FragmentDashboardBinding.inflate(layoutInflater, container, false)
    }
}
