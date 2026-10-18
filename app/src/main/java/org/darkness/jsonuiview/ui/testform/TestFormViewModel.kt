package org.darkness.jsonuiview.ui.testform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.darkness.jsonuiview.data.model.export.TestFormComponent
import org.darkness.jsonuiview.data.model.export.TestFormState
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.data.repository.FormRepository

class TestFormViewModel(
    private val repository: FormRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TestFormState())
    val uiState = _uiState.asStateFlow()
    
    init {
        // If formId is provided, load existing form
    }
    
    fun loadForm(formId: String) {
        viewModelScope.launch {
            val form = repository.getFormById(formId)
            form?.let { f ->
                _uiState.update { state ->
                    state.copy(
                        title = f.title,
                        components = f.components.map { convertToTestComponent(it) }
                    )
                }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun addComponent(component: TestFormComponent) {
        _uiState.update { it.copy(components = it.components + component) }
    }
    
    fun updateComponent(index: Int, component: TestFormComponent) {
        _uiState.update { state ->
            val newComponents = state.components.toMutableList()
            if (index in newComponents.indices) {
                newComponents[index] = component
            }
            state.copy(components = newComponents)
        }
    }
    
    fun deleteComponent(index: Int) {
        _uiState.update { state ->
            val newComponents = state.components.toMutableList()
            if (index in newComponents.indices) {
                newComponents.removeAt(index)
            }
            state.copy(components = newComponents)
        }
    }
    
    fun reorderComponents(from: Int, to: Int) {
        _uiState.update { state ->
            val newComponents = state.components.toMutableList()
            if (from in newComponents.indices && to in newComponents.indices) {
                val item = newComponents.removeAt(from)
                newComponents.add(to, item)
            }
            state.copy(components = newComponents)
        }
    }
    
    private fun convertToTestComponent(component: org.darkness.jsonuiview.data.model.form.FormComponent): TestFormComponent {
        return when (component) {
            is org.darkness.jsonuiview.data.model.form.FormComponent.Button ->
                TestFormComponent.Button(component.text, component.iconPath)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Label ->
                TestFormComponent.Label(component.text)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Header ->
                TestFormComponent.Header(component.text)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Divider ->
                TestFormComponent.Divider
            is org.darkness.jsonuiview.data.model.form.FormComponent.TextField ->
                TestFormComponent.TextField(component.label, component.placeholder, component.defaultValue)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Dropdown ->
                TestFormComponent.Dropdown(component.label, component.options, component.defaultIndex)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Slider ->
                TestFormComponent.Slider(component.label, component.min, component.max, component.step, component.defaultValue)
            is org.darkness.jsonuiview.data.model.form.FormComponent.Toggle ->
                TestFormComponent.Toggle(component.label, component.defaultValue)
            else ->
                TestFormComponent.Label("Unknown component")
        }
    }
}