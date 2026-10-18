package org.darkness.jsonuiview.ui.testform.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.export.TestFormComponent
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.gestures.dragAndDropSource
import androidx.compose.foundation.gestures.dragAndDropTarget
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ComponentList(
    components: List<TestFormComponent>,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onReorder: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        components.forEachIndexed { index, component ->
            ComponentItem(
                component = component,
                index = index,
                onClick = { onItemClick(index) },
                onDelete = { onDelete(index) },
                onMoveUp = { if (index > 0) onReorder(index, index - 1) },
                onMoveDown = { if (index < components.size - 1) onReorder(index, index + 1) },
                isDragging = false
            )
        }
        
        // Add button
        androidx.compose.material3.OutlinedButton(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 8.dp))
            Text("Add Component")
        }
    }
}

@Composable
fun ComponentItem(
    component: TestFormComponent,
    index: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    isDragging: Boolean
) {
    val componentName = getComponentDisplayName(component)
    val componentIcon = getComponentIcon(component)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(
                color = if (isDragging) androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer 
                        else androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth(),
        onClick = onClick,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp).size(20.dp)
                )
                Icon(
                    imageVector = componentIcon,
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp).padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "#${index + 1} $componentName",
                        style = androidx.compose.material3.Typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = getComponentSummary(component),
                        style = androidx.compose.material3.Typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMoveUp) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Move up")
                }
                IconButton(onClick = onMoveDown) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Move down")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun getComponentDisplayName(component: TestFormComponent): String {
    return when (component) {
        is TestFormComponent.Button -> "Button"
        is TestFormComponent.Label -> "Label"
        is TestFormComponent.Header -> "Header"
        is TestFormComponent.Divider -> "Divider"
        is TestFormComponent.TextField -> "Text Field"
        is TestFormComponent.Dropdown -> "Dropdown"
        is TestFormComponent.Slider -> "Slider"
        is TestFormComponent.Toggle -> "Toggle"
    }
}

private fun getComponentIcon(component: TestFormComponent): androidx.compose.ui.graphics.vector.ImageVector {
    return when (component) {
        is TestFormComponent.Button -> Icons.Default.SmartButton
        is TestFormComponent.Label -> Icons.Default.Label
        is TestFormComponent.Header -> Icons.Default.Subject
        is TestFormComponent.Divider -> Icons.Default.Remove
        is TestFormComponent.TextField -> Icons.Default.TextFields
        is TestFormComponent.Dropdown -> Icons.Default.ArrowDropDownCircle
        is TestFormComponent.Slider -> Icons.Default.Tune
        is TestFormComponent.Toggle -> Icons.Default.ToggleOn
    }
}

private fun getComponentSummary(component: TestFormComponent): String {
    return when (component) {
        is TestFormComponent.Button -> component.text.takeIf { it.isNotBlank() } ?: "Empty"
        is TestFormComponent.Label -> component.text.takeIf { it.isNotBlank() } ?: "Empty"
        is TestFormComponent.Header -> component.text.takeIf { it.isNotBlank() } ?: "Empty"
        is TestFormComponent.Divider -> "Horizontal divider"
        is TestFormComponent.TextField -> "${component.label}: ${component.placeholder}"
        is TestFormComponent.Dropdown -> "${component.label} (${component.options.size} options)"
        is TestFormComponent.Slider -> "${component.label} (${component.min}-${component.max})"
        is TestFormComponent.Toggle -> "${component.label}"
    }
}