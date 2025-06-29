package com.kurtnettle.simsmsmanager.presentation.common.components.dialogs.exportDialog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kurtnettle.simsmsmanager.R
import com.kurtnettle.simsmsmanager.data.model.BackupType
import com.kurtnettle.simsmsmanager.data.repository.ExportRepository
import com.kurtnettle.simsmsmanager.presentation.common.shared.MessageSharedViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportDialog(
    viewModel: MessageSharedViewModel, onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val export = ExportRepository()
    val selectedSubId by viewModel.selectedSubId.collectAsState(initial = -1)
    val simMessages by viewModel.simMessages.collectAsState(initial = emptyList())

    fun getTimestampForFilename(): String {
        return SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
    }

    fun handleExport(backupType: BackupType) {
        var exportApp = ""
        var json = ""

        json = when (backupType) {
            BackupType.RAW -> {
                exportApp = "RAW"
                export.createRawJson(simMessages ?: emptyList())
            }

            BackupType.FOSSIFY -> {
                exportApp = "FOSSIFY"
                export.createFossifyMessagesJson(simMessages ?: emptyList())
            }

            BackupType.QUIK -> {
                exportApp = "QUIK"
                export.createQuikJson(simMessages ?: emptyList())
            }
        }

        scope.launch {
            val filename =
                "SSManager-Sim${selectedSubId}-${exportApp}-${getTimestampForFilename()}.json"
            try {
                export.saveFile(context, filename, json)
                Toast.makeText(
                    context,
                    context.getString(R.string.export_success),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.export_failed, e.localizedMessage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                ExportDialogHeader(
                    onDismiss = onDismiss
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Body (Export options)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background.copy(0.6f))
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 4.dp),

                    ) {
                    ExportOptionItem(
                        stringResource(R.string.export_json),
                        stringResource(R.string.export_json_sub),
                        Icons.Outlined.Code,
                        { handleExport(BackupType.RAW) }
                    )

                    ItemDivider()

                    ExportOptionItem(
                        stringResource(R.string.export_fossify),
                        stringResource(R.string.export_fossify_sub),
                        Icons.AutoMirrored.Outlined.Message,
                        { handleExport(BackupType.FOSSIFY) }
                    )

                    ItemDivider()

                    ExportOptionItem(
                        stringResource(R.string.export_quik),
                        stringResource(R.string.export_quik_sub),
                        Icons.Outlined.Sync,
                        { handleExport(BackupType.QUIK) }
                    )
                }

                // Footer
                ExportDialogFooter(onDismiss = onDismiss)
            }
        }
    }
}


@Composable
fun ItemDivider() {
    HorizontalDivider(
        thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp)
    )
}


////@Preview
////@Composable
////fun PreviewExportOptionItem() {
////    SIMSMSManagerTheme {
////        ExportMessageScreen()
////    }
////}

