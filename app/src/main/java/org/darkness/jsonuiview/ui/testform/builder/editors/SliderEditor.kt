package org.darkness.jsonuiview.ui.testform.builder.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
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
fun SliderEditor(
    component: TestFormComponent.Slider?,
    onConfirm: (TestFormComponent) -> Unit
) {
    var label by remember { mutableStateOf(component?.label ?: "") }
    var min by remember { mutableStateOf(component?.min ?: 0) }
    var max by remember { mutableStateOf(component?.max ?: 100) }
    var step by remember { mutableStateOf(component?.step ?: 1) }
    var defaultValue by remember { mutableStateOf(component?.defaultValue ?: min) }
    
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        TextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = min.toString(),
                onValueChange = { min = it.toIntOrNull() ?: 0 },
                label = { Text("Min") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            TextField(
                value = max.toString(),
                onValueChange = { max = it.toIntOrNull() ?: 100 },
                label = { Text("Max") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        }
        
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = step.toString(),
                onValueChange = { step = it.toIntOrNull() ?: 1 },
                label = { Text("Step") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            TextField(
                value = defaultValue.toString(),
                onValueChange = { defaultValue = it.toIntOrNull() ?: min },
                label = { Text("Default") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Slider(
            value = defaultValue.toFloat(),
            onValueChange = { defaultValue = it.roundToInt() },
            valueRange = min.toFloat()..max.toFloat(),
            steps = if (max > min) ((max - min) / step) else 1,
            enabled = true
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { onConfirm(TestFormComponent.Slider(label, min, max, step, defaultValue)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Slider")
        }
    }
}