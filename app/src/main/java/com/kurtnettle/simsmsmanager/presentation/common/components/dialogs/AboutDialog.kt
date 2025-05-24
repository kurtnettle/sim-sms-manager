package com.kurtnettle.simsmsmanager.presentation.common.components.dialogs


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.kurtnettle.simsmsmanager.BuildConfig
import com.kurtnettle.simsmsmanager.R

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    val repoUrl = "https://github.com/kurtnettle/sim-sms-manager"

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.padding(12.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SimCard,
                        contentDescription = "App Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = "Made by kurtnettle",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = appVersion,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AssistChip(
                        onClick = { context.openUrl(repoUrl) },
                        label = { Text(stringResource(R.string.source_code)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Code,
                                contentDescription = stringResource(R.string.source_code),
                            )
                        }
                    )

                    AssistChip(
                        onClick = { context.openUrl("$repoUrl/issues") },
                        label = { Text(stringResource(R.string.report_issue)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.report_issue),
                            )
                        }
                    )
                }
            }
        }
    }
}

fun Context.openUrl(url: String) {
    try {
        Intent(Intent.ACTION_VIEW).apply {
            setData(url.toUri())
            startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No browser available", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAboutDialog() {
    AboutDialog({})
}