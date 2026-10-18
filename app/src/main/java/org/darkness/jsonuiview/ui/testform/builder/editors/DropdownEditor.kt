package org.darkness.jsonuiview.ui.testform.builder.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.darkness.jsonuiview.data.model.export.TestFormComponent

@Composable
fun DropdownEditor(
    component: TestFormComponent.Dropdown?,
    onConfirm: (TestFormComponent) -> Unit
) {
    var label by remember { mutableStateOf(component?.label ?: "") }
    var optionsText by remember { mutableStateOf(component?.options.joinToString(", ") ?: "") }
    var defaultIndex by remember { mutableStateOf(component?.defaultIndex ?: 0) }
    
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        TextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        TextField(
            value = optionsText,
            onValueChange = { optionsText = it },
            label = { Text("Options (comma separated)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Option 1, Option 2, Option 3") }
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        TextField(
            value = defaultIndex.toString(),
            onValueChange = { defaultIndex = it.toIntOrNull() ?: 0 },
            label = { Text("Default Index (0-based)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { 
                val options = optionsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                onConfirm(TestFormComponent.Dropdown(label, options, if (options.isNotEmpty()) defaultIndex else null))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Dropdown")
        }
    }
}