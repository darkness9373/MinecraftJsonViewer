package org.darkness.jsonuiview.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.data.repository.FormRepository
import org.darkness.jsonuiview.util.FileUtils
import org.darkness.jsonuiview.util.RecentFileItem

class MainViewModel(
    private val repository: FormRepository
) : ViewModel() {
    private val _recentFiles = MutableStateFlow<List<RecentFileItem>>(emptyList())
    val recentFiles = _recentFiles.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        loadRecentFiles()
    }
    
    private fun loadRecentFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRecentFiles().collect { entities ->
                _recentFiles.value = entities.map { RecentFileItem(it.uri, it.displayName, it.lastOpened) }
                _isLoading.value = false
            }
        }
    }
}