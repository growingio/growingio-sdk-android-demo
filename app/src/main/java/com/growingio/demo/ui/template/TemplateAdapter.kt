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

package com.growingio.demo.ui.template

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.growingio.demo.R
import com.growingio.demo.data.TemplateItem

/**
 * <p>
 *
 * @author cpacm 2023/4/19
 */
class TemplateAdapter(private val listener: TemplateAdapterListener) :
    RecyclerView.Adapter<TemplateAdapter.TemplateItemViewHolder>() {

    private val items: MutableSet<TemplateItem> = mutableSetOf()

    fun loadItems(list: List<TemplateItem>) {
        notifyItemRangeRemoved(0, itemCount)
        items.clear()
        items.addAll(list)
        notifyItemRangeInserted(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateItemViewHolder {
        return TemplateItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_template_item, parent, false),
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TemplateItemViewHolder, position: Int) {
        holder.bind(items.elementAt(position))
    }

    inner class TemplateItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tIcon = itemView.findViewById<ImageView>(R.id.templateIcon)
        private val tTitle = itemView.findViewById<TextView>(R.id.templateTitle)
        private val tDesc = itemView.findViewById<TextView>(R.id.templateDesc)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(itemView, items.elementAt(layoutPosition))
            }
        }

        fun bind(item: TemplateItem) {
            if (item.icon != null) {
                tIcon.setImageResource(item.icon)
            }
            tTitle.text = item.title
            tDesc.text = item.desc
        }
    }

    interface TemplateAdapterListener {
        fun onItemClick(view: View, item: TemplateItem)
    }
}
