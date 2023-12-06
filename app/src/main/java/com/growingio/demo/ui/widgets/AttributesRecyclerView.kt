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

package com.growingio.demo.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.growingio.demo.R

/**
 * <p>
 *     用于创建添加属性列表
 * @author cpacm 2023/7/21
 */
class AttributesRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    private val buildSize = arrayListOf<Int>()

    private val attrAdapter by lazy { AttributesAdapter(context, buildSize) }

    fun addAttribute() {
        if (buildSize.size > MAX_COUNT) return
        buildSize.add(buildSize.size)
        attrAdapter.notifyItemInserted(buildSize.size - 1)
        smoothScrollToPosition(buildSize.size)
    }

    fun subAttribute() {
        if (buildSize.size <= 0) return
        buildSize.removeLast()
        attrAdapter.notifyItemRemoved(buildSize.size)
    }

    fun attributeSize(): Int {
        return buildSize.size
    }

    fun validAttributes(): Boolean {
        buildSize.indices.forEach { index ->
            val viewHolder = findViewHolderForAdapterPosition(index) as? AddAttrViewHolder ?: return@forEach
            if (viewHolder.keyEt.text.isNullOrEmpty()) {
                return false
            }
            if (viewHolder.valueEt.text.isNullOrEmpty()) {
                return false
            }
        }
        return true
    }

    fun getAttributes(): Map<String, String> {
        val builder = hashMapOf<String, String>()
        buildSize.indices.forEach { index ->
            val viewHolder = findViewHolderForAdapterPosition(index) as AddAttrViewHolder
            val key = viewHolder.keyEt.text.toString()
            val value = viewHolder.valueEt.text.toString()
            builder[key] = value
        }
        return builder
    }

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = attrAdapter
        buildSize.add(0)
    }

    companion object {
        const val MAX_COUNT = 8
    }

    class AttributesAdapter(private val context: Context, private val list: List<Int>) :
        Adapter<AddAttrViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAttrViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.recycler_attributes_item, parent, false)
            return AddAttrViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: AddAttrViewHolder, position: Int) {
            holder.keyEt.setText("key$position")
            holder.valueEt.setText("value$position")
        }
    }

    class AddAttrViewHolder(itemView: View) : ViewHolder(itemView) {
        val keyEt: EditText = itemView.findViewById(R.id.key)
        val valueEt: EditText = itemView.findViewById(R.id.value)
    }
}
