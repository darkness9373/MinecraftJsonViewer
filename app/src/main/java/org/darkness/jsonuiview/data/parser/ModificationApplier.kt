package org.darkness.jsonuiview.data.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.darkness.jsonuiview.data.model.jsonui.Binding
import org.darkness.jsonuiview.data.model.jsonui.Modification
import org.darkness.jsonuiview.data.model.jsonui.UIControl
import org.darkness.jsonuiview.data.model.jsonui.UIElement
import org.darkness.jsonuiview.util.Logger

class ModificationApplier {
    private val logger = Logger("ModificationApplier")
    private val json = Json { ignoreUnknownKeys = true }

    fun apply(element: UIElement, registry: ElementRegistry): UIElement {
        var current = element.copy()
        
        current.modifications?.forEach { mod ->
            current = applyModification(current, mod, registry)
        }
        
        current.controls?.forEachIndexed { index, control ->
            val modifiedControl = apply(control.element, registry)
            if (modifiedControl !== control.element) {
                current = current.copy(
                    controls = current.controls?.toMutableList()?.also { 
                        it[index] = control.copy(element = modifiedControl) 
                    }
                )
            }
        }
        
        return current
    }

    private fun applyModification(element: UIElement, mod: Modification, registry: ElementRegistry): UIElement {
        return when (mod.arrayName) {
            "controls" -> applyControlsModification(element, mod, registry)
            "bindings" -> applyBindingsModification(element, mod)
            else -> {
                logger.w("Unknown array_name: ${mod.arrayName}")
                element
            }
        }
    }

    private fun applyControlsModification(element: UIElement, mod: Modification, registry: ElementRegistry): UIElement {
        val controls = element.controls?.toMutableList() ?: mutableListOf()
        val newControls = parseControls(mod.value, registry)
        
        return when (mod.operation) {
            "insert_back" -> element.copy(controls = controls + newControls)
            "insert_front" -> element.copy(controls = newControls + controls)
            "insert_after" -> insertAt(controls, newControls, mod.where?.bindingName, after = true)
            "insert_before" -> insertAt(controls, newControls, mod.where?.bindingName, after = false)
            "remove" -> element.copy(controls = controls.filter { it.name != mod.targetControl })
            "replace" -> replaceAt(controls, newControls, mod.targetControl)
            "move_back" -> moveControl(controls, mod.targetControl, toEnd = true)
            "move_front" -> moveControl(controls, mod.targetControl, toEnd = false)
            "swap" -> swapControls(controls, mod.targetControl, mod.where?.bindingName)
            else -> {
                logger.w("Unknown operation: ${mod.operation}")
                element
            }
        }
    }

    private fun applyBindingsModification(element: UIElement, mod: Modification): UIElement {
        val bindings = element.bindings?.toMutableList() ?: mutableListOf()
        val newBindings = parseBindings(mod.value)
        
        val modifiedBindings = when (mod.operation) {
            "insert_back" -> bindings + newBindings
            "insert_front" -> newBindings + bindings
            "insert_after" -> insertBindingsAt(bindings, newBindings, mod.where?.bindingName, after = true)
            "insert_before" -> insertBindingsAt(bindings, newBindings, mod.where?.bindingName, after = false)
            "remove" -> bindings.filter { it.bindingName != mod.targetControl }
            "replace" -> replaceBindingAt(bindings, newBindings, mod.targetControl)
            else -> bindings
        }
        
        return element.copy(bindings = modifiedBindings)
    }

    private fun parseControls(value: JsonElement?, registry: ElementRegistry): List<UIControl> {
        if (value == null) return emptyList()
        
        try {
            val jsonArray = value.jsonArray
            return jsonArray.map { obj ->
                val jsonObj = obj.jsonObject
                val entries = jsonObj.entries
                if (entries.size == 1) {
                    val (name, elementJson) = entries.first()
                    val element = json.decodeFromJsonElement<UIElement>(elementJson)
                    UIControl(name, element)
                } else {
                    UIControl("", json.decodeFromJsonElement<UIElement>(obj))
                }
            }
        } catch (e: Exception) {
            logger.w("Failed to parse controls: ${e.message}")
            return emptyList()
        }
    }

