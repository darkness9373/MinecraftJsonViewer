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
fun LabelEditor(
    component: TestFormComponent.Label?,
    onConfirm: (TestFormComponent) -> Unit
) {
    var text by remember { mutableStateOf(component?.text ?: "") }
    
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label Text") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(
            onClick = { onConfirm(TestFormComponent.Label(text)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Label")
        }
    }
}