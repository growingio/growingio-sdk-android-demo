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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialRecyclerBinding
import com.growingio.demo.navgraph.PageNav
import com.growingio.demo.ui.base.ViewBindingFragment
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * <p>
 *
 * @author cpacm 2023/5/11
 */
class RecyclerViewFragment : ViewBindingFragment<FragmentMaterialRecyclerBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMaterialRecyclerBinding {
        return FragmentMaterialRecyclerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.adapter = RecyclerSampleAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 1,
                icon = R.drawable.ic_lists,
                title = "RecyclerView",
                route = PageNav.MaterialRecyclerViewPage.route(),
                fragmentClass = RecyclerViewFragment::class
            )
        }
    }
}

class RecyclerSampleAdapter : RecyclerView.Adapter<RecyclerSampleAdapter.SampleViewHolder>() {

    private val sampleList = mutableListOf<SampleItem>()

    init {
        for (i in 0..100) {
            sampleList.add(SampleItem("title-$i", "desc in position-$i"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_sample_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return sampleList.size
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        val item = sampleList[position]
        holder.titleTv.text = item.title
        holder.descTv.text = item.desc
        holder.button.setOnClickListener {
            // nothing
        }
    }

    data class SampleItem(val title: String, val desc: String)

    class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv = itemView.findViewById<TextView>(R.id.title)
        val descTv = itemView.findViewById<TextView>(R.id.desc)
        val button = itemView.findViewById<Button>(R.id.action)
    }
}

