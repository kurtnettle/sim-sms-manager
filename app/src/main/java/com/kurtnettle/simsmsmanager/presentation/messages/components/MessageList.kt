package com.kurtnettle.simsmsmanager.presentation.messages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R

@Composable
fun MessageList(messages: List<Map<String, String>>?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (messages.isNullOrEmpty()) {
            var title = stringResource(R.string.no_messages)
            var desc = stringResource(R.string.no_messages_sub)
            var icon = Icons.Outlined.Inbox

            if (messages == null) {
                title = stringResource(R.string.select_sim)
                desc = stringResource(R.string.select_sim_sub)
                icon = Icons.Outlined.SimCard
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = icon.name,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(messages) { index, message ->
                    MessageItem(
                        message = message,

                        )
                }
            }
        }
    }
}