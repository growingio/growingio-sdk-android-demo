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
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.growingio.demo.R
import com.growingio.demo.data.SdkIntroItem

/**
 * <p>
 *
 * @author cpacm 2023/4/19
 */
class DashboardAdapter(private val listener: DashboardAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sdkItems: MutableSet<SdkIntroItem> = mutableSetOf()

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(list: MutableSet<SdkIntroItem>) {
        sdkItems.clear()
        sdkItems.add(SdkIntroItem(-1, route = "header", fragmentClass = DashboardFragment::class)) // header
        sdkItems.add(SdkIntroItem(0, route = "初始化", fragmentClass = DashboardFragment::class))
        list.forEach {
            if (it.id == 10) {
                sdkItems.add(SdkIntroItem(0, route = "采集API", fragmentClass = DashboardFragment::class))
            }
            if (it.id == 20) {
                sdkItems.add(SdkIntroItem(0, route = "功能模块", fragmentClass = DashboardFragment::class))
            }
            sdkItems.add(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType < 0) {
            return HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_header, parent, false),
            )
        } else if (viewType == 0) {
            return DividerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_divider, parent, false),
            )
        } else {
            return SdkItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_item, parent, false),
            )
        }
    }

    override fun getItemCount(): Int {
        return sdkItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return sdkItems.elementAt(position).id
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SdkItemViewHolder) {
            holder.bind(sdkItems.elementAt(position))
        } else if (holder is DividerViewHolder) {
            holder.setDividerTitle(sdkItems.elementAt(position).route)
        }
    }

    inner class SdkItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sdkIcon = itemView.findViewById<ImageView>(R.id.sdkIcon)
        private val sdkTitle = itemView.findViewById<TextView>(R.id.sdkTitle)
        private val sdkDesc = itemView.findViewById<TextView>(R.id.sdkDesc)
        private val sdkJump = itemView.findViewById<Button>(R.id.sdkJump)

        init {
            sdkJump.setOnClickListener {
                listener.onSdkItemClick(itemView, sdkItems.elementAt(layoutPosition))
            }
        }

        fun bind(item: SdkIntroItem) {
            if (item.icon != null) {
                sdkIcon.setImageResource(item.icon.icon)
            }
            sdkTitle.text = item.title
            sdkDesc.text = item.desc
        }
    }

    inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val dividerTv = itemView.findViewById<TextView>(R.id.dividerTitle)

        fun setDividerTitle(title: String) {
            dividerTv.text = title
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val docLinkButton = itemView.findViewById<Button>(R.id.docLink)
        private val badgeList = itemView.findViewById<RecyclerView>(R.id.badgeList)

        init {
            val context = itemView.context
            docLinkButton.setOnClickListener {
                listener.onLinkClick(context.getString(R.string.dashboard_doc_url))
            }

            badgeList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            badgeList.adapter = BadgeAdapter(context) {
                listener.onLinkClick(it)
            }
        }
    }

    interface DashboardAdapterListener {
        fun onSdkItemClick(view: View, item: SdkIntroItem)
        fun onLinkClick(url: String)
    }

    class BadgeAdapter(val context: Context, private val linkCallback: (String) -> Unit) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

        private val badges = arrayOf(
            R.string.dashboard_sdk_version_badge,
            R.string.dashboard_sdk_platform_badge,
            R.string.dashboard_sdk_license_badge,
            R.string.dashboard_sdk_codecov_badge,
        )

        class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val badge = itemView.findViewById<ImageView>(R.id.badge)
        }

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BadgeViewHolder {
            return BadgeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_header_item, parent, false),
            )
        }

        override fun getItemCount(): Int {
            return badges.size
        }

        override fun onBindViewHolder(badgeHolder: BadgeViewHolder, p1: Int) {
            badgeHolder.badge.load(context.getString(badges[p1]))
            badgeHolder.badge.setOnClickListener {
                linkCallback(context.getString(R.string.dashboard_sdk_repo))
            }
        }
    }
}
