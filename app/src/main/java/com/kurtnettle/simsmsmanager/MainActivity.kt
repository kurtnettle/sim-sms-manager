package com.kurtnettle.simsmsmanager

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import com.kurtnettle.simsmsmanager.data.repository.SimCardRepository
import com.kurtnettle.simsmsmanager.presentation.common.shared.MessageSharedViewModel
import com.kurtnettle.simsmsmanager.presentation.messages.MessagesScreen
import com.kurtnettle.simsmsmanager.ui.theme.SIMSMSManagerTheme


class MainActivity : ComponentActivity() {
    private val simCardRepo by lazy { SimCardRepository(this) }
    private val messageSharedViewModel by lazy {
        MessageSharedViewModel(simCardRepo)
    }

    fun openAppInfo() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = "package:$packageName".toUri()
        startActivity(intent)
    }

    fun isDefaultSmsApp(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = this.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_SMS) ?: false
        } else {
            Telephony.Sms.getDefaultSmsPackage(this) == packageName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIMSMSManagerTheme {
                MessagesScreen(
                    viewModel = messageSharedViewModel,
                    isDefaultSmsApp = { isDefaultSmsApp() },
                    onRequestDefaultSms = { openAppInfo() }
                )
            }
        }
    }
}
