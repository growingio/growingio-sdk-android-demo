package com.growingio.demo.ui.material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialBinding
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.demo.ui.widgets.GridDividerDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaterialFragment : ViewBindingFragment<FragmentMaterialBinding>() {

    private val viewModel: MaterialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleStateBinding()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.materialRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.materialRv.adapter = MaterialAdapter(object : MaterialAdapter.MaterialAdapterListener {
            override fun onMaterialItemClick(view: View, item: MaterialItem) {
                findParentNavController().navigate(item.route)
            }
        })
        binding.materialRv.addItemDecoration(
            GridDividerDecoration(
                resources.getDimensionPixelSize(R.dimen.material_divider_size),
                ContextCompat.getColor(requireContext(), R.color.material_grid_divider_color),
                2
            )
        )
        viewModel.refreshData()
    }

    private fun lifecycleStateBinding() {
        lifecycleScope.launch {
            viewModel.materialItemState.collect {
                when (it) {
                    is MaterialItemState.MaterialItemSet -> {
                        (binding.materialRv.adapter as MaterialAdapter).loadData(it.set)
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

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMaterialBinding {
        return FragmentMaterialBinding.inflate(layoutInflater, container, false)
    }

}
