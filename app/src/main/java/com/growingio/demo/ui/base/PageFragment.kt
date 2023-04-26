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

package com.growingio.demo.ui.base

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.demo.databinding.FragmentCodePreviewBinding
import com.growingio.demo.databinding.FragmentPageBinding
import com.growingio.demo.ui.widgets.ContainerTransformConfigurationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * <p>
 *
 * @author cpacm 2023/4/21
 */
abstract class PageFragment<T : ViewBinding> : ViewBindingFragment<FragmentPageBinding>() {

    lateinit var _codeBinding: FragmentCodePreviewBinding
    val codeBinding: FragmentCodePreviewBinding
        get() = _codeBinding

    lateinit var _pageBinding: T
    val pageBinding: T get() = _pageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPageBinding {
        val createBinding = FragmentPageBinding.inflate(inflater, container, false)
        createBinding.codeLayout.addView(createCodeView(inflater, container))
        createBinding.apiLayout.addView(createApiView(inflater, container))
        return createBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        binding.tabGroup.check(binding.tabGroup.getChildAt(0).id)
        binding.tabGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            when (checkedId) {
                binding.apiTab.id -> {
                    val sharedAxis = createTransition(false)
                    TransitionManager.beginDelayedTransition(binding.root, sharedAxis);
                    binding.codeLayout.visibility = View.GONE
                    binding.apiLayout.visibility = View.VISIBLE
                }
                binding.codeTab.id -> {
                    val sharedAxis = createTransition(true)
                    TransitionManager.beginDelayedTransition(binding.root, sharedAxis);
                    binding.codeLayout.visibility = View.VISIBLE
                    binding.apiLayout.visibility = View.GONE
                }
            }
        }

        GrowingAutotracker.get().ignoreViewClick(binding.logcatFab, true)
        binding.logcatFab.setOnClickListener {
            showLogcatView()
        }

        binding.logcatView.closeActionWith { discardLogcatView() }

        ViewCompat.setTransitionName(binding.logcatFab, binding.logcatFab.id.toString())
        ViewCompat.setTransitionName(binding.logcatView, binding.logcatView.id.toString())
    }

    protected fun setTitle(title: String) {
        binding.toolbar.title = title
    }

    private fun createCodeView(inflater: LayoutInflater, container: ViewGroup?): View {
        _codeBinding = FragmentCodePreviewBinding.inflate(inflater, container, false)
        return codeBinding.root
    }

    private fun createApiView(inflater: LayoutInflater, container: ViewGroup?): View {
        _pageBinding = createPageBinding(inflater, container)
        return _pageBinding.root
    }

    abstract fun createPageBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun loadAssetCode(clazz: Any, language: String = "kotlin", showLineNumbers: Boolean = false) {
        val fileName = clazz.javaClass.name + ".code"
        loadAssetCode(fileName, language, showLineNumbers)
    }

    fun loadAssetCode(fileName: String, language: String = "kotlin", showLineNumbers: Boolean = false) {
        lifecycleScope.launch {
            val text = withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    requireContext().assets.open(fileName).bufferedReader().readText()
                }.onFailure {
                    Log.e("PageFragment", "loadAssetCode: ", it)
                }.getOrNull()
            }
            if (text != null) bindSyntaxHighlighter(text, language, showLineNumbers)
        }

    }

    fun bindSyntaxHighlighter(formattedSourceCode: String, language: String, showLineNumbers: Boolean = false) {
        codeBinding.syntaxView.bindSyntaxHighlighter(formattedSourceCode, language, showLineNumbers)
    }

    private fun createTransition(entering: Boolean): MaterialSharedAxis {
        val transition = MaterialSharedAxis(MaterialSharedAxis.X, entering);

        // Add targets for this transition to explicitly run transitions only on these views. Without
        // targeting, a MaterialSharedAxis transition would be run for every view in the
        // the ViewGroup's layout.
        transition.addTarget(binding.apiLayout)
        transition.addTarget(binding.codeLayout)

        return transition
    }

    fun hideTabGroup() {
        binding.tabGroup.visibility = View.GONE
    }

    fun showTabGroup() {
        binding.tabGroup.visibility = View.VISIBLE
    }

    private val configurationHelper: ContainerTransformConfigurationHelper by lazy { ContainerTransformConfigurationHelper() }
    protected fun buildContainerTransform(entering: Boolean): MaterialContainerTransform {
        val transform = MaterialContainerTransform(requireContext(), entering)
        transform.scrimColor = Color.TRANSPARENT
        configurationHelper.configure(transform, entering)
        return transform
    }

    private fun showLogcatView() {
        val transition = buildContainerTransform(true)
        transition.startView = binding.logcatFab
        transition.endView = binding.logcatView
        transition.addTarget(binding.logcatView)
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.logcatFab.visibility = View.INVISIBLE
        binding.logcatView.visibility = View.VISIBLE
    }

    private fun discardLogcatView() {
        val transition = buildContainerTransform(false)
        transition.endView = binding.logcatFab
        transition.startView = binding.logcatView
        transition.addTarget(binding.logcatFab)

        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.logcatFab.visibility = View.VISIBLE
        binding.logcatView.visibility = View.INVISIBLE
    }

    override fun onBackPressed(): Boolean {
        if (binding.logcatView.visibility == View.VISIBLE) {
            discardLogcatView()
            return true
        }
        return super.onBackPressed()
    }

}