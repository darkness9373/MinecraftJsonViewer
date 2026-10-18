package org.darkness.jsonuiview.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.data.repository.FormRepository

class FormViewerViewModel(
    private val repository: FormRepository
) : ViewModel() {
    private val _form = MutableStateFlow<ResolvedForm?>(null)
    val form = _form.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    val showJsonSheet = MutableStateFlow(false)
    
    fun getForm(formId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val form = repository.getFormById(formId)
            _form.value = form
            _isLoading.value = false
        }
    }
}