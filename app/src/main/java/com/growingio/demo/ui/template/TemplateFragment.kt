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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.growingio.demo.R
import com.growingio.demo.data.TemplateItem
import com.growingio.demo.databinding.FragmentTemplateBinding
import com.growingio.demo.ui.base.ViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TemplateFragment : ViewBindingFragment<FragmentTemplateBinding>(), TemplateAdapter.TemplateAdapterListener {

    private val viewModel: TemplateViewModel by viewModels()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTemplateBinding {
        return FragmentTemplateBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TemplateAdapter(this)
        binding.templateRv.layoutManager = LinearLayoutManager(requireContext())
        binding.templateRv.adapter = adapter

        viewModel.templates.observe(viewLifecycleOwner) {
            adapter.loadItems(it)
        }
    }

    override fun onItemClick(view: View, item: TemplateItem) {
        // flutter
        if (item.sort == 2) {
            if (FlutterEngineCache.getInstance().get(FLUTTER_ENGINE_ID) == null) {
                createFlutterEngineAndStart()
            } else {
                startFlutterEngine()
            }
            return
        }
        Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show()
    }

    private fun showFlutterEngineLoading(): Dialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.template_flutter_dialog_title)
            .setIcon(R.drawable.ic_flutter)
            .setView(R.layout.dialog_flutter_loading)
            .create()
        dialog.show()
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun createFlutterEngineAndStart() {
        lifecycleScope.launch {
            val dialog = withContext(Dispatchers.Main) {
                showFlutterEngineLoading()
            }
            val flutterEngine = FlutterEngine(requireContext().applicationContext)
            flutterEngine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
            FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)

            withContext(Dispatchers.Default) {
                // wait 2s for flutter engine init
                delay(2000)
            }
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                startFlutterEngine()
            }
        }
    }

    private fun startFlutterEngine() {
        startActivity(
            FlutterActivity
                .withCachedEngine(FLUTTER_ENGINE_ID)
                .build(requireContext().applicationContext)
        )
    }

    companion object {
        const val FLUTTER_ENGINE_ID = "flutter_engine"
    }
}
