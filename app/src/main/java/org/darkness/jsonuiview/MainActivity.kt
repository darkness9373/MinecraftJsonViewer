package org.darkness.jsonuiview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.darkness.jsonuiview.navigation.AppNavHost
import org.darkness.jsonuiview.ui.theme.Theme
import org.darkness.jsonuiview.util.PreferencesManager

class MainActivity : ComponentActivity() {
    private val preferencesManager: PreferencesManager by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by remember { mutableStateOf(preferencesManager.themeMode) }
            Theme(themeMode = themeMode) {
                Surface {
                    AppNavHost(
                        onThemeChange = { newMode ->
                            preferencesManager.themeMode = newMode
                        }
                    )
                }
            }
        }
    }
}