package org.darkness.jsonuiview.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.data.repository.FormRepository
import org.darkness.jsonuiview.util.PreferencesManager

class SettingsViewModel(
    private val repository: FormRepository
) : ViewModel() {
    private val _themeMode = MutableStateFlow(PreferencesManager.ThemeMode.System)
    val themeMode = _themeMode.asStateFlow()
    
    init {
        // Theme mode is managed by PreferencesManager
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            repository.database.formDao().deleteAllForms()
            repository.database.recentFileDao().clearRecentFiles()
        }
    }
}