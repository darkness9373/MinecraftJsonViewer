package org.darkness.jsonuiview.ui.testform.builder.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
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
fun ToggleEditor(
    component: TestFormComponent.Toggle?,
    onConfirm: (TestFormComponent) -> Unit
) {
    var label by remember { mutableStateOf(component?.label ?: "") }
    var defaultValue by remember { mutableStateOf(component?.defaultValue ?: false) }
    
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Default Value")
            Switch(
                checked = defaultValue,
                onCheckedChange = { defaultValue = it }
            )
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { onConfirm(TestFormComponent.Toggle(label, defaultValue)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Toggle")
        }
    }
}