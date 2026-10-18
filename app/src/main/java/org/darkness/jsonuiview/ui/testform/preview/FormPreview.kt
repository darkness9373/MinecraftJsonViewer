package org.darkness.jsonuiview.ui.testform.preview

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
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ToggleOn

@Composable
fun FormPreview(
    title: String,
    components: List<TestFormComponent>
) {
    // Mock Minecraft form preview
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFF333333), RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title.ifBlank { "Form Title" },
                    style = androidx.compose.material3.Typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            
            // Components
            if (components.isEmpty()) {
                Text(
                    text = "No components added",
                    style = androidx.compose.material3.Typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.CenterHorizontally)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    components.forEach { component ->
                        when (component) {
                            is TestFormComponent.Button -> PreviewButton(component)
                            is TestFormComponent.Label -> PreviewLabel(component)
                            is TestFormComponent.Header -> PreviewHeader(component)
                            is TestFormComponent.Divider -> PreviewDivider()
                            is TestFormComponent.TextField -> PreviewTextField(component)
                            is TestFormComponent.Dropdown -> PreviewDropdown(component)
                            is TestFormComponent.Slider -> PreviewSlider(component)
                            is TestFormComponent.Toggle -> PreviewToggle(component)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewButton(button: TestFormComponent.Button) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SmartButton,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp).padding(end = 12.dp)
            )
            Text(
                text = button.text.ifBlank { "Button" },
                style = androidx.compose.material3.Typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
fun PreviewLabel(label: TestFormComponent.Label) {
    Text(
        text = label.text.ifBlank { "Label" },
        style = androidx.compose.material3.Typography.bodyMedium,
        color = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
fun PreviewHeader(header: TestFormComponent.Header) {
    Text(
        text = header.text.ifBlank { "Header" },
        style = androidx.compose.material3.Typography.titleSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
fun PreviewDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
fun PreviewTextField(field: TestFormComponent.TextField) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = field.label.ifBlank { "Label" },
                style = androidx.compose.material3.Typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            androidx.compose.material3.TextField(
                value = field.defaultValue ?: "",
                onValueChange = { /* Preview only */ },
                placeholder = { Text(field.placeholder.ifBlank { "Placeholder" }) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedContainerColor = Color(0xFF1E1E1E),
                    textColor = Color.White,
                    placeholderColor = Color.White.copy(alpha = 0.4f),
                    labelColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun PreviewDropdown(dropdown: TestFormComponent.Dropdown) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = dropdown.label.ifBlank { "Label" },
                style = androidx.compose.material3.Typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dropdown.options.getOrNull(dropdown.defaultIndex ?: 0) ?: "Select...",
                    style = androidx.compose.material3.Typography.bodyMedium,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDownCircle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PreviewSlider(slider: TestFormComponent.Slider) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = slider.label.ifBlank { "Label" },
                style = androidx.compose.material3.Typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            androidx.compose.material3.Slider(
                value = (slider.defaultValue ?: slider.min).toFloat(),
                onValueChange = { /* Preview only */ },
                valueRange = slider.min.toFloat()..slider.max.toFloat(),
                steps = ((slider.max - slider.min) / slider.step),
                enabled = false,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    activeTrackColor = Color(0xFF4CAF50),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                    activeThumbColor = Color(0xFF4CAF50),
                    inactiveThumbColor = Color.White.copy(alpha = 0.5f)
                )
            )
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${slider.min}", style = androidx.compose.material3.Typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                Text("${slider.max}", style = androidx.compose.material3.Typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
            }
        }
    )
}

@Composable
fun PreviewToggle(toggle: TestFormComponent.Toggle) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = toggle.label.ifBlank { "Label" },
                style = androidx.compose.material3.Typography.bodyMedium,
                color = Color.White
            )
            androidx.compose.material3.Switch(
                checked = toggle.defaultValue ?: false,
                onCheckedChange = { /* Preview only */ },
                enabled = false,
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}