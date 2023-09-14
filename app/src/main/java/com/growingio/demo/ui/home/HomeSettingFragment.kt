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

package com.growingio.demo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.growingio.android.sdk.TrackerContext
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.demo.GrowingioInitializer
import com.growingio.demo.R
import com.growingio.demo.data.settingsDataStore
import com.growingio.demo.databinding.FragmentProjectSettingBinding
import kotlinx.coroutines.launch

/**
 * <p>
 *
 * @author cpacm 2023/9/14
 */

class HomeSettingFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentProjectSettingBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProjectSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (TrackerContext.initializedSuccessfully()) {
            val configuration = GrowingAutotracker.get().context.configurationProvider
            val projectId = configuration.core().projectId
            val urlScheme = configuration.core().urlScheme
            val dataSourceId = configuration.core().dataSourceId
            val serverHost = configuration.core().dataCollectionServerHost

            binding.pidEt.setText(projectId)
            binding.urlEt.setText(urlScheme)
            binding.dataEt.setText(dataSourceId)
            binding.hostEt.setText(serverHost)
        }

        binding.settle.setOnClickListener {
            val pid = binding.pidEt.text.toString()
            val url = binding.urlEt.text.toString()
            val data = binding.dataEt.text.toString()
            val host = binding.hostEt.text.toString()

            if (!TrackerContext.initializedSuccessfully()) {
                dismiss()
                return@setOnClickListener
            }

            if (pid.isEmpty() || url.isEmpty() || data.isEmpty() || host.isEmpty()) {
                Snackbar.make(view, R.string.setting_settle_hint, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val configuration = GrowingAutotracker.get().context.configurationProvider.core();
            if (pid == configuration.projectId && data == configuration.dataSourceId && host == configuration.dataCollectionServerHost) {
                Snackbar.make(view, R.string.setting_settle_hint2, Snackbar.LENGTH_SHORT).show()
                dismiss()
                return@setOnClickListener
            }


            lifecycleScope.launch {

                GrowingAutotracker.shutdown()
                requireContext().settingsDataStore.updateData {
                    it.toBuilder().setProjectId(pid)
                        .setUrlScheme(url)
                        .setDatasourceId(data)
                        .setServerHost(host)
                        .build()
                }

                val sdkInitialize = GrowingioInitializer()
                sdkInitialize.create(requireActivity())
                dismiss()
            }

        }
    }
}