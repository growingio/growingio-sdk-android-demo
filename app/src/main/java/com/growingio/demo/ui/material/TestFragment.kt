/*
 *   Copyright (c) - 2025 Beijing Yishu Technology Co., Ltd.
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
import com.growingio.demo.databinding.FragmentTestBinding
import com.growingio.demo.ui.base.ViewBindingFragment

class TestFragment : ViewBindingFragment<FragmentTestBinding>() {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTestBinding {
        return FragmentTestBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title")
        val index = arguments?.getInt("index") ?: 0
        binding.test.text = title
        binding.test.setOnClickListener {
            showMessage("click")
        }
        val grid1Msg = "Grid${index * 10 + 1}"
        binding.grid1.text = grid1Msg
        binding.grid1.setOnClickListener {
            showMessage(grid1Msg)
        }

        val grid2Msg = "Grid${index * 10 + 2}"
        binding.grid2.text = grid2Msg
        binding.grid2.setOnClickListener {
            showMessage(grid1Msg)
        }

        val grid3Msg = "Grid${index * 10 + 3}"
        binding.grid3.text = grid3Msg
        binding.grid3.setOnClickListener {
            showMessage(grid3Msg)
        }
    }

    companion object {
        const val TAG = "TestFragment"

        fun newInstance(title: String, index: Int): TestFragment {
            val fragment = TestFragment()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putInt("index", index)
            fragment.arguments = bundle
            return fragment
        }


    }
}