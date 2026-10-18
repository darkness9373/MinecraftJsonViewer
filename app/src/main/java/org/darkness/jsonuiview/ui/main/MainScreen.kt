package org.darkness.jsonuiview.ui.main

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.ui.components.EmptyState
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History

@Composable
fun MainScreen(
    onFileSelected: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as Activity
    
    val pickFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { onFileSelected(it.toString()) }
    }
    
    val recentFiles = remember { mutableStateOf<List<RecentFileItem>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    
    // TODO: Load recent files from repository
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThemeSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = androidx.compose.material3.Typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text(
                        text = "Select a server_form.json file to view Action Forms and Modal Forms",
                        style = androidx.compose.material3.Typography.bodyMedium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.TextAlign.Center
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
                    Button(
                        onClick = { pickFileLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 8.dp))
                        Text(stringResource(R.string.pick_file))
                    }
                }
            }
        }
        
        // Recent files section (if any)
        if (recentFiles.value.isNotEmpty()) {
            // TODO: Show recent files list
        }
    }
}

data class RecentFileItem(
    val uri: String,
    val displayName: String,
    val lastOpened: Long
)