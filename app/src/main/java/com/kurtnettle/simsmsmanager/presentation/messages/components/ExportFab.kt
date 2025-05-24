package com.kurtnettle.simsmsmanager.presentation.messages.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kurtnettle.simsmsmanager.ui.theme.SIMSMSManagerTheme

@Composable
fun ExportFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(imageVector = Icons.Outlined.Save, contentDescription = "Export")
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewExportFab() {
    SIMSMSManagerTheme {
        ExportFab({})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDarkExportFab() {
    SIMSMSManagerTheme(darkTheme = true) {
        ExportFab({})
    }
}