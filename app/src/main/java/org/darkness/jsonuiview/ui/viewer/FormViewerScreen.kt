package org.darkness.jsonuiview.ui.viewer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.form.FormComponent
import org.darkness.jsonuiview.data.model.form.FormType
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.ui.components.EmptyState
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import org.darkness.jsonuiview.ui.viewer.components.ActionFormDisplay
import org.darkness.jsonuiview.ui.viewer.components.JsonBottomSheet
import org.darkness.jsonuiview.ui.viewer.components.ModalFormDisplay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.foundation.layout.width

@Composable
fun FormViewerScreen(
    formId: String,
    onTestClick: (ResolvedForm) -> Unit
) {
    val viewModel: FormViewerViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val form by viewModel.getForm(formId).collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val showJsonSheet by viewModel.showJsonSheet.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = form?.title ?: "Loading...",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { /* TODO: Navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    form?.let { f ->
                        androidx.compose.material3.IconButton(onClick = { onTestClick(f) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.test_form))
                        }
                        androidx.compose.material3.IconButton(onClick = { 
                            viewModel.showJsonSheet.value = true 
                        }) {
                            Icon(Icons.Default.Code, contentDescription = stringResource(R.string.raw_json))
                        }
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
            if (isLoading) {
                org.darkness.jsonuiview.ui.components.LoadingIndicator("Loading form...")
            } else {
                form?.let { f ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (f.type) {
                            FormType.ACTION_FORM -> ActionFormDisplay(components = f.components)
                            FormType.MODAL_FORM -> ModalFormDisplay(components = f.components)
                        }
                    }
                } ?: EmptyState(
                    icon = Icons.Default.ErrorOutline,
                    title = "Form not found",
                    message = "The requested form could not be loaded"
                )
            }
        }
    }
    
    if (showJsonSheet) {
        form?.let { f ->
            JsonBottomSheet(
                json = f.rawJson,
                onDismiss = { viewModel.showJsonSheet.value = false },
                onCopy = {
                    val clipboard = androidx.compose.ui.platform.LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.primaryClip = ClipData.newPlainText("JSON", f.rawJson)
                    scope.launch { snackbarHostState.showSnackbar(SnackbarData("Copied to clipboard")) }
                }
            )
        }
    }
}