package com.kurtnettle.simsmsmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kurtnettle.simsmsmanager.data.repository.SimCardRepository
import com.kurtnettle.simsmsmanager.presentation.common.shared.MessageSharedViewModel
import com.kurtnettle.simsmsmanager.presentation.messages.MessagesScreen
import com.kurtnettle.simsmsmanager.ui.theme.SIMSMSManagerTheme


class MainActivity : ComponentActivity() {
    private val simCardRepo by lazy { SimCardRepository(this) }
    private val messageSharedViewModel by lazy {
        MessageSharedViewModel(simCardRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIMSMSManagerTheme {
                MessagesScreen(
                    viewModel = messageSharedViewModel
                )
            }
        }
    }
}
