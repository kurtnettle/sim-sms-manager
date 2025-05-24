package com.kurtnettle.simsmsmanager.presentation.permission

import android.Manifest
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel

class PermissionPromptViewModel() : ViewModel() {
    private val _grantedPermissionsState: SnapshotStateMap<String, Boolean> = mutableStateMapOf(
        Manifest.permission.READ_SMS to false,
        Manifest.permission.READ_PHONE_STATE to false
    )

    val grantedPermissionsState = _grantedPermissionsState

    fun updatePermission(permission: String, granted: Boolean) {
        _grantedPermissionsState[permission] = granted
    }
}