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

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem

/**
 * <p>
 *
 * @author cpacm 2023/4/19
 */
class MaterialAdapter(private val listener: MaterialAdapterListener) :
    RecyclerView.Adapter<MaterialAdapter.MaterialItemViewHolder>() {

    private val materialItems: MutableSet<MaterialItem> = mutableSetOf()

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(list: MutableSet<MaterialItem>) {
        materialItems.clear()
        materialItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialItemViewHolder {
        return MaterialItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_material_item, parent, false),
        )
    }

    override fun onBindViewHolder(holder: MaterialItemViewHolder, position: Int) {
        holder.bind(materialItems.elementAt(position))
    }

    override fun getItemCount(): Int {
        return materialItems.size
    }

    inner class MaterialItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val materialIcon = itemView.findViewById<ImageView>(R.id.materialIcon)
        private val materialTitle = itemView.findViewById<TextView>(R.id.materialTitle)

        init {
            itemView.rootView.setOnClickListener {
                listener.onMaterialItemClick(itemView, materialItems.elementAt(layoutPosition))
            }
        }

        fun bind(item: MaterialItem) {
            if (item.icon != null) {
                materialIcon.setImageResource(item.icon)
            }
            materialTitle.text = item.title
        }
    }

    interface MaterialAdapterListener {
        fun onMaterialItemClick(view: View, item: MaterialItem)
    }
}
