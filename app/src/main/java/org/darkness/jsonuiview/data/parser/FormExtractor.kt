package org.darkness.jsonuiview.data.parser

import org.darkness.jsonuiview.data.model.form.FormComponent
import org.darkness.jsonuiview.data.model.form.FormType
import org.darkness.jsonuiview.data.model.jsonui.UIElement
import org.darkness.jsonuiview.util.Logger

class FormExtractor {
    private val logger = Logger("FormExtractor")

    fun extract(element: UIElement, formType: FormType, bindingContext: Map<String, Any>): List<FormComponent> {
        val components = mutableListOf<FormComponent>()
        extractRecursive(element, components, formType, bindingContext)
        return components
    }

    private fun extractRecursive(
        element: UIElement, 
        components: MutableList<FormComponent>, 
        formType: FormType,
        bindingContext: Map<String, Any>
    ) {
        val visible = (bindingContext["#visible"] as? Boolean) ?: true
        if (!visible) return

        when (element.type) {
            "button" -> extractButton(element, components)
            "label" -> extractLabel(element, components)
            "header" -> extractHeader(element, components)
            "divider" -> components.add(FormComponent.Divider)
            "text_field" -> extractTextField(element, components)
            "dropdown" -> extractDropdown(element, components)
            "slider" -> extractSlider(element, components)
            "toggle" -> extractToggle(element, components)
            "panel", "stack_panel", "collection_panel", "factory", "grid_panel" -> {
                element.controls?.forEach { control ->
                    extractRecursive(control.element, components, formType, bindingContext)
                }
            }
            else -> {
                if (element.dynamicProps.isNotEmpty() || element.text != null || element.texture != null) {
                    components.add(FormComponent.Custom(
                        element.type ?: "unknown",
                        element.dynamicProps.toMap()
                    ))
                }
                element.controls?.forEach { control ->
                    extractRecursive(control.element, components, formType, bindingContext)
                }
            }
        }
    }

    private fun extractButton(element: UIElement, components: MutableList<FormComponent>) {
        val text = element.text ?: element.dynamicProps["text"] as? String ?: ""
        val icon = element.dynamicProps["icon"] as? String
            ?: element.dynamicProps["icon_path"] as? String
            ?: element.texture
        components.add(FormComponent.Button(text, icon))
    }

    private fun extractLabel(element: UIElement, components: MutableList<FormComponent>) {
        val text = element.text ?: element.dynamicProps["text"] as? String ?: ""
        components.add(FormComponent.Label(text))
    }

    private fun extractHeader(element: UIElement, components: MutableList<FormComponent>) {
        val text = element.text ?: element.dynamicProps["text"] as? String ?: ""
        components.add(FormComponent.Header(text))
    }

    private fun extractTextField(element: UIElement, components: MutableList<FormComponent>) {
        val label = element.dynamicProps["label"] as? String ?: ""
        val placeholder = element.dynamicProps["placeholder"] as? String ?: ""
        val defaultValue = element.dynamicProps["default_value"] as? String
        components.add(FormComponent.TextField(label, placeholder, defaultValue))
    }

    private fun extractDropdown(element: UIElement, components: MutableList<FormComponent>) {
        val label = element.dynamicProps["label"] as? String ?: ""
        val options = (element.dynamicProps["options"] as? List<*>)?.map { it.toString() } ?: emptyList()
        val defaultIndex = element.dynamicProps["default_index"] as? Int
        components.add(FormComponent.Dropdown(label, options, defaultIndex))
    }

    private fun extractSlider(element: UIElement, components: MutableList<FormComponent>) {
        val label = element.dynamicProps["label"] as? String ?: ""
        val min = (element.dynamicProps["min"] as? Number)?.intValue() ?: 0
        val max = (element.dynamicProps["max"] as? Number)?.intValue() ?: 100
        val step = (element.dynamicProps["step"] as? Number)?.intValue() ?: 1
        val defaultValue = (element.dynamicProps["default_value"] as? Number)?.intValue()
        components.add(FormComponent.Slider(label, min, max, step, defaultValue))
    }

    private fun extractToggle(element: UIElement, components: MutableList<FormComponent>) {
        val label = element.dynamicProps["label"] as? String ?: ""
        val defaultValue = element.dynamicProps["default_value"] as? Boolean
        components.add(FormComponent.Toggle(label, defaultValue))
    }
}