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

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.growingio.android.sdk.autotrack.GrowingAutotracker

/**
 * <p>
 *
 * @author cpacm 2023/04/14
 */
open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val result = onBackPressed()
                if (!result) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

//        GrowingAutotracker.get().autotrackPage(this, this.javaClass.simpleName)
    }

    /**
     * find the first navController
     */
    fun findParentNavController(): NavController? {
        var findFragment: Fragment? = this
        var tempNavController: NavController? = null
        while (findFragment != null) {
            if (findFragment is NavHostFragment) {
                tempNavController = findFragment.navController
            } else {
                val primaryNavFragment = findFragment.parentFragmentManager
                    .primaryNavigationFragment
                if (primaryNavFragment is NavHostFragment) {
                    tempNavController = primaryNavFragment.navController
                }
            }
            findFragment = findFragment.parentFragment
        }
        if (tempNavController != null) return tempNavController

        // Try looking for one associated with the view instead, if applicable
        val view = this.view
        if (view != null) {
            try {
                return Navigation.findNavController(view)
            } catch (ignored: IllegalStateException) {
            }
        }
        Log.e("BaseFragment", "Fragment $this does not have a NavController set")
        return null
    }

    protected fun registerPermissions(permissions: Array<String>, callback: (Map<String, Boolean>) -> Unit) {
        val requestPermissionsContract = ActivityResultContracts.RequestMultiplePermissions()
        val permissionsLaunch = registerForActivityResult(requestPermissionsContract) {
            callback(it)
        }
        permissionsLaunch.launch(permissions)
    }

    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var permissionCallback: ActivityResultCallback<Boolean>? = null
    protected fun launchPermission(permission: String, callback: ActivityResultCallback<Boolean>) {
        permissionCallback = callback
        permissionLauncher?.launch(permission)
    }

    protected fun registerPermissionContract() {
        val requestPermissionContract = ActivityResultContracts.RequestPermission()
        permissionLauncher = registerForActivityResult(requestPermissionContract) {
            permissionCallback?.onActivityResult(it)
        }
    }

    open fun onBackPressed(): Boolean {
        return findParentNavController()?.popBackStack() ?: false
    }
}
