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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.growingio.demo.R
import com.growingio.demo.data.MaterialItem
import com.growingio.demo.databinding.FragmentMaterialExpandedListviewBinding
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
class ExpandableListViewFragment : ViewBindingFragment<FragmentMaterialExpandedListviewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMaterialExpandedListviewBinding {
        return FragmentMaterialExpandedListviewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.expandableListView.setAdapter(ExpandableSampleAdapter())

        binding.expandableListView.setOnGroupClickListener { p0, p1, p2, p3 -> false }
        binding.expandableListView.setOnChildClickListener { expandableListView, view, i, i2, l ->
            false
        }
    }

    var groupList: ArrayList<String> = arrayListOf()
    var itemSet: ArrayList<ArrayList<String>> = arrayListOf()

    private fun initData() {
        groupList = ArrayList()
        groupList.add("我的家人")
        groupList.add("我的朋友")
        groupList.add("黑名单")
        itemSet = ArrayList()
        val itemList1 = ArrayList<String>()
        itemList1.add("大妹")
        itemList1.add("二妹")
        itemList1.add("三妹")
        val itemList2 = ArrayList<String>()
        itemList2.add("大美")
        itemList2.add("二美")
        itemList2.add("三美")
        val itemList3 = ArrayList<String>()
        itemList3.add("狗蛋")
        itemList3.add("二丫")
        itemSet.add(itemList1)
        itemSet.add(itemList2)
        itemSet.add(itemList3)
    }

    @dagger.Module
    @InstallIn(SingletonComponent::class)
    object Module {
        @IntoSet
        @Provides
        fun provideMaterialItem(): MaterialItem {
            return MaterialItem(
                sort = 13,
                icon = R.drawable.ic_lists,
                title = "ExpandableListView",
                route = PageNav.MaterialExpandablePage.route(),
                fragmentClass = ExpandableListViewFragment::class,
            )
        }
    }

    inner class ExpandableSampleAdapter : BaseExpandableListAdapter() {

        val lif = requireActivity().layoutInflater

        override fun getGroupCount(): Int {
            return groupList.size
        }

        override fun getChildrenCount(p0: Int): Int {
            return itemSet[p0].size
        }

        override fun getGroup(p0: Int): Any {
            return groupList[p0]
        }

        override fun getChild(p0: Int, p1: Int): Any {
            return itemSet[p0][p1]
        }

        override fun getGroupId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getChildId(p0: Int, p1: Int): Long {
            return p1.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        @SuppressLint("SetTextI18n")
        override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup?,
        ): View {
            var newView = convertView
            if (newView == null) {
                newView = lif.inflate(R.layout.listview_group_item, parent, false)
            }
            val group = getGroup(groupPosition) as String
            val tvGroup = newView!!.findViewById(R.id.group) as TextView
            val indexGroup = newView.findViewById(R.id.index) as TextView
            tvGroup.text = group
            indexGroup.text = "Group ${groupPosition + 1} ∨"
            return newView
        }

        @SuppressLint("SetTextI18n")
        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup?,
        ): View {
            var newView = convertView
            if (newView == null) {
                newView = lif.inflate(R.layout.listview_child_item, parent, false)
            }
            val item = getChild(groupPosition, childPosition) as String
            val tvChild = newView!!.findViewById(R.id.childName) as TextView
            val indexChild = newView.findViewById(R.id.childIndex) as TextView
            tvChild.text = item
            indexChild.text = "child ${childPosition + 1}"
            return newView
        }

        override fun isChildSelectable(p0: Int, p1: Int): Boolean {
            return true
        }
    }
}
