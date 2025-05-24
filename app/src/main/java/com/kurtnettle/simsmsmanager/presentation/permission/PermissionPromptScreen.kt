package com.kurtnettle.simsmsmanager.presentation.permission

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R

@Composable
fun PermissionPromptScreen(
    viewModel: PermissionPromptViewModel,
    requestPermission: (String) -> Unit
) {
    val permissionsState by remember { derivedStateOf { viewModel.grantedPermissionsState } }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Permissions",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.required_permissions_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.required_permissions_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            PermissionItem(
                name = stringResource(R.string.perm_read_sms_title),
                description = stringResource(R.string.perm_read_sms_desc),
                icon = Icons.Outlined.Sms,
                isChecked = permissionsState[Manifest.permission.READ_SMS] == true,
                onCheckedChange = { requestPermission(Manifest.permission.READ_SMS) }
            )

            HorizontalDivider(thickness = 1.dp)

            PermissionItem(
                name = stringResource(R.string.perm_phone_state_title),
                description = stringResource(R.string.perm_phone_state_desc),
                icon = Icons.Outlined.PhoneAndroid,
                isChecked = permissionsState[Manifest.permission.READ_PHONE_STATE] == true,
                onCheckedChange = { requestPermission(Manifest.permission.READ_PHONE_STATE) }
            )


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                HorizontalDivider(thickness = 1.dp)

                PermissionItem(
                    name = stringResource(R.string.perm_storage_title),
                    description = stringResource(R.string.perm_storage_desc),
                    icon = Icons.Outlined.Storage,
                    isChecked = permissionsState[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true,
                    onCheckedChange = { requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) }
                )
            }

        }
    }
}

@Composable
fun PermissionItem(
    name: String,
    description: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "${icon.name} icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Checkbox(
            checked = isChecked,
            enabled = !isChecked,
            onCheckedChange = { onCheckedChange() },
            modifier = Modifier.size(24.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
