package org.darkness.jsonuiview.data.parser

import org.darkness.jsonuiview.data.model.jsonui.Binding
import org.darkness.jsonuiview.data.model.jsonui.UIElement
import org.darkness.jsonuiview.util.Logger

class BindingEvaluator {
    private val logger = Logger("BindingEvaluator")

    fun evaluate(element: UIElement, registry: ElementRegistry, parentContext: Map<String, Any> = emptyMap()): Map<String, Any> {
        val context = mutableMapOf<String, Any>()
        context.putAll(parentContext)
        
        collectBindings(element, registry, context)
        evaluateViewBindings(element, registry, context)
        
        return context
    }

    private fun collectBindings(element: UIElement, registry: ElementRegistry, context: MutableMap<String, Any>) {
        element.bindings?.forEach { binding ->
            when (binding.bindingType) {
                "global", "collection", "collection_details" -> {
                    if (binding.bindingName != null) {
                        val value = resolveBindingValue(binding, registry, context)
                        context[binding.bindingName] = value
                    }
                }
                "none" -> {
                    if (binding.bindingName != null) {
                        context[binding.bindingName] = binding.sourcePropertyName
                    }
                }
            }
        }
        
        element.controls?.forEach { control ->
            collectBindings(control.element, registry, context)
        }
    }

    private fun evaluateViewBindings(element: UIElement, registry: ElementRegistry, context: Map<String, Any>) {
        element.bindings?.filter { it.bindingType == "view" }?.forEach { binding ->
            val value = evaluateExpression(binding.sourcePropertyName, context)
            val targetProp = binding.targetPropertyName ?: "#visible"
            context[targetProp] = value
        }
        
        element.controls?.forEach { control ->
            evaluateViewBindings(control.element, registry, context)
        }
    }

    private fun resolveBindingValue(binding: Binding, registry: ElementRegistry, context: Map<String, Any>): Any? {
        return when {
            binding.bindingName?.startsWith("#") == true -> {
                context[binding.bindingName]
            }
            binding.sourceControlName != null && binding.sourcePropertyName != null -> {
                val sourceElement = registry.findControl(binding.sourceControlName!!)?.element
                sourceElement?.let { evaluateExpression(binding.sourcePropertyName!!, context) }
            }
            binding.sourcePropertyName != null -> {
                evaluateExpression(binding.sourcePropertyName!!, context)
            }
            else -> null
        }
    }

    fun evaluateExpression(expr: String?, context: Map<String, Any>): Any? {
        if (expr == null || expr.isBlank()) return null
        
        try {
            return ExpressionParser.parse(expr.trim(), context)
        } catch (e: Exception) {
            logger.w("Failed to evaluate expression: $expr - ${e.message}")
            return null
        }
    }
}

object ExpressionParser {
    fun parse(expr: String, context: Map<String, Any>): Any? {
        val trimmed = expr.trim()
        
        if (trimmed.startsWith("not(") && trimmed.endsWith(")")) {
            val inner = trimmed.substring(4, trimmed.length - 1)
            val result = parse(inner, context)
            return when (result) {
                is Boolean -> !result
                is String -> result != "true"
                else -> result == null || result == false
            }
        }
        
        if (trimmed.contains(" and ")) {
            val parts = trimmed.split(" and ")
            return parts.all { parse(it.trim(), context) as Boolean? == true }
        }
        
        if (trimmed.contains(" or ")) {
            val parts = trimmed.split(" or ")
            return parts.any { parse(it.trim(), context) as Boolean? == true }
        }
        
        if (trimmed.contains(" = ")) {
            val parts = trimmed.split(" = ")
            if (parts.size == 2) {
                val left = resolveValue(parts[0].trim(), context)
                val right = resolveValue(parts[1].trim(), context)
                return left == right
            }
        }
        
        if (trimmed.contains(" != ")) {
            val parts = trimmed.split(" != ")
            if (parts.size == 2) {
                val left = resolveValue(parts[0].trim(), context)
                val right = resolveValue(parts[1].trim(), context)
                return left != right
            }
        }
        
        return resolveValue(trimmed, context)
    }

    private fun resolveValue(token: String, context: Map<String, Any>): Any? {
        val trimmed = token.trim()
        
        if (trimmed.startsWith("#")) {
            return context[trimmed]
        }
        
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        
        return try {
            trimmed.toInt()
        } catch (e: NumberFormatException) {
            try {
                trimmed.toDouble()
            } catch (e: NumberFormatException) {
                if (trimmed == "true") true
                else if (trimmed == "false") false
                else trimmed
            }
        }
    }
}