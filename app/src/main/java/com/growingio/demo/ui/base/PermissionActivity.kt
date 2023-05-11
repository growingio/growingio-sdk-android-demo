package com.cpacm.moment.ui.base

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * <p>
 *
 * @author cpacm 2023/5/10
 */
open class PermissionActivity : BaseActivity() {

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
}