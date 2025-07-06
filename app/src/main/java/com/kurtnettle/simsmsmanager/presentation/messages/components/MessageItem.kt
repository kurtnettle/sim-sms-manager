package com.kurtnettle.simsmsmanager.presentation.messages.components

import android.os.Build
import android.util.Log
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R
import com.kurtnettle.simsmsmanager.data.model.Message
import com.kurtnettle.simsmsmanager.presentation.common.components.dialogs.PropertiesDialog
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


@Composable
fun MessageItem(
    message: Map<String, String>,
    isSelected: Boolean,
    onSelected: () -> Unit,
    hasMultiSelect: Boolean
) {
    var showPropertiesDialog by remember { mutableStateOf(false) }
    val msg = Message(
        _id = message["_id"],
        date = message["date"].toString(),
        body = message["body"].toString(),
        address = message["address"].toString()
    )

    if (showPropertiesDialog) {
        PropertiesDialog(
            title = stringResource(R.string.message_properties),
            message,
            { showPropertiesDialog = false })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(
                MaterialTheme.shapes.large
            )
            .combinedClickable(
                onClick = {
                    Log.d("SSManager", "onClick: hasMultiSelect: $hasMultiSelect")

                    if (!hasMultiSelect) {
                        showPropertiesDialog = true
                    } else {
                        onSelected()
                    }
                },
                onLongClick = {
                    Log.d("SSManager", "onLongClick: hasMultiSelect: $hasMultiSelect")
                    if (!hasMultiSelect) onSelected()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg.address,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = msg.date.formatAsReadableTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = msg.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


fun String.formatAsReadableTime(): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDateTime = Instant.ofEpochMilli(this.toLong())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            localDateTime.format(DateTimeFormatter.ofPattern("y-M-d H:mm"))
        } else {
            val date = Date(this.toLong())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            outputFormat.format(date)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}
