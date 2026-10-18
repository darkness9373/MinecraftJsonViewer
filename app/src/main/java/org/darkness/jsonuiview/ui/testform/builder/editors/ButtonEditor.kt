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
fun ButtonEditor(
    component: TestFormComponent.Button?,
    onConfirm: (TestFormComponent) -> Unit
) {
    var text by remember { mutableStateOf(component?.text ?: "") }
    var iconPath by remember { mutableStateOf(component?.iconPath ?: "") }
    
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Button Text") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        TextField(
            value = iconPath,
            onValueChange = { iconPath = it },
            label = { Text("Icon Path (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("e.g., textures/items/diamond") }
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { onConfirm(TestFormComponent.Button(text, iconPath.ifBlank { null })) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Button")
        }
    }
}