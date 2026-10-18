package org.darkness.jsonuiview.ui.testform.builder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.export.TestFormComponent
import org.darkness.jsonuiview.ui.testform.builder.editors.ButtonEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.DropdownEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.HeaderEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.LabelEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.SliderEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.TextFieldEditor
import org.darkness.jsonuiview.ui.testform.builder.editors.ToggleEditor
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetValue
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState

@Composable
fun ComponentEditorBottomSheet(
    component: TestFormComponent?,
    index: Int,
    onConfirm: (TestFormComponent) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.Hidden }
    )
    
    // Auto-show
    androidx.compose.runtime.LaunchedEffect(key1 = Unit) {
        bottomSheetState.show()
    }
    
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ComponentType?>(null) }
    
    // Initialize from existing component
    androidx.compose.runtime.LaunchedEffect(key1 = component) {
        component?.let { c ->
            selectedType = ComponentType.fromComponent(c)
            title = when (c) {
                is TestFormComponent.Button -> c.text
                is TestFormComponent.Label -> c.text
                is TestFormComponent.Header -> c.text
                is TestFormComponent.TextField -> c.label
                is TestFormComponent.Dropdown -> c.label
                is TestFormComponent.Slider -> c.label
                is TestFormComponent.Toggle -> c.label
                else -> ""
            }
        }
    }
    
    ModalBottomSheet(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { Text(if (component == null) "Add Component" else "Edit Component") },
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                
                // Component type selector (only for new components)
                if (component == null) {
                    ComponentTypeSelector(
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it }
                    )
                }
                
                // Editor for selected type
                selectedType?.let { type ->
                    ComponentEditorContent(
                        type = type,
                        component = component,
                        title = title,
                        onTitleChange = { title = it },
                        onConfirm = { comp -> onConfirm(comp) }
                    )
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}

@Composable
fun ComponentTypeSelector(
    selectedType: ComponentType?,
    onTypeSelected: (ComponentType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Select Component Type", style = androidx.compose.material3.Typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
        
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ComponentType.values().forEach { type ->
                androidx.compose.foundation.lazy.items(1) {
                    val isSelected = selectedType == type
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(vertical = 8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer 
                                           else androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(type.icon, contentDescription = null, tint = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer else androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                            Text(type.displayName, style = androidx.compose.material3.Typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentEditorContent(
    type: ComponentType,
    component: TestFormComponent?,
    title: String,
    onTitleChange: (String) -> Unit,
    onConfirm: (TestFormComponent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Title field (for most components)
        if (type != ComponentType.DIVIDER) {
            TextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title / Label") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        }
        
        when (type) {
            ComponentType.BUTTON -> ButtonEditor(component = component as? TestFormComponent.Button, onConfirm = onConfirm)
            ComponentType.LABEL -> LabelEditor(component = component as? TestFormComponent.Label, onConfirm = onConfirm)
            ComponentType.HEADER -> HeaderEditor(component = component as? TestFormComponent.Header, onConfirm = onConfirm)
            ComponentType.DIVIDER -> DividerEditor(onConfirm = onConfirm)
            ComponentType.TEXT_FIELD -> TextFieldEditor(component = component as? TestFormComponent.TextField, onConfirm = onConfirm)
            ComponentType.DROPDOWN -> DropdownEditor(component = component as? TestFormComponent.Dropdown, onConfirm = onConfirm)
            ComponentType.SLIDER -> SliderEditor(component = component as? TestFormComponent.Slider, onConfirm = onConfirm)
            ComponentType.TOGGLE -> ToggleEditor(component = component as? TestFormComponent.Toggle, onConfirm = onConfirm)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        androidx.compose.material3.Button(
            onClick = { /* Handled by editors */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) {
            Text("Done")
        }
    }
}

enum class ComponentType(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    BUTTON("Button", Icons.Default.SmartButton),
    LABEL("Label", Icons.Default.Label),
    HEADER("Header", Icons.Default.Subject),
    DIVIDER("Divider", Icons.Default.Remove),
    TEXT_FIELD("Text Field", Icons.Default.TextFields),
    DROPDOWN("Dropdown", Icons.Default.ArrowDropDownCircle),
    SLIDER("Slider", Icons.Default.Tune),
    TOGGLE("Toggle", Icons.Default.ToggleOn);
    
    companion object {
        fun fromComponent(component: TestFormComponent): ComponentType {
            return when (component) {
                is TestFormComponent.Button -> BUTTON
                is TestFormComponent.Label -> LABEL
                is TestFormComponent.Header -> HEADER
                is TestFormComponent.Divider -> DIVIDER
                is TestFormComponent.TextField -> TEXT_FIELD
                is TestFormComponent.Dropdown -> DROPDOWN
                is TestFormComponent.Slider -> SLIDER
                is TestFormComponent.Toggle -> TOGGLE
            }
        }
    }
}

@Composable
fun DividerEditor(onConfirm: (TestFormComponent) -> Unit) {
    androidx.compose.material3.Button(
        onClick = { onConfirm(TestFormComponent.Divider) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Add Divider")
    }
}