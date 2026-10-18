package org.darkness.jsonuiview.ui.formlist

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.form.FormType
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.ui.components.EmptyState
import org.darkness.jsonuiview.ui.components.FormCard
import org.darkness.jsonuiview.ui.components.LoadingIndicator
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Tabs
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size

@Composable
fun FormListScreen(
    fileUri: String,
    onFormClick: (ResolvedForm) -> Unit,
    onTestFormClick: (ResolvedForm) -> Unit
) {
    val viewModel: FormListViewModel = viewModel()
    val uri = remember { Uri.parse(fileUri) }
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Action Forms", "Modal Forms", "All")
    
    val forms by viewModel.getForms(uri).collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    val filteredForms = remember(forms, selectedTab) {
        when (selectedTab) {
            0 -> forms.filter { it.type == FormType.ACTION_FORM }
            1 -> forms.filter { it.type == FormType.MODAL_FORM }
            else -> forms
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(uri.lastPathSegment ?: "Forms") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { /* TODO: Navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    androidx.compose.material3.IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                )
            )
            
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                indicator = { tabPositions ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .height(2.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            .offset(x = tabPositions[selectedTab].left)
                    )
                },
                divider = { androidx.compose.foundation.layout.Box(Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
                ) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            if (isLoading) {
                LoadingIndicator("Parsing forms...")
            } else if (filteredForms.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.FilterList,
                    title = stringResource(R.string.no_forms_found),
                    message = "No ${tabs[selectedTab].lowercase()} found in this file"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(filteredForms) { form ->
                        FormCard(
                            form = form,
                            onClick = { onFormClick(form) },
                            onTestClick = { onTestFormClick(form) }
                        )
                    }
                }
            }
        }
    }
}