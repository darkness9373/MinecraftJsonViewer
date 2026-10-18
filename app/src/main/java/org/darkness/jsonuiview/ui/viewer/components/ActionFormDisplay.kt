package org.darkness.jsonuiview.ui.viewer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.darkness.jsonuiview.R
import org.darkness.jsonuiview.data.model.form.FormComponent
import org.darkness.jsonuiview.ui.theme.ThemeSurface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Remove

@Composable
fun ActionFormDisplay(components: List<FormComponent>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        components.forEach { component ->
            when (component) {
                is FormComponent.Button -> ActionButton(component)
                is FormComponent.Label -> FormLabel(component)
                is FormComponent.Header -> FormHeader(component)
                is FormComponent.Divider -> FormDivider()
                is FormComponent.Custom -> CustomComponentDisplay(component)
                else -> {
                    // Skip modal-only components in action form
                }
            }
        }
    }
}

@Composable
fun ModalFormDisplay(components: List<FormComponent>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        components.forEach { component ->
            when (component) {
                is FormComponent.TextField -> FormTextField(component)
                is FormComponent.Dropdown -> FormDropdown(component)
                is FormComponent.Slider -> FormSlider(component)
                is FormComponent.Toggle -> FormToggle(component)
                is FormComponent.Label -> FormLabel(component)
                is FormComponent.Header -> FormHeader(component)
                is FormComponent.Divider -> FormDivider()
                is FormComponent.Button -> ActionButton(component)
                is FormComponent.Custom -> CustomComponentDisplay(component)
                else -> {
                    // Skip action-only components in modal form
                }
            }
        }
    }
}

@Composable
fun ActionButton(button: FormComponent.Button) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            button.iconPath?.let { icon ->
                Icon(
                    imageVector = getIconForPath(icon),
                    contentDescription = null,
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp).padding(end = 12.dp)
                )
            }
            Text(
                text = button.text,
                style = androidx.compose.material3.Typography.bodyLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FormLabel(label: FormComponent.Label) {
    Text(
        text = label.text,
        style = androidx.compose.material3.Typography.bodyMedium,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
fun FormHeader(header: FormComponent.Header) {
    Text(
        text = header.text,
        style = androidx.compose.material3.Typography.titleMedium,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun FormDivider() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun FormTextField(field: FormComponent.TextField) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = field.label,
                style = androidx.compose.material3.Typography.labelMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            androidx.compose.material3.TextField(
                value = field.defaultValue ?: "",
                onValueChange = { /* Read-only */ },
                placeholder = { Text(field.placeholder) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }
    }
}

@Composable
fun FormDropdown(dropdown: FormComponent.Dropdown) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dropdown.label,
                style = androidx.compose.material3.Typography.labelMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            androidx.compose.material3.Text(
                text = dropdown.options.getOrNull(dropdown.defaultIndex ?: 0) ?: dropdown.options.firstOrNull() ?: "Select...",
                style = androidx.compose.material3.Typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            if (dropdown.options.size > 1) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = "${dropdown.options.size} options available",
                    style = androidx.compose.material3.Typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FormSlider(slider: FormComponent.Slider) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = slider.label,
                style = androidx.compose.material3.Typography.labelMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            androidx.compose.material3.Slider(
                value = (slider.defaultValue ?: slider.min).toFloat(),
                onValueChange = { /* Read-only */ },
                valueRange = slider.min.toFloat()..slider.max.toFloat(),
                steps = ((slider.max - slider.min) / slider.step),
                enabled = false,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    activeTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                )
            )
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${slider.min}", style = androidx.compose.material3.Typography.bodySmall)
                Text("${slider.max}", style = androidx.compose.material3.Typography.bodySmall)
            }
        }
    }
}

@Composable
fun FormToggle(toggle: FormComponent.Toggle) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = toggle.label,
                style = androidx.compose.material3.Typography.bodyMedium
            )
            androidx.compose.material3.Switch(
                checked = toggle.defaultValue ?: false,
                onCheckedChange = { /* Read-only */ },
                enabled = false
            )
        }
    }
}

@Composable
fun CustomComponentDisplay(custom: FormComponent.Custom) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Custom: ${custom.type}",
                style = androidx.compose.material3.Typography.labelMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
            custom.properties.forEach { (key, value) ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, style = androidx.compose.material3.Typography.bodySmall)
                    Text(value.toString(), style = androidx.compose.material3.Typography.bodySmall, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun getIconForPath(path: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        path.contains("diamond") -> Icons.Default.Diamond
        path.contains("sword") -> Icons.Default.SportsKabaddi
        path.contains("apple") -> Icons.Default.LocalDining
        path.contains("chest") -> Icons.Default.Inventory
        path.contains("block") -> Icons.Default.CropSquare
        else -> Icons.Default.Widgets
    }
}