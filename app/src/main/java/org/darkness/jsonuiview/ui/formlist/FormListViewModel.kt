package org.darkness.jsonuiview.ui.formlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.data.repository.FormRepository
import android.net.Uri

class FormListViewModel(
    private val repository: FormRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    fun getForms(uri: Uri) = repository.getFormsBySourceFile(uri.toString())
    
    fun parseFile(uri: Uri, contentResolver: android.content.ContentResolver) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.parseAndCacheFile(uri, contentResolver)
            _isLoading.value = false
        }
    }
}