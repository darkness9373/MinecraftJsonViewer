package org.darkness.jsonuiview.data.parser

import org.darkness.jsonuiview.data.model.jsonui.ServerFormJson
import org.darkness.jsonuiview.data.model.jsonui.UIControl
import org.darkness.jsonuiview.data.model.jsonui.UIElement

class ElementRegistry(private val serverForm: ServerFormJson) {
    val namespace: String = serverForm.namespace
    private val elements: MutableMap<String, UIElement> = mutableMapOf()
    private val controls: MutableMap<String, UIControl> = mutableMapOf()

    init {
        indexElement("main_screen_content", serverForm.mainScreenContent?.let { UIControl("main_screen_content", it) })
        indexElement("long_form", serverForm.longForm?.let { UIControl("long_form", it) })
        indexElement("custom_form", serverForm.customForm?.let { UIControl("custom_form", it) })
        indexElement("form_buttons", serverForm.formButtons?.let { UIControl("form_buttons", it) })
        indexElement("custom_dropdown", serverForm.customDropdown?.let { UIControl("custom_dropdown", it) })
        
        serverForm.customElements.forEach { (name, element) ->
            indexElement(name, UIControl(name, element))
        }
        
        indexRecursive(serverForm.mainScreenContent)
        indexRecursive(serverForm.longForm)
        indexRecursive(serverForm.customForm)
        indexRecursive(serverForm.formButtons)
        indexRecursive(serverForm.customDropdown)
        serverForm.customElements.values.forEach { indexRecursive(it) }
    }

    private fun indexElement(name: String, control: UIControl?) {
        control?.let {
            elements[name] = it.element
            controls[name] = it
            it.element.dynamicProps["__element_name"] = name
        }
    }

    private fun indexRecursive(element: UIElement?) {
        element?.controls?.forEach { control ->
            indexElement(control.name, control)
            indexRecursive(control.element)
        }
    }

    fun findElement(name: String): UIElement? = elements[name]

    fun findControl(name: String): UIControl? = controls[name]

    fun findElementInControls(name: String): UIElement? {
        return controls[name]?.element
    }

    fun getAllElements(): Map<String, UIElement> = elements.toMap()

    fun addElement(name: String, element: UIElement) {
        elements[name] = element
    }

    fun addControl(name: String, control: UIControl) {
        controls[name] = control
        elements[name] = control.element
    }
}