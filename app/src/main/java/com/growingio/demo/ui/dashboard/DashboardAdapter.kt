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

package com.growingio.demo.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.growingio.demo.R
import com.growingio.demo.data.DashboardItem

/**
 * <p>
 *
 * @author cpacm 2023/4/19
 */
class DashboardAdapter(private val listener: DashboardAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sdkItems = arrayListOf<DashboardItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(list: List<DashboardItem>) {
        sdkItems.clear()
        sdkItems.add(DashboardItem(id = -1, "header"))// header
        sdkItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType < 0) {
            return HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_header, parent, false)
            )
        } else {
            return SdkItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_item, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return sdkItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return sdkItems[position].id
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SdkItemViewHolder) {
            holder.bind(sdkItems[position])
        }
    }

    inner class SdkItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sdkIcon = itemView.findViewById<ImageView>(R.id.sdkIcon)
        private val sdkTitle = itemView.findViewById<TextView>(R.id.sdkTitle)
        private val sdkDesc = itemView.findViewById<TextView>(R.id.sdkDesc)
        private val sdkJump = itemView.findViewById<Button>(R.id.sdkJump)

        init {
            sdkJump.setOnClickListener {
                listener.onSdkItemClick(itemView, sdkItems[adapterPosition])
            }
        }

        fun bind(item: DashboardItem) {
            if (item.icon != null) {
                sdkIcon.setImageResource(item.icon.icon)
            }
            sdkTitle.text = item.title
            sdkDesc.text = item.desc
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val docLinkButton = itemView.findViewById<Button>(R.id.docLink)

        private val sdkVersionBadge = itemView.findViewById<ImageView>(R.id.sdkVersionBadge)
        private val sdkPlatformBadge = itemView.findViewById<ImageView>(R.id.sdkPlatformBadge)
        private val sdkLicenseBadge = itemView.findViewById<ImageView>(R.id.sdkLicenseBadge)
        private val sdkCodecovBadge = itemView.findViewById<ImageView>(R.id.sdkCodecovBadge)

        init {
            val context = itemView.context
            docLinkButton.setOnClickListener {
                listener.onLinkClick(context.getString(R.string.dashboard_doc_url))
            }
            sdkVersionBadge.setOnClickListener {
                listener.onLinkClick(context.getString(R.string.dashboard_sdk_repo))
            }

            sdkVersionBadge.load(context.getString(R.string.dashboard_sdk_version_badge)) {
                placeholder(R.drawable.sdk_version)
            }
            sdkPlatformBadge.load(context.getString(R.string.dashboard_sdk_platform_badge))
            sdkLicenseBadge.load(context.getString(R.string.dashboard_sdk_license_badge))
            sdkCodecovBadge.load(context.getString(R.string.dashboard_sdk_codecov_badge))
        }
    }


    interface DashboardAdapterListener {
        fun onSdkItemClick(view: View, item: DashboardItem)
        fun onLinkClick(url: String)
    }
}