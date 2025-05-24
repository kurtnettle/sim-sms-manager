package com.kurtnettle.simsmsmanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.kurtnettle.simsmsmanager.presentation.permission.PermissionPromptScreen
import com.kurtnettle.simsmsmanager.presentation.permission.PermissionPromptViewModel
import com.kurtnettle.simsmsmanager.ui.theme.SIMSMSManagerTheme

class PermissionPromptActivity : ComponentActivity() {
    private val viewModel: PermissionPromptViewModel by viewModels()
    private var currentPermission: String? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkMissingPermissions()
        gotoMessagesScreen()

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            currentPermission?.let { permission ->
                viewModel.updatePermission(permission, granted)
                currentPermission = null
            }

            gotoMessagesScreen()
        }

        setContent {
            SIMSMSManagerTheme {
                PermissionPromptScreen(
                    viewModel, requestPermission = { permission ->
                        currentPermission = permission
                        permissionLauncher.launch(permission)
                    }
                )
            }
        }
    }

    private fun checkMissingPermissions() {
        val permsToCheck = mutableListOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permsToCheck.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        permsToCheck.forEach { permission ->
            val granted = ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.updatePermission(permission, granted)
        }
    }

    fun gotoMessagesScreen() {
        if (viewModel.grantedPermissionsState.all { it.value }) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}