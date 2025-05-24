package com.kurtnettle.simsmsmanager.presentation.common.components.dialogs.exportDialog


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R

@Composable
fun ExportDialogFooter(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onDismiss,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}