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

package com.growingio.demo.ui.material

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialRecyclerHorBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.ViewBindingFragment
import com.growingio.demo.ui.material.ViewPager2Fragment.Companion.PAGE_ARRAY
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/5/11
 */
class RecyclerViewHorFragment : ViewBindingFragment<FragmentMaterialRecyclerHorBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMaterialRecyclerHorBinding {
        return FragmentMaterialRecyclerHorBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.recycler.adapter = RecyclerHorSampleAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recycler)

        binding.viewPager.adapter = ViewPagerSampleAdapter(requireContext(), childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 15,
                icon = R.drawable.ic_list_hor,
                title = "ViewSlider",
                route = PageNav.MaterialRecyclerViewHorPage.route(),
                fragmentClass = RecyclerViewHorFragment::class,
            )
        }
    }
}

class ViewPagerSampleAdapter(val context:Context,fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val resId = PAGE_ARRAY[position]
        val content = context.getString(resId)
        return TestFragment.newInstance(content, position)
    }

    override fun getCount(): Int {
        return PAGE_ARRAY.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val resId = PAGE_ARRAY[position]
        return context.getString(resId)
    }


}

class RecyclerHorSampleAdapter : RecyclerView.Adapter<RecyclerHorSampleAdapter.SliderViewHolder>() {
    val picList = arrayOf(
        R.drawable.smoothies,
        R.drawable.vegan,
        R.drawable.nougat,
        R.drawable.mango,
        R.drawable.kiwi,
        R.drawable.jelly_bean,
        R.drawable.honeycomb,
        R.drawable.froyo,
        R.drawable.donut,
        R.drawable.cupcake,
        R.drawable.chips,
        R.drawable.apple_sauce,
        R.drawable.apple_chips,
        R.drawable.almonds,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_viewslider_item, parent, false),
        )
    }

    override fun getItemCount(): Int {
        return picList.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = picList[position]
        holder.sliderView.setImageResource(item)
        holder.sliderView.setOnClickListener {}
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderView = itemView.findViewById<ImageView>(R.id.sliderView)
    }
}
