package com.kurtnettle.simsmsmanager.presentation.messages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R
import com.kurtnettle.simsmsmanager.presentation.common.components.AppTopBar
import com.kurtnettle.simsmsmanager.presentation.common.components.dialogs.exportDialog.ExportDialog
import com.kurtnettle.simsmsmanager.presentation.common.shared.MessageSharedViewModel
import com.kurtnettle.simsmsmanager.presentation.messages.components.ExportFab
import com.kurtnettle.simsmsmanager.presentation.messages.components.MessageList
import com.kurtnettle.simsmsmanager.presentation.messages.components.SimCardChooserRow
import kotlinx.coroutines.delay


@Composable
fun MessagesScreen(
    viewModel: MessageSharedViewModel
) {
    val isLoadingSimMessages by viewModel.isLoading.collectAsState(initial = false)
    val allSubInfo by viewModel.allSubInfo.collectAsState(initial = emptyList())
    val selectedSubId by viewModel.selectedSubId.collectAsState(initial = 0)
    val simMessages by viewModel.simMessages.collectAsState(initial = emptyList())

    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            delay(Toast.LENGTH_SHORT.toLong())
        }
    }

    Scaffold(
        topBar = { AppTopBar() },
        floatingActionButton = {
            if (simMessages?.isNotEmpty() == true) {
                ExportFab({ showExportDialog = true })
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->

        if (showExportDialog) {
            ExportDialog(
                viewModel = viewModel,
                onDismiss = { showExportDialog = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SimCardChooserRow(
                allSubs = allSubInfo,
                selectedSubId = selectedSubId,
                onSimSelected = { viewModel.updateSelectedSim(it) },
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (isLoadingSimMessages) {
                    LoadingMessageProgress()
                } else {
                    MessageList(simMessages, { viewModel.getSimMessages() })
                }
            }
        }
    }
}

@Composable
fun LoadingMessageProgress() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinearProgressIndicator(
                modifier = Modifier.width(72.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.loading_messages),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}