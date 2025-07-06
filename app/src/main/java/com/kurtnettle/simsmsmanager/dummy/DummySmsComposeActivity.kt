package com.kurtnettle.simsmsmanager.dummy

import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.kurtnettle.simsmsmanager.ui.theme.SIMSMSManagerTheme

class DummySmsComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent?.action) {
            Intent.ACTION_SEND,
            Intent.ACTION_SENDTO,
            Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT -> {
                finish()
                return
            }
        }

        setContent {
            SIMSMSManagerTheme {
                Text("Dummy SMS Compose Activity")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
    }
}