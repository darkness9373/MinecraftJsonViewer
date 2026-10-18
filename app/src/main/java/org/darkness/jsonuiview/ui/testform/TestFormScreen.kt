package org.darkness.jsonuiview.ui.testform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.export.TestFormComponent
import org.darkness.jsonuiview.data.model.export.TestFormState
import org.darkness.jsonuiview.ui.components.EmptyState
import org.darkness.jsonuiview.ui.testform.builder.ComponentEditorBottomSheet
import org.darkness.jsonuiview.ui.testform.builder.ComponentList
import org.darkness.jsonuiview.ui.testform.preview.FormPreview
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetValue
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun TestFormScreen(formId: String?) {
    val viewModel: TestFormViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val showExportDialog = remember { mutableStateOf(false) }
    val exportFormat = remember { mutableStateOf<ExportFormat>(ExportFormat.JSON) }
    
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val showComponentEditor = remember { mutableStateOf<Int?>(null) }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    TextField(
                        value = state.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text("Form Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default,
                        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { /* TODO: Navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(onClick = { 
                        showExportDialog.value = true 
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run Test")
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 4.dp))
                        Text("Run")
                    }
                    androidx.compose.material3.IconButton(onClick = { 
                        exportFormat.value = ExportFormat.JSON
                        showExportDialog.value = true 
                    }) {
                        Icon(Icons.Default.Code, contentDescription = "Export JSON")
                    }
                    androidx.compose.material3.IconButton(onClick = { 
                        exportFormat.value = ExportFormat.SCRIPT_API
                        showExportDialog.value = true 
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Export ScriptAPI")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Component List
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Components", style = androidx.compose.material3.Typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        if (state.components.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.Add,
                                title = "No components",
                                message = "Add components to build your form",
                                actionText = "Add Component",
                                onAction = { showComponentEditor.value = -1 }
                            )
                        } else {
                            ComponentList(
                                components = state.components,
                                onItemClick = { index ->
                                    showComponentEditor.value = index
                                },
                                onAddClick = { showComponentEditor.value = -1 },
                                onReorder = { from, to -> viewModel.reorderComponents(from, to) },
                                onDelete = { index -> viewModel.deleteComponent(index) }
                            )
                        }
                    }
                }
                
                // Preview
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Preview", style = androidx.compose.material3.Typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        FormPreview(
                            title = state.title,
                            components = state.components
                        )
                    }
                }
            }
        }
    }
    
    // Component Editor Bottom Sheet
    showComponentEditor.value?.let { index ->
        ComponentEditorBottomSheet(
            component = if (index >= 0 && index < state.components.size) state.components[index] else null,
            index = index,
            onConfirm = { component ->
                if (index >= 0 && index < state.components.size) {
                    viewModel.updateComponent(index, component)
                } else {
                    viewModel.addComponent(component)
                }
                showComponentEditor.value = null
            },
            onDismiss = { showComponentEditor.value = null }
        )
    }
    
    // Export Dialog
    if (showExportDialog.value) {
        ExportDialog(
            state = state,
            format = exportFormat.value,
            onConfirm = { content ->
                val clipboard = androidx.compose.ui.platform.LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.primaryClip = ClipData.newPlainText("Export", content)
                scope.launch { snackbarHostState.showSnackbar(SnackbarData("Exported to clipboard")) }
                showExportDialog.value = false
            },
            onDismiss = { showExportDialog.value = false }
        )
    }
}

enum class ExportFormat {
    JSON, SCRIPT_API
}

@Composable
fun ExportDialog(
    state: TestFormState,
    format: ExportFormat,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val content = when (format) {
        ExportFormat.JSON -> state.toJson()
        ExportFormat.SCRIPT_API -> state.toScriptAPI()
    }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export as ${format.name}") },
        text = {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .background(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = content,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(content) }) { Text("Copy") }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}