    private fun parseBindings(value: JsonElement?): List<Binding> {
        if (value == null) return emptyList()
        
        try {
            return json.decodeFromJsonElement<List<Binding>>(value)
        } catch (e: Exception) {
            logger.w("Failed to parse bindings: ${e.message}")
            return emptyList()
        }
    }

    private fun insertAt(
        controls: MutableList<UIControl>, 
        newControls: List<UIControl>, 
        targetName: String?, 
        after: Boolean
    ): UIElement {
        if (targetName == null) return UIElement(type = "panel", controls = controls + newControls)
        
        val index = controls.indexOfFirst { it.name == targetName }
        if (index >= 0) {
            val insertIndex = if (after) index + 1 else index
            val newList = controls.toMutableList()
            newControls.forEachIndexed { i, c -> newList.add(insertIndex + i, c) }
            return UIElement(type = "panel", controls = newList)
        }
        return UIElement(type = "panel", controls = controls + newControls)
    }

    private fun replaceAt(
        controls: MutableList<UIControl>, 
        newControls: List<UIControl>, 
        targetName: String?
    ): UIElement {
        if (targetName == null) return UIElement(type = "panel", controls = newControls)
        
        val index = controls.indexOfFirst { it.name == targetName }
        if (index >= 0) {
            val newList = controls.toMutableList()
            newList.removeAt(index)
            newControls.forEachIndexed { i, c -> newList.add(index + i, c) }
            return UIElement(type = "panel", controls = newList)
        }
        return UIElement(type = "panel", controls = controls + newControls)
    }

    private fun moveControl(
        controls: MutableList<UIControl>, 
        targetName: String?, 
        toEnd: Boolean
    ): UIElement {
        if (targetName == null) return UIElement(type = "panel", controls = controls)
        
        val index = controls.indexOfFirst { it.name == targetName }
        if (index >= 0) {
            val control = controls.removeAt(index)
            if (toEnd) controls.add(control) else controls.add(0, control)
            return UIElement(type = "panel", controls = controls)
        }
        return UIElement(type = "panel", controls = controls)
    }

    private fun swapControls(
        controls: MutableList<UIControl>, 
        targetName1: String?, 
        targetName2: String?
    ): UIElement {
        if (targetName1 == null || targetName2 == null) return UIElement(type = "panel", controls = controls)
        
        val index1 = controls.indexOfFirst { it.name == targetName1 }
        val index2 = controls.indexOfFirst { it.name == targetName2 }
        
        if (index1 >= 0 && index2 >= 0) {
            val newList = controls.toMutableList()
            val temp = newList[index1]
            newList[index1] = newList[index2]
            newList[index2] = temp
            return UIElement(type = "panel", controls = newList)
        }
        return UIElement(type = "panel", controls = controls)
    }

    private fun insertBindingsAt(
        bindings: MutableList<Binding>, 
        newBindings: List<Binding>, 
        targetName: String?, 
        after: Boolean
    ): List<Binding> {
        if (targetName == null) return bindings + newBindings
        
        val index = bindings.indexOfFirst { it.bindingName == targetName }
        if (index >= 0) {
            val insertIndex = if (after) index + 1 else index
            val newList = bindings.toMutableList()
            newBindings.forEachIndexed { i, b -> newList.add(insertIndex + i, b) }
            return newList
        }
        return bindings + newBindings
    }

    private fun replaceBindingAt(
        bindings: MutableList<Binding>, 
        newBindings: List<Binding>, 
        targetName: String?
    ): List<Binding> {
        if (targetName == null) return newBindings
        
        val index = bindings.indexOfFirst { it.bindingName == targetName }
        if (index >= 0) {
            val newList = bindings.toMutableList()
            newList.removeAt(index)
            newBindings.forEachIndexed { i, b -> newList.add(index + i, b) }
            return newList
        }
        return bindings + newBindings
    }
